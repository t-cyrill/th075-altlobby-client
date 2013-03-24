package jp.dip.th075altlobby.imo.Window.MainWindow;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JOptionPane;

import jp.dip.th075altlobby.imo.CasterAdapter.IOStreamCasterAdapterWithOutputCallback;
import jp.dip.th075altlobby.imo.Constants.ClientConstants;
import jp.dip.th075altlobby.imo.Data.IPConverter.IPConverter;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ClientSetting;
import jp.dip.th075altlobby.imo.Data.communication.AbstractOutputThread;
import jp.dip.th075altlobby.imo.Data.communication.ClientToClientCommand;
import jp.dip.th075altlobby.imo.Data.communication.CommunicationDataInput;
import jp.dip.th075altlobby.imo.Data.communication.DataSplitter;
import jp.dip.th075altlobby.imo.Data.communication.DataWrapper;
import jp.dip.th075altlobby.imo.Data.communication.InstantMessage;
import jp.dip.th075altlobby.imo.Data.communication.ResponseStoredInstantMessages;
import jp.dip.th075altlobby.imo.Data.communication.StateModifiedInfo;
import jp.dip.th075altlobby.imo.Data.communication.UserInfo;
import jp.dip.th075altlobby.imo.Data.communication.VersionResponse;
import jp.dip.th075altlobby.imo.Data.communication.WrappedData;
import jp.dip.th075altlobby.imo.sound.ClipPlayer;

/**
 * クライアントのメインスレッド
 * @author Cyrill
 */
public class ClientThread implements Runnable {
	private final Socket _socket;
	private final AbstractOutputThread out;
	private IOStreamCasterAdapterWithOutputCallback caster = null;
	private final AtomicInteger ioErrorCounter = new AtomicInteger(0);
	private final AtomicInteger myUserState = new AtomicInteger(UserInfo.WAITING);
	private final static Logger logger = Logger.getLogger("jp.dip.th075altlobby.imo.Application");
	private final ClientSetting _setting;
	private final MainWindow _mainWindow;
	private final ClientThread _ioThread;
	private final Map<String, String> messageMap;
	private final AtomicInteger threadState = new AtomicInteger(0);
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
	private Future<Integer> socketCloserTask = null;
	private volatile int exitStatus = 0;

	public ClientThread(MainWindow mainWindow, Socket s, ClientSetting setting) throws IOException {
		_socket = s;
		_mainWindow = mainWindow;
		_ioThread = this;
		_setting = setting;
		messageMap = mainWindow.getMessageMap();
		out = new OutputThread(s.getOutputStream());
	}
	
	/**
	 * ユーザ状態をホスト募集中に対する接続待ちの状態にする
	 * 
	 * この状態は特別な状態です。
	 * ホスト接続待ちユーザに対する接続要求時以外に呼び出すべきではありません。
	 * このメソッドは外部からユーザステートを変更する唯一の方法です。
	 */
	public void setUserStateHostGatheringWait() {
		myUserState.set(UserInfo.HOST_GATHERING_WAIT);
	}

	/**
	 * <h2>Casterプロセスへの書き出し機能を提供する</h2>
	 * <p>このメソッドは捕捉しているcasterプロセスへの書き出し機能を提供します。casterを捕捉していない場合、このメソッドは何も実行しません。</p>
	 * <p>このメソッドは柔軟な操作を可能としますが注意して利用する必要があります。詳細は{@link IOStreamCasterAdapterWithOutputCallback#writeCasterStream(String)}を参照してください。</p>
	 *
	 * @param s 書き出す文字列
	 * @since rev31
	 */
	public void writeCasterStream(String s) {
		if (caster != null)
			caster.writeCasterStream(s);
	}

	public void fireSelectedWatchEvent(int i) {
		caster.fireSelectedWatchEvent(i);
	}

	public void fireInputedMarginEvent(Integer delay) {
		caster.fireInputedMarginEvent(delay);
	}

	public void fireSelectedEvent(int selected) {
		caster.fireSelectedEvent(selected);
	}

	private void scheduleSocketCloserTask() {
		synchronized (this) {
			if (socketCloserTask != null)
				socketCloserTask.cancel(true);

			socketCloserTask = scheduler.schedule(new Callable<Integer>() {
				@Override
				public Integer call() throws Exception {
					_socket.close();
					return 0;
				}
			}, ClientConstants.SOCKET_CLOSE_TIME_DELAY, TimeUnit.SECONDS);
		}
	}

	@Override
	public void run() {
		ExecutorService exs = Executors.newCachedThreadPool();
		try {
			InputThread in = new InputThread(_socket, _socket.getInputStream(), out);

			exs.submit(in);
			exs.submit(out);

			scheduler.scheduleAtFixedRate(new Runnable() {
				@Override
				public void run() {
					_ioThread.put(new WrappedData(WrappedData.KEEP_ALIVE, new byte[0]));
				}
			}, ClientConstants.KEEP_ALIVE_TIME_FIRST_DELAY, ClientConstants.KEEP_ALIVE_TIME_DELAY, TimeUnit.SECONDS);

			// 自動切断タスクの起動
			scheduleSocketCloserTask();

			try {
				exs.shutdown();
				// タイムアウトを実質無効にする
				exs.awaitTermination(Integer.MAX_VALUE, TimeUnit.DAYS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.log(Level.INFO, "終了ブロックがインタラプトされました。");
			}

		} catch (IOException e) {
			logger.log(Level.WARNING, "IO例外が発生しました。", e);
			shutdown();
			exs.shutdownNow();
		} finally {
			scheduler.shutdownNow();
		}

		logger.log(Level.INFO, "ClientThreadを終了します。");
		if (ioErrorCounter.get() > 0) {
			// IOErrorによる終了？
			logger.log(Level.WARNING, "IO例外によりスレッドが強制的に終了されました。");
		}
		_mainWindow.putExitStatus(exitStatus);
	}

	/**
	 * 送信するdataをキューに追加する
	 *
	 * @param data
	 */
	public void put(WrappedData data) {
		out.put(data);
	}

	/**
	 * casterが起動していれば閉じます
	 *
	 */
	public void closeCaster() {
		if(caster != null)
			caster.close();
	}

	/**
	 * infoに格納されているIPアドレスとポート番号にCasterで接続します。
	 *
	 * @throws IOException プロセスの起動に失敗した場合
	 */
	public void connectCaster(UserInfo info) throws IOException {
		String ipString = IPConverter.toString(info.getIpAddress());
		int port = info.getCasterPort();
		closeCaster();
		logger.warning("接続可能です。接続します。");
		// caster = new IOStreamCasterAdapter(new CasterEventListener(), new CasterStateChangeListener(), setting.getCasterSetting().getCasterPath(), "-i" + ipString + " -p" + port);
		caster = new IOStreamCasterAdapterWithOutputCallback(_mainWindow.getCasterEventListener(), _mainWindow.getCasterStateChangeListener(),
				_mainWindow.getOutputCasterMessageCallback(), _setting.getCasterSetting().getCasterPath(), "-i" + ipString + " -p" + port);
	}

	/**
	 * ClientThreadを安全な形で終了します。
	 * このメソッドの呼び出しによって、socketがクローズされることはありません。
	 * socketのクローズは明示的に行う必要があります。
	 *
	 */
	public void shutdown() {
		logger.info("BYE Signalを送信します。");
		WrappedData bye = new WrappedData(WrappedData.BYE, new byte[0]);
		_ioThread.put(bye);
		_ioThread.closeCaster();
	}

	/**
	 * 入力処理を扱うスレッドの実装
	 * @author Cyrill
	 */
	class InputThread implements Runnable {
		private final Socket s;
		private final CommunicationDataInput _in;
		private final AbstractOutputThread _out;
		private boolean exitFlag = false;

		public InputThread(Socket s, InputStream in, AbstractOutputThread out) {
			this.s = s;
			_in = new CommunicationDataInput(in);
			_out = out;
		}

		@Override
		public void run() {
			threadState.addAndGet(1);
			try {
				try {
					while (!Thread.currentThread().isInterrupted()) {
						WrappedData data = _in.recieve();
						consume(data);
						if (exitFlag == true)
							break;
					}
				} catch (EOFException e) {
					exitStatus = -5;
					logger.log(Level.SEVERE, "ソケットが閉じられています。", e);
					_out.put(new WrappedData(WrappedData.EXIT_DUMMY, new byte[0]));
				} catch (IOException e) {
					ioErrorCounter.incrementAndGet();
					logger.log(Level.SEVERE, "InputThreadでIO例外をキャッチしました。", e);
					try {
						_in.close();
					} catch (IOException e1) {
						logger.log(Level.SEVERE, "ストリームを閉じられませんでした。", e1);
						ioErrorCounter.incrementAndGet();
					}

					logger.warning("InputThreadが異常終了しました。OutputThreadの実行を停止します。");
					exitStatus = -4;
					_out.put(new WrappedData(WrappedData.EXIT_DUMMY, new byte[0]));
				}
			} catch (RuntimeException e) {
				logger.log(Level.SEVERE, "RuntimeExceptionをcatchしました。処理を続行できません。以下にトレースを示します。", e);
			}
			logger.info("InputThreadを終了します。");
			threadState.addAndGet(-1);
		}

		/**
		 * 受け取ったデータを消費する
		 *
		 * @param data 消費するWrappedData
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void consume(WrappedData data) throws IOException {
			Byte command = data.getCommand();
			byte[] rawData = data.getRawData();
			switch (command) {
			case WrappedData.RESPONSE_VERSION:
				checkVersion(rawData);
				break;
			case WrappedData.RESPONSE_BYE:
				bye();
				break;
			case WrappedData.RESPONSE_NEW_CONNECT:
				newConnectRes(rawData);
				break;
			case WrappedData.JOIN:
				// 新規接続があった場合
				newConnected(rawData);
				break;
			case WrappedData.RESPONSE_USERLIST:
				// ユーザー一覧の結果
				responceUserlist(rawData);
				break;
			case WrappedData.RESPONSE_MODIFY_STATE:
				responseModifyState(rawData);
				break;
			case WrappedData.STATE_MODIFIED:
				stateModified(rawData);
				break;
			case WrappedData.DISTRIBUTE_INSTANTMESSAGE:
				postedIM(rawData);
				break;
			case WrappedData.RESPONSE_STORED_INSTANTMESSAGES:
				storedIM(rawData);
				break;
			case WrappedData.DISTRIBUTED_CLIENT_TO_CLIENT_COMMAND:
				distributedClientToClientCommand(rawData);
				break;
			case WrappedData.LEAVE:
				leave(rawData);
				break;
			case WrappedData.RESPONSE_KEEP_ALIVE:
				keepalive();
				break;
			default:
				logger.warning("サーバーから未知のコマンドが届きました。command = " + command + "はサポートされていません。");
			}
		}

		/**
		 * 接続を継続する.
		 */
		private void keepalive() {
			scheduleSocketCloserTask();
		}

		/**
		 * 切断されたユーザーの情報を処理する
		 *
		 * @param rawData サーバーから送られたバイト列
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void leave(byte[] rawData) throws IOException {
			String UID = DataSplitter.toString(rawData);
			UserInfo info = _mainWindow.getUserInfoByUID(UID);
			if (info != null)
				_mainWindow.removeUserInfo(info);
		}

		/**
		 * <h1>responseModifyState</h1>
		 * <h2>ModifyStateの結果を処理する</h2>
		 * <p>サーバーから送信された状態変更の結果を処理します。</p>
		 * <p>このメソッドは{@link MessageInputPanel#switchButton(int)}を呼び出しボタンの状態を変更し、
		 * {@link MessageInputPanel#setUserStateTextLabelState(byte)}を呼び出しテキストの変更を行った後、
		 * {@link MainWindow#myUserState}の値をアトミックに変更します。</p>
		 *
		 * @param rawData サーバーから送られたバイト列
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void responseModifyState(byte[] rawData) throws IOException {
			byte state = DataSplitter.toByte(rawData);
			_mainWindow.setMyStateControls(state);
			myUserState.set(state);
		}

		/**
		 * <h1>distributedClientToClientCommand</h1>
		 * <h2>サーバーから配信されたクライアント間コマンドの処理をする</h2>
		 * <p>サーバーから送られたクライアント間コマンドを処理します。</p>
		 *
		 * @param rawData サーバーから送られたバイト列
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void distributedClientToClientCommand(byte[] rawData) throws IOException {
			ClientToClientCommand ccc = DataSplitter.toDistributedClientToClientCommand(rawData);

			UserInfo info = _mainWindow.getUserInfoByUID(ccc.getFromUID());
			switch (ccc.getCommand()) {
			case ClientToClientCommand.ACCEPTED_POST_FIGHT:
				// 対戦が了承された場合
				if (info == null) return;

				try {
					connectCaster(info);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(
						null,
						"Caster起動時にエラーが発生しました。ログにトレースを出力します。",
						"IOException", JOptionPane.ERROR_MESSAGE
					);
					logger.log(Level.WARNING, "connectCasterでIO例外をcatchしました。Casterを起動できません。以下にトレースを示します。", e);
				}
				break ;
			case ClientToClientCommand.POST_FIGHT:
				// 対戦の要求があった場合

				// NGUserに含まれれば拒否する
				if (_mainWindow.isNGUser(ccc.getFromUID()))
					break ;

				// 募集中なら要求を受ける
				switch (myUserState.get()) {
				case UserInfo.GATHERING:
				case UserInfo.HOST_GATHERING_WAIT:
					try {
						closeCaster();
						String IPString = IPConverter.toString(info.getIpAddress());
						// caster = new IOStreamCasterAdapter(new CasterEventListener(), new CasterStateChangeListener(), setting.getCasterSetting().getCasterPath(), "-w");
						caster = new IOStreamCasterAdapterWithOutputCallback(
							_mainWindow.getCasterEventListener(),
							_mainWindow.getCasterStateChangeListener(),
							_mainWindow.getOutputCasterMessageCallback(),
							_setting.getCasterSetting().getCasterPath(), "-i"+IPString, "-w"
						);

						_ioThread.put(
							new WrappedData(
								WrappedData.POST_CLIENT_TO_CLIENT_COMMAND,
								DataWrapper.wrapPostClientToClientCommand(
									ccc.getFromUID(), ClientToClientCommand.ACCEPTED_POST_FIGHT)
								)
						);

						logger.info("Casterを起動しました");
						_mainWindow.putSystemMessage("System", info.getPublicName() + "@" + info.getUID().substring(0, 3) +
							messageMap.get("main.message.connected"));
					} catch (IOException e) {
						JOptionPane.showMessageDialog(
							null,
							"Caster起動時にエラーが発生しました。ログにトレースを出力します。",
							"IOException", JOptionPane.ERROR_MESSAGE
						);
						logger.log(Level.WARNING, "接続要求を受けましたがCasterの起動に失敗しました。" +
								"対戦できない旨を返します。以下にトレースを示します。", e);
						_ioThread.put(new WrappedData(WrappedData.POST_CLIENT_TO_CLIENT_COMMAND,
								DataWrapper.wrapPostClientToClientCommand(ccc.getFromUID(), ClientToClientCommand.PROCESS_START_ERROR)));
					}
					break;
				case UserInfo.HOST_GATHERING:
					logger.info("HostGatheringに対して対戦要求がありました。");

					_ioThread.put(
						new WrappedData(
							WrappedData.POST_CLIENT_TO_CLIENT_COMMAND,
							DataWrapper.wrapPostClientToClientCommand(
								ccc.getFromUID(),
								ClientToClientCommand.POST_FIGHT
							)
						)
					);

					_mainWindow.putSystemMessage(
						"System",
						info.getPublicName() + "@" + info.getUID().substring(0, 3) + messageMap.get("main.message.connected")
					);

					break;
				default :
					_ioThread.put(
						new WrappedData(
							WrappedData.POST_CLIENT_TO_CLIENT_COMMAND,
							DataWrapper.wrapPostClientToClientCommand(
								ccc.getFromUID(),
								ClientToClientCommand.NON_GATHERING)
							)
					);
					break;
				}
				break ;
			}
		}

		/**
		 * <h1>storedIM</h1>
		 * <h2>サーバーに保存されたIMを処理する</h2>
		 * <p>サーバーに保存されているIMを処理します。</p>
		 * <p>サーバーは要求を拒否することがあります。</p>
		 *
		 * @param rawData サーバーから送られたバイト列
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void storedIM(byte[] rawData) throws IOException {
			ResponseStoredInstantMessages im = DataSplitter.toResponseStoredInstantMessages(rawData);
			switch(im.getResponseCode()){
			case WrappedData.ACCEPTED:
				logger.log(Level.INFO, "正しく結果が返りました。");
				Queue<InstantMessage> ims = im.getInstantMessagesQueue();
				while (true) {
					InstantMessage current = ims.poll();
					if(current == null)
						break;
					_mainWindow.addInstantMessage(current, ims.isEmpty());
				}
				break;
			case WrappedData.REFUSED:
				logger.log(Level.INFO, "要求が拒否されました。");
				break;
			case WrappedData.NOT_EXISTS:
				logger.log(Level.INFO, "データが存在しません。");
				break;
			case WrappedData.IO_ERROR:
				logger.log(Level.INFO, "サーバー側でIOエラーが発生しました。");
				break;
			default:
				logger.log(Level.INFO, im.getResponseCode() + "は未知の応答です。");
			}
		}

		/**
		 * <h1>postedIM</h1>
		 * <h2>ポストされたIMを処理する</h2>
		 * <p>サーバーから送られたIM情報を処理します。</p>
		 *
		 * @param rawData サーバーから送られたバイト列
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void postedIM(byte[] rawData) throws IOException {
			InstantMessage im = DataSplitter.toDistributedIM(rawData);
			_mainWindow.addInstantMessage(im);

			// 対戦中に音を鳴らさない + 対戦中
			if (_setting.getImsoundSetting().getNonIMSoundIfPlaying() && (myUserState.get() == UserInfo.FIGHTING)) return ;

			// フォーカスがあるときに音を鳴らさない + フォーカスがある
			if (_setting.getImsoundSetting().getNonIMPlayingIfWindowFocused() && _mainWindow.isActive()) return ;

			if(im.getTo().equals("all")) {
				// 全体IM
				if(_setting.getImsoundSetting().getWholeIMPlaySound()) {
					try {
						ClipPlayer player = new ClipPlayer(_setting.getImsoundSetting().getWavePath());
						player.play();
					} catch (LineUnavailableException e) {
						logger.warning("LineUnavailableException");
					} catch (UnsupportedAudioFileException e) {
						logger.warning("全体IM再生ファイルはサポートされない形式です。");
					} catch (FileNotFoundException e) {
						logger.warning("全体IM再生ファイルのオープンに失敗しました。");
					}
				}
			} else {
				// 個別IM
				try {
					ClipPlayer player = new ClipPlayer(_setting.getImsoundSetting().getToMeWavePath());
					player.play();
				} catch (LineUnavailableException e) {
					logger.warning("LineUnavailableException");
				} catch (UnsupportedAudioFileException e) {
					logger.warning("指定IM再生ファイルはサポートされない形式です。");
				} catch (FileNotFoundException e) {
					logger.warning("指定IM再生ファイルのオープンに失敗しました。");
				}
			}
		}

		/**
		 * ユーザー状態変更の通知処理
		 *
		 * @param rawData 更新されたユーザーの情報が格納されているはずのバイト列
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void stateModified(byte[] rawData) throws IOException {
			StateModifiedInfo info = DataSplitter.toStateModifiedInfo(rawData);
			_mainWindow.updateUserInfo(info);
		}

		/**
		 * ユーザー一覧要求の結果
		 *
		 * @param rawData ユーザー一覧が格納されているはずのバイト列
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void responceUserlist(byte[] rawData) throws IOException {
			UserInfo[] usersArray = DataSplitter.toConnectedUsersInfo(rawData);
			_mainWindow.setUserInfos(usersArray);
			/*
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						for(int i=0;i < usersArray.length; i++) {
							userinfoTablePanel.addUserInfo(usersArray[i]);
						}
					}
				});
			 */
		}

		/**
		 * NEW_CONNECT要求の結果
		 *
		 * @param rawData サーバーから送られたバイト列
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void newConnectRes(byte[] rawData) throws IOException {
			if(DataSplitter.toByte(rawData).equals(WrappedData.ACCEPTED)){
				logger.log(Level.INFO, "新規接続要求が通りました。");
			} else {
				logger.log(Level.INFO, "新規接続要求が通りませんでした。");
			}
		}
		/**
		 * 新しくユーザーが追加されたことの通知
		 *
		 * @param rawData サーバーから送られたバイト列
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void newConnected(byte[] rawData) throws IOException {
			UserInfo info = DataSplitter.toNewConnectedUserInfo(rawData);
			_mainWindow.addUserInfo(info);
		}

		/**
		 * バージョンのチェック要求を処理する。
		 * このサーバーが対応しているプロトコルのバージョンとサーバー自身のバージョンを送信します。
		 *
		 * @param rawData サーバーから送られたバイト列
		 * @throws IOException 入出力例外が発生した場合
		 */
		private void checkVersion(byte[] b) throws IOException {
			VersionResponse ver = DataSplitter.toVersionResponse(b);
			logger.log(Level.INFO, "PROTOCOL_VERSION = " + ver.getProtocolVersion());
			logger.log(Level.INFO, "SERVER_VERSION = "+ ver.getServerVersion());
			if(ver.getProtocolVersion() != ClientConstants.PROTOCOL_VERSION) {
				exitStatus = -2;
				shutdown();
			}
		}

		private void bye() throws IOException {
			logger.log(Level.INFO, "Byeが通りました");
			exitFlag = true;
			_out.put(new WrappedData(WrappedData.EXIT_DUMMY, new byte[0]));
			s.close();
		}
	}

	/**
	 * 出力処理を扱うスレッドの実装
	 * @author Cyrill
	 */
	class OutputThread extends AbstractOutputThread {
		/**
		 * 出力ストリームを指定してOutputThreadを構築する
		 * @param out 対象となる出力ストリーム
		 */
		public OutputThread(OutputStream out) {
			super(out);
		}

		@Override
		public void run() {
			threadState.addAndGet(2);
			try {
				while (true) {
					// putによりデータが追加されるまでブロックされる
					WrappedData data = queue.take();

					if(data.getCommand() == WrappedData.EXIT_DUMMY) {
						logger.info("終了シグナルを拾いました");
						break;
					}

					// このインスタンスが保持している出力ストリームに対してデータを送る
					out.send(data);
				}
			} catch (IOException e) {
				ioErrorCounter.incrementAndGet();
				logger.log(Level.WARNING, "IO例外をキャッチしました。", e);
				try {
					out.close();
				} catch (IOException e1) {
					ioErrorCounter.incrementAndGet();
					logger.log(Level.WARNING, "出力ストリームのクローズに失敗しました。", e1);
				}
				exitStatus = -3;
			} catch (InterruptedException e) {
				logger.log(Level.INFO, "スレッドがインタラプトされました。");

				// インタラプトステートを復元する
				Thread.currentThread().interrupt();
			} finally {
				logger.info("OutputThreadを終了します。");
				threadState.addAndGet(-2);
			}
		}

		/**
		 * 送信キューにデータを追加します。
		 * このメソッドは内部で保持しているBlockingQueueのaddメソッドを呼び出します。
		 * このメソッドがブロックされることはありません。
		 * キューに追加されたデータは、送信可能であればすぐサーバーに送信されます。
		 * @param data キューに追加されるデータ
		 */
		public void put(WrappedData data){
			queue.add(data);
		}
	}

	/**
	 * IOスレッドが生きているかどうかを調べる.
	 *
	 * isAliveメソッドがtrueを返す場合、なんらかのIOスレッドは生存しています。
	 * このメソッドがfalseを返す場合、socketを安全に閉じることができます。
	 *
	 * @return スレッドが生存している場合true
	 */
	public boolean isAlive() {
		return (threadState.get() != 0);
	}
}

