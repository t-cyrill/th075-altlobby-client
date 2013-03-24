package jp.dip.th075altlobby.imo.Window.MainWindow;

import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.UTFDataFormatException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.html.StyleSheet;

import jp.dip.th075altlobby.imo.CasterAdapter.CasterEventInterface;
import jp.dip.th075altlobby.imo.CasterAdapter.IOStreamCasterAdapterWithOutputCallback;
import jp.dip.th075altlobby.imo.CasterAdapter.Listeners.StateChangeListener;
import jp.dip.th075altlobby.imo.Constants.ClientConstants;
import jp.dip.th075altlobby.imo.Data.IPConverter.IPConverter;
import jp.dip.th075altlobby.imo.Data.SettingManager.SettingManager;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.CasterSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ClientSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ConnectionSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.InstantMessageSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.TableSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.WindowSetting;
import jp.dip.th075altlobby.imo.Data.communication.ClientToClientCommand;
import jp.dip.th075altlobby.imo.Data.communication.DataWrapper;
import jp.dip.th075altlobby.imo.Data.communication.InstantMessage;
import jp.dip.th075altlobby.imo.Data.communication.StateModifiedInfo;
import jp.dip.th075altlobby.imo.Data.communication.UserInfo;
import jp.dip.th075altlobby.imo.Data.communication.WrappedData;
import jp.dip.th075altlobby.imo.ProcessAdapter.CallBackRunnable;
import jp.dip.th075altlobby.imo.Resource.Resource;
import jp.dip.th075altlobby.imo.System.Icon.WindowIcon;
import jp.dip.th075altlobby.imo.System.SystemTray.TrayControl;
import jp.dip.th075altlobby.imo.security.Encrypter;

/**
 * メインウィンドウ用フレーム表現クラス
 *
 * @since rev1
 * @author Cyrill
 */
public class MainWindow extends MainWindowBase {
	/**
	 * パブリックメッセージ用ＵＩＤ
	 */
	private final static String publicIMUID = "f1d3ff8443297732862df21dc4e57262";

	private final ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(1);
	private final JFrame ownerFrame;

	private final JSplitPane horizontalSplitPane;
	private final JSplitPane leftVerticalSplitPane;
	private final JSplitPane rightVerticalSplitPane;
	private final JSplitPane rightVerticalSplitInstantMessageToCasterTerminalPane;
	private final CasterTerminalPanel casterTerminalPanel;
	private final UserInfoTablePanel userinfoTablePanel;
	private final GatheringMessagePanel gatheringMessagePanel;
	private final MessageInputPanel messageInputPanel;
	private final ClientSetting _setting;
	private final InstantMessagesPanel instantMessagePanel;
	private final AppTray tray;
	private final String _hostaddr;
	private final int _hostport;
	private Socket _socket;
	private ClientThread _ioThread;
	private final JFrame mainFrame;

	private volatile boolean _hostOnlyFlag = false;

	/**
	 * インスタンスの生成
	 * @param hostaddr 接続先ホストアドレス
	 * @param hostport ホストのポート
	 * @throws IOException 入出力例外が発生した場合
	 * @throws UnknownHostException ホストのIPアドレスを決定できなかった場合
	 */
	public MainWindow(String hostaddr, int hostport, ClientSetting setting, JFrame Owner) throws UnknownHostException, IOException {
		loadMessageResorce();

		_hostaddr = hostaddr;
		_hostport = hostport;
		ownerFrame = Owner;
		_setting = setting;

		beginConnection();

		WindowSetting ws = setting.getWindowSetting();

		mainFrame = new JFrame();

		// このフレームの設定
		mainFrame.setTitle(ClientConstants.MainWindowCaption);
		mainFrame.setSize(ws.getMainWindowWidth(), ws.getMainWindowHeight());

		// x=0 && y=0のとき、ウィンドウを中央に配置、それ以外の場合は記録してある位置に配置する。
		if (ws.getMainWindowXpos() == 0 && ws.getMainWindowYPos() == 0)
			mainFrame.setLocationRelativeTo(null);
		else
			mainFrame.setLocation(ws.getMainWindowXpos(),
						ws.getMainWindowYPos());

		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// ベースとなる3つのJSplitPaneの準備
		horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		leftVerticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightVerticalSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		rightVerticalSplitInstantMessageToCasterTerminalPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

		horizontalSplitPane.setLeftComponent(leftVerticalSplitPane);
		horizontalSplitPane.setRightComponent(rightVerticalSplitPane);
		horizontalSplitPane.setDividerSize(6);
		horizontalSplitPane.setOneTouchExpandable(true);

		userinfoTablePanel = new UserInfoTablePanel(setting);
		userinfoTablePanel.regiserListeners(new SetIMSendUserRunnable(), new FightSelectedPlayerRunnable(), new ResetGatheringTextCallbackRunnable());
		gatheringMessagePanel = new GatheringMessagePanel();
		leftVerticalSplitPane.setTopComponent(userinfoTablePanel);
		leftVerticalSplitPane.setBottomComponent(gatheringMessagePanel);

		StyleSheet style = null;
		try {
			style = SettingManager.loadStyleSheet(new File("style.css"));
		} catch (IOException e) {
			logger.log(Level.WARNING, "IO例外が発生しました。", e);
		}

		instantMessagePanel = new InstantMessagesPanel(style, setting);
		casterTerminalPanel = new CasterTerminalPanel(new WriteOutCasterProcessStreamCallbackRunnable());
		rightVerticalSplitInstantMessageToCasterTerminalPane.setTopComponent(instantMessagePanel);
		rightVerticalSplitInstantMessageToCasterTerminalPane.setBottomComponent(casterTerminalPanel);
		rightVerticalSplitInstantMessageToCasterTerminalPane.setOneTouchExpandable(true);

		messageInputPanel = new MessageInputPanel(setting);
		messageInputPanel.registerListeners(
			new PushedTextArea(),
			new GatherButtonPushedRunnable(),
			new GatherSwitchButtonPushedRunnable(),
			new CloseCasterButtonPushedRunnable()
		);
		_hostOnlyFlag = setting.getHostOnlyFlag(); 
		messageInputPanel.setGatherButtonText(_hostOnlyFlag);

		rightVerticalSplitPane.setTopComponent(rightVerticalSplitInstantMessageToCasterTerminalPane);
		rightVerticalSplitPane.setBottomComponent(messageInputPanel);

		horizontalSplitPane.setDividerLocation(ws.getHorizontalSplitDeviderLocation());
		leftVerticalSplitPane.setDividerLocation(ws.getLeftVerticalSplitDeviderLocation());
		rightVerticalSplitPane.setDividerLocation(ws.getRightVerticalSplitDeviderLocation());
		rightVerticalSplitInstantMessageToCasterTerminalPane.setDividerLocation(ws.getRightVerticalSplitInstantMessageToCasterTerminalPaneDeviderLocation());

		tray = new AppTray(mainFrame);
		tray.add();
		
		WindowIcon.getInstance().setIcon(mainFrame);

		mainFrame.getContentPane().add(horizontalSplitPane);
	}

	public Map<String, String> getMessageMap() {
		return messageMap;
	}

	/**
	 * コネクションを確立し、IOスレッドを起動する.
	 *
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private void beginConnection() throws UnknownHostException, IOException {
		_socket = new Socket(_hostaddr, _hostport);
		_ioThread = new ClientThread(this, _socket, _setting);

		ExecutorService exs = Executors.newCachedThreadPool();
		exs.execute(_ioThread);
		exs.shutdown();
	}

	/**
	 * 通信スレッドを閉じ、ソケットを閉じる。
	 *
	 * @throws IOException ソケットを閉じる際にエラーが発生した場合
	 */
	private void closeConnection() throws IOException {
		_ioThread.shutdown();
		int counter = 0;
		while (_ioThread.isAlive() && counter < 5) {
			try {
				logger.info("_ioThreadの終了を待っています");
				Thread.sleep(1000);
				counter++;
			} catch (InterruptedException e) {
				break;
			}
		}

		logger.info("ソケットを閉じます。");

		// IOスレッドの終了を待ってソケットを閉じる
		_socket.close();
	}

	/**
	 * コネクションを終了し、再度接続を試みる.
	 *
	 * resetConnectionは連続してcloseConnectionとbeginConnectionを呼び出し、
	 * サーバーへの再接続を試みます。
	 * 再接続ができない場合、このメソッドは例外をスローします。
	 *
	 * @throws UnknownHostException 接続先ホストが見つからない場合
	 * @throws IOException 通信例外が発生した場合
	 */
	private void resetConnection() throws UnknownHostException, IOException {
			logger.info("コネクションを切断します。");
			try {
				closeConnection();
			} catch (IOException e) {
				logger.warning("closeConnectionで例外を検出しました。");
			}
			logger.info("コネクションを繋ぎなおします。");
			beginConnection();
	}

	private void loadMessageResorce() {
		ResourceBundle resource = Resource.getBundle();

		String[] keys = {
			"main.error.im.exception.length",
			"main.message.alreadyFighting",
			"main.message.connected",
			"main.message.FightSelectedPlayerRunnable.casterError",
			"main.message.FightSelectedPlayerRunnable.cannotConnect",
			"main.message.inputBufferMarginTiming",
			"main.message.observedDelay",
			"main.message.selectWatch.message.line1",
			"main.message.selectWatch.message.line2",
			"main.message.selectWatch.message.caption",
			"main.message.selectWatch.watch",
			"main.message.selectWatch.close",
			"main.message.selectDelayBufferMargin",
			"main.message.waitingOppositePlayerInput",
			"apptray.menu.turnwin",
			"apptray.menu.leave",
			"apptray.menu.close",
			"apptray.menu.quit"
		};

		for (String key : keys)
			messageMap.put(key, resource.getString(key));
	}

	/**
	 * Casterプロセスに書き出すタイミングで呼ばれる引数付きRunnable
	 *
	 * @author Cyrill
	 * @since rev31
	 */
	class WriteOutCasterProcessStreamCallbackRunnable extends CallBackRunnable {
		/**
		 * このメソッドはEDTから実行されます。
		 */
		@Override
		public void run(String s) {
			_ioThread.writeCasterStream(s);
		}
	}

	public void registerListener() {
		mainFrame.addWindowListener(new windowCloseListener());
		instantMessagePanel.registerListeners();
	}

	/**
	 * <h1>putSystemMessage</h1>
	 * <h2>システムメッセージを送信します</h2>
	 * <p>プログラムからユーザーに通知したい情報をInstantMessageビューに追加します。</p>
	 * <h3>スレッドセーフ</h3>
	 * <p>このメソッドはSwingのイベントディスパッチスレッド以外からも呼び出すことができます。</p>
	 *
	 * @param fromName 送信元
	 * @param message メッセージ
	 */
	void putSystemMessage(final String fromName, final String message) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				InstantMessage im = new InstantMessage(fromName, "*", message, System.currentTimeMillis(), (byte) 1);
				instantMessagePanel.addInstantMessage(im);
			}
		});
	}

	class windowCloseListener extends WindowAdapter {
		@Override
		public void windowClosing(WindowEvent windowevent) {
			putExitStatus(WINDOW_CLOSED);
		}
	}

	/**
	 * テキストエリア内でキーが設定されたキーが押されたときに呼ばれるRunnable
	 *
	 * @author Cyrill
	 */
	class PushedTextArea implements Runnable {
		@Override
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					// IM配信の予約をする
					String toUID = "";
					String message = messageInputPanel.getText();
					int color = messageInputPanel.getSelectedColor();

					// 全体IMの場合特殊なUIDをセットする
					if (messageInputPanel.isWholeIMCheckSelected())
						toUID = publicIMUID;


					if (messageInputPanel.getText().length() > 1024 * 64) {
						putSystemMessage("System", messageMap.get("main.error.im.exception.length"));
					} else {
						try {
							WrappedData data = new WrappedData(WrappedData.POST_INSTANTMESSAGE, DataWrapper.wrapPostInstantMessage(toUID, message, (byte) color));
							_ioThread.put(data);
							messageInputPanel.setText("");
						} catch (IOException e) {
							logger.log(Level.WARNING, "IM送信処理でIO例外が発生しました。");
						}
					}
				}
			});
		}
	}

	/**
	 * IM送信先に設定するメニューが押されたときに呼ばれるRunnable
	 *
	 * @author Cyrill
	 */
	class SetIMSendUserRunnable implements Runnable {
		@Override
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UserInfo p = userinfoTablePanel.getSelectedRow();
					if (p != null) {
						String name = p.getPublicName() + "@" + p.getUID().substring(0, 3) + "...";
						messageInputPanel.setNameFieldText(name);
					} else {
						logger.log(Level.INFO, "ユーザーが選択されていません。");
					}
				}
			});
		}
	}

	/**
	 * 対戦するメニューが押されたときに呼ばれるRunnable
	 *
	 * @author Cyrill
	 */
	class FightSelectedPlayerRunnable implements Runnable {
		@Override
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UserInfo p = userinfoTablePanel.getSelectedRow();
					if (p == null) return ;

					switch (p.getState()) {
						case UserInfo.HOST_GATHERING:
							_ioThread.setUserStateHostGatheringWait();
						case UserInfo.GATHERING:
							try {
								_ioThread.put(new WrappedData(WrappedData.POST_CLIENT_TO_CLIENT_COMMAND,
										DataWrapper.wrapPostClientToClientCommand(p.getUID(), ClientToClientCommand.POST_FIGHT)));
								logger.log(Level.INFO, p.getPublicName() + "さんに対戦申込みメッセージを送信しました。");
							} catch (IOException e) {
								logger.log(Level.WARNING, "IO例外が発生しました。");
							}
							break;
						case UserInfo.FIGHTING:
							try {
								_ioThread.connectCaster(p);
							} catch (IOException e) {
								logger.warning("Casterの起動に失敗しました。");
								putSystemMessage("System", messageMap.get("main.message.FightSelectedPlayerRunnable.casterError"));
							}
							break;
						default:
							putSystemMessage("System", messageMap.get("main.message.FightSelectedPlayerRunnable.cannotConnect"));
							break ;
					}
				}
			});
		}
	}

	class ResetGatheringTextCallbackRunnable implements Runnable {
		@Override
		public void run() {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					UserInfo p = userinfoTablePanel.getSelectedRow();
					if(p != null)
						gatheringMessagePanel.setText(p.getPublicMessage());
				}
			});
		}
	}

	/**
	 * 募集するボタンが押されたときに呼ばれるRunnable
	 *
	 * @author Cyrill
	 */
	class GatherButtonPushedRunnable implements Runnable {
		@Override
		public void run() {
			byte status = _hostOnlyFlag == true ? UserInfo.HOST_GATHERING : UserInfo.GATHERING;
			setMyUserState(status);
		}
	}

	/**
	 * 募集するボタンが押されたときに呼ばれるRunnable
	 *
	 * @author Cyrill
	 */
	class GatherSwitchButtonPushedRunnable implements Runnable {
		@Override
		public void run() {
			_hostOnlyFlag = !_hostOnlyFlag;
			messageInputPanel.setGatherButtonText(_hostOnlyFlag);
		}
	}
	
	/**
	 * 募集をやめるボタンが押されたときに呼ばれるRunnable
	 *
	 * @author Cyrill
	 */
	class CloseCasterButtonPushedRunnable implements Runnable {
		@Override
		public void run() {
			setMyUserState(UserInfo.WAITING);
		}
	}

	/**
	 * 自分の現在の状態を設定します。
	 * このメソッドはModifyStateの送信を簡略化します。
	 * 実際の変更が行われるのはサーバからのレスポンスがあったときです。
	 *
	 * @param state 設定する状態
	 * @see ClientThread.InputThread#responseModifyState(byte[])
	 */
	private void setMyUserState(byte state) {
		try {
			_ioThread.put(new WrappedData(WrappedData.MODIFY_STATE, DataWrapper.wrapModifyUserState(state)));
		} catch (IOException e) {
			logger.log(Level.WARNING, "IO例外が発生しました。");
		}
	}

	public StateChangeListener getCasterStateChangeListener() {
		return new StateChangeListener() {
			@Override
			public void stateChanged(int state) {
				setMyUserState((byte) state);
			}
		};
	}

	/**
	 * {@link IOStreamCasterAdapterWithOutputCallback}から呼ばれる
	 * Casterのライン出力処理用クラスのインスタンスを生成する.
	 *
	 * @return 出力処理用のコールバッククラス
	 * @since rev39
	 * @author Cyrill
	 *
	 */
	public CallBackRunnable getOutputCasterMessageCallback() {
		return new CallBackRunnable() {
			@Override
			public void run(String s) {
				casterTerminalPanel.append(s, true);
			}
		};
	}

	/**
	 * Casterの出力を受け取るクラスのインスタンスを生成する.
	 *
	 * @return 出力を受け取るクラスのインスタンス
	 * @since rev39
	 * @author Cyrill
	 * @return
	 */
	public CasterEventInterface getCasterEventListener() {
		return new CasterEventInterface() {
			AtomicLong real_delay = new AtomicLong(0L);

			@Override
			public void bufferMarginPrinted(int margin) {
				putSystemMessage("caster", "Buffer Margin : " + margin);
			}

			@Override
			public void closed() {
				try {
					_ioThread.put(new WrappedData(WrappedData.MODIFY_STATE, DataWrapper.wrapModifyUserState(UserInfo.WAITING)));
				} catch (IOException e) {
					logger.log(Level.WARNING, "IO例外をキャッチしました。");
				}
			}

			@Override
			public void defaultCall(String s) {
				// RoundCountなら出力
				// それ以外は破棄
				if(s.matches("^debug : RoundCount \\d+"))
					putSystemMessage("caster", s);
				else if(s.matches("\\d+:\\d+:\\d+ .*"))
					putSystemMessage("caster", s);
			}

			@Override
			public void observedDelay(double realDelay) {
				putSystemMessage("caster", messageMap.get("main.message.observedDelay") + " : " + realDelay + "[ms]");
				real_delay.set(Double.doubleToLongBits(realDelay));
			}

			@Override
			public void inputBufferMarginTiming() {
				try {
					_ioThread.put(new WrappedData(WrappedData.MODIFY_STATE, DataWrapper.wrapModifyUserState(UserInfo.SETTING)));
				} catch (IOException e) {
					logger.log(Level.WARNING, "IO例外が発生しました。");
				}
				putSystemMessage("System", messageMap.get("main.message.inputBufferMarginTiming"));
				selectDelayBufferMargin();
			}

			/**
			 * ポート設定を自動で行う
			 *
			public void portSetting() {
				_ioThread.fireSelectedEvent(3);
				_ioThread.fireSelectedEvent(_setting.getCasterSetting().getPort());
			}

			public void rollInputDelayPrinted(String s) {
				Pattern pt = Pattern.compile("  Input delay : (\\d+)");
				Matcher matcher = pt.matcher(s);
				String inputDelay;
				if (matcher.matches()) {
					inputDelay  = matcher.group(1);
					putSystemMessage("caster", "  Input delay : " + inputDelay);
				}
			}

			public void rewindFrames(String s) {
				Pattern pt = Pattern.compile("Rewind frames : (\\d+)");
				Matcher matcher = pt.matcher(s);
				String rewindFrames;
				if(matcher.matches()) {
					rewindFrames  = matcher.group(1);
					putSystemMessage("caster", "Rewind frames : " + rewindFrames);
				}
			}

			public void phasePrinted(String s) {
				/*
				if(s.equals("Phase : Default ( Wait )"))
				else if(s.equals("Phase : Default"))
				*
				logger.log(Level.INFO, s);
			}

			public void rollCasterWaitingInput() {
				_ioThread.fireSelectedEvent(-1);
			}

			public void roundCountPrinted(String s) {
				putSystemMessage("caster", s);
			}

			public void roundCount(String s) {
				putSystemMessage("caster", s);
			}
			*/

			public void alreadyFighting() {
				putSystemMessage("System", messageMap.get("main.message.alreadyFighting"));
				selectWatch();
			}

			private void selectWatch() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						setMyUserState(UserInfo.SETTING);
						String message[] = { messageMap.get("main.message.selectWatch.message.line1"), messageMap.get("main.message.selectWatch.message.line2")};
						int value = JOptionPane.showConfirmDialog(null , message , messageMap.get("main.message.selectWatch.message.caption") , JOptionPane.YES_NO_OPTION);
						if (value == JOptionPane.YES_OPTION){
							putSystemMessage("System", messageMap.get("main.message.selectWatch.watch"));
							_ioThread.fireSelectedWatchEvent(1);
						} else {
							putSystemMessage("System", messageMap.get("main.message.selectWatch.close"));
							_ioThread.closeCaster();
						}
					}
				});
			}

			public void inputedBufferMargin(int margin) {
				putSystemMessage("caster", "Buffer Margin : " + margin);
				setMyUserState(UserInfo.FIGHTING);
			}

			private void selectDelayBufferMargin() {
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Integer[] delay_array = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
						Double delay = Double.longBitsToDouble(real_delay.get());
						int recommend = 2;
						int iDelay = delay.intValue();
						if(iDelay < 8)
							recommend = 1;
						else if(iDelay > 33){
							if(iDelay < 48)
								recommend = 3;
							else
								recommend = 4;
						}

						Integer InputedDelay = recommend;

						try {
							String delayString;
							if (delay == Double.longBitsToDouble(0L))
								delayString = "不明";
							else
								delayString = delay.toString() + "[ms]";
							InputedDelay =  (Integer) JOptionPane.showInputDialog(
								null , "ディレイを設定してください\n"
								+ "Delay " + delayString + "\n"
								+ "推奨 : "+recommend+" or " + (recommend + 1), "Delayの設定",
								JOptionPane.INFORMATION_MESSAGE , null , delay_array , delay_array[recommend]);
						} catch (RuntimeException e) {
							logger.log(Level.WARNING, "ダイアログのオープンに失敗しました。推奨値を使います。");
							putSystemMessage("System", messageMap.get("main.message.selectDelayBufferMargin") + "[delay = "+InputedDelay+"]");
						}

						// delayのチェック
						InputedDelay = (InputedDelay == null) ? 0 : InputedDelay;

						_ioThread.fireInputedMarginEvent(InputedDelay);
					}
				});
			}

			public void connected(String ip) {
				UserInfo info = userinfoTablePanel.getUserInfoByUID(Encrypter.getHash(IPConverter.toByteArray(ip), "MD5"));
				if (info != null) {
					putSystemMessage("caster", info.getPublicName() + "@" + info.getUID().substring(0, 3)
							+ messageMap.get("main.message.connected"));
				} else {
					logger.log(Level.WARNING, "UIDの取得に失敗しました。");
				}
			}

			@Override
			public void failed(int reason) {
				switch (reason) {
					case BIND_ERROR:
						putSystemMessage("caster", "BIND ERROR");
						_ioThread.closeCaster();
						break;

					case INPUT_TIMEOUT:
						putSystemMessage("caster", "INPUT TIMEOUT");
						_ioThread.closeCaster();
						break;

					case TH075FAILED:
						putSystemMessage("System", "東方萃夢想の起動に失敗しました。");
						_ioThread.closeCaster();
						break;

					case TIMEOUT_ACCESS:
						putSystemMessage("caster", "TIMEOUT ACCESS");
						_ioThread.closeCaster();
						break;

					case TIMEOUT_AWAY:
						putSystemMessage("caster", "TIMEOUT AWAY");
						_ioThread.closeCaster();
						break;

				}
			}

			@Override
			public void waitingOppositePlayerInput() {
				putSystemMessage("System", messageMap.get("main.message.waitingOppositePlayerInput"));
			}
		};
	}

	class AppTray implements ActionListener,ItemListener {
		private Image image;
		private MenuItem	windowTurnEnableItem,
							disconnectItem,
							exitItem;
		private CheckboxMenuItem leavingItem;
		private ActionListener listener;
		private final JFrame owner;
		public AppTray(JFrame owner) {
			windowTurnEnableItem = new MenuItem(messageMap.get("apptray.menu.turnwin"));
			leavingItem = new CheckboxMenuItem(messageMap.get("apptray.menu.leave"));
			disconnectItem = new MenuItem(messageMap.get("apptray.menu.close"));
			exitItem = new MenuItem(messageMap.get("apptray.menu.quit"));

			this.owner = owner;
			image = WindowIcon.getInstance().getImageIcon();
		}

		/**
		 * イベントリスナを登録する。
		 * このメソッドはコンストラクタからイベントリスナを登録できないために用意されています。
		 */
		private void registerListeners(){
			windowTurnEnableItem.addActionListener(this);
			leavingItem.addItemListener(this);
			disconnectItem.addActionListener(this);
			exitItem.addActionListener(this);

			listener = new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(!leavingItem.getState())
						ownerVisible();
				}
			};
		}

		private void removeListeners() {
			windowTurnEnableItem.removeActionListener(this);
			leavingItem.removeItemListener(this);
			disconnectItem.removeActionListener(this);
			exitItem.removeActionListener(this);
		}

		/**
		 * トレイアイコンを追加します。
		 */
		public void add(){
			registerListeners();
			TrayControl.addIcon(image, ClientConstants.MainWindowCaption, getPopupMenu());
			TrayControl.setIconActionLister(listener);
		}

		/**
		 * トレイアイコンを取り除きます。
		 */
		public void remove(){
			removeListeners();
			TrayControl.removeIcon();
		}

		private PopupMenu getPopupMenu(){
			PopupMenu popup = new PopupMenu();
			popup.add(windowTurnEnableItem);
			popup.add(leavingItem);
			popup.addSeparator();
			popup.add(disconnectItem);
			popup.add(exitItem);
			return popup;
		}

		/**
		 * オーナーウィンドウの表示状態を切り替えます。
		 */
		private void ownerVisible(){
			if(owner.isVisible()){
				owner.setExtendedState(JFrame.ICONIFIED);
				owner.setVisible(false);
			}else{
				owner.setVisible(true);
				owner.setExtendedState(JFrame.NORMAL);
				owner.toFront();
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO 自動生成されたメソッド・スタブ
			if(e.getSource().equals(windowTurnEnableItem)){
				ownerVisible();
			}else if(e.getSource().equals(disconnectItem)){
				putExitStatus(WINDOW_CLOSED);
			}else if(e.getSource().equals(exitItem)){
				putExitStatus(WINDOW_CLOSE_AND_EXIT);
			}
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			try {
				if(leavingItem.getState()){
					// 退席中チェック時
					// 状態を退席中に変更する
					_ioThread.put(new WrappedData(WrappedData.MODIFY_STATE, DataWrapper.wrapModifyUserState(UserInfo.LEAVING)));
					userinfoTablePanel.setFightSelectedPlayerEnabled(false);
				}else{
					// 退席中解除時
					_ioThread.put(new WrappedData(WrappedData.MODIFY_STATE, DataWrapper.wrapModifyUserState(UserInfo.WAITING)));
					userinfoTablePanel.setFightSelectedPlayerEnabled(true);
				}
			} catch (IOException ex) {
				logger.severe("IO例外が発生しました");
			}
		}
	}

	/**
	 * <h1>open</h1>
	 * <h2>設定ウィンドウを開く</h2>
	 * <p>設定ウィンドウを開きます。設定された結果がClientSettingとして返ります。</p>
	 * <p>このメソッドは、引数として渡されたJFrameをロックします。</p>
	 *
	 * @param owner 親ウィンドウ
	 * @return 終了ステータス
	 */
	public int open()  {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ownerFrame.setVisible(false);
				mainFrame.setVisible(true);
			}
		});

		try {
			firstContact();
		} catch (IOException e) {
			logger.log(Level.WARNING, "IO例外が発生しました。", e);
			putExitStatus(-1);
		}

		int ret = -1;
		try {
			while (true) {
				ret = queue.take();
				if (ret == -3 || ret == -4) {
					reconnectAfter(new Runnable() {
						@Override
						public void run() {
							putExitStatus(-5);
						}
					});
				} else {
					break;
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		ExecutorService exs = Executors.newCachedThreadPool();
		exs.execute(new Runnable() {
			@Override
			public void run() {
				try {
					closeConnection();
				} catch (IOException e) {
					logger.log(Level.WARNING, "終了処理でソケットを閉じられませんでした。", e);
				}
			}
		});

		instantMessagePanel.close();

		storeSetting();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ownerFrame.setVisible(true);
				mainFrame.setVisible(false);
			}
		});

		tray.remove();
		return ret;
	}

	private void firstContact() throws UTFDataFormatException, IOException {
		// バージョン確認
		// 新規接続要求
		// 接続中ユーザー一覧の取得
		// IM履歴
		WrappedData checkVersion = new WrappedData(WrappedData.CHECK_VERSION, new byte[0]);
		_ioThread.put(checkVersion);

		// 新規接続要求
		ConnectionSetting c = _setting.getConnectionSetting();
		CasterSetting cs = _setting.getCasterSetting();
		InstantMessageSetting ims = _setting.getImSetting();
		byte IPHideFlag = 0;
		if (c.getIPHideFlag())
			IPHideFlag = 1;
		WrappedData newConnect = new WrappedData(WrappedData.NEW_CONNECT, DataWrapper.wrapNewConnect(
				c.getName(), c.getMessage(), c.getShortMessage(), cs.getPort().shortValue(), ims.getIMState().byteValue(), IPHideFlag));
		_ioThread.put(newConnect);

		// ユーザー一覧の要求
		WrappedData userList = new WrappedData(WrappedData.REQUEST_USERLIST, new byte[0]);
		_ioThread.put(userList);

		// IM履歴の要求
		if(ims.getOldIMReadCounter() > 0) {
			WrappedData imHistory = new WrappedData(WrappedData.REQUEST_STORED_INSTANTMESSAGES, DataWrapper.wrapByte(ims.getOldIMReadCounter().byteValue()));
			_ioThread.put(imHistory);
		}
	}

	private void storeSetting() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String tableOrder = userinfoTablePanel.getTableHeadersOrder();

				Rectangle r = mainFrame.getBounds();
				WindowSetting w = new WindowSetting(
					horizontalSplitPane.getDividerLocation(),
					leftVerticalSplitPane.getDividerLocation(),
					rightVerticalSplitPane.getDividerLocation(),
					rightVerticalSplitInstantMessageToCasterTerminalPane.getDividerLocation(),
					r.x,
					r.y,
					r.width,
					r.height,
					messageInputPanel.getSelectedColor(),
					instantMessagePanel.getIMFilterSelectedIndex(),
					instantMessagePanel.isAutoScrollSelected()
				);
				TableSetting t = new TableSetting(
					tableOrder,
					userinfoTablePanel.getTableHeaderWidth(0),
					userinfoTablePanel.getTableHeaderWidth(1),
					userinfoTablePanel.getTableHeaderWidth(2),
					userinfoTablePanel.getTableHeaderWidth(3),
					userinfoTablePanel.getTableHeaderWidth(4),
					userinfoTablePanel.getTableHeaderWidth(5)
				);
				ClientSetting newSetting = new ClientSetting(
					_setting.getConnectionSetting(),
					_setting.getImSetting(),
					_setting.getImsoundSetting(),
					_setting.getCasterSetting(),
					w, t, userinfoTablePanel.getNGUserListString(),
					_hostOnlyFlag
				);
				SettingManager.storeSetting(new File("config_lobby_sys_c.xml"), newSetting);
			}
		});
	}

	/**
	 * 終了ステータスを設定し、このウィンドウを閉じます。
	 * 一般に正常終了の場合は0以上の値をセットし、以上終了の場合は負の値をセットします。
	 * 実際にどのステータスがどのように振舞うかは実装に依存します。
	 *
	 * @param status 終了ステータス
	 */
	public void putExitStatus(int status) {
		queue.add(status);
	}

	public UserInfo getUserInfoByUID(String UID) {
		return userinfoTablePanel.getUserInfoByUID(UID);
	}

	public void removeUserInfo(UserInfo info) {
		userinfoTablePanel.removeUserInfo(info);
	}

	public void setMyStateControls(byte state) {
		switch (state) {
			case UserInfo.WAITING:
				userinfoTablePanel.setFightSelectedPlayerEnabled(true);
				messageInputPanel.switchButton(0);
				break;
			case UserInfo.GATHERING:
			case UserInfo.HOST_GATHERING:
				userinfoTablePanel.setFightSelectedPlayerEnabled(false);
				messageInputPanel.switchButton(1);
				break;
			default:
				userinfoTablePanel.setFightSelectedPlayerEnabled(false);
				messageInputPanel.switchButton(2);
				break;
		}
		messageInputPanel.setUserStateTextLabelState(state);
	}

	public boolean isNGUser(String fromUID) {
		return userinfoTablePanel.isNGUser(fromUID);
	}

	public void addInstantMessage(InstantMessage current, boolean empty) {
		instantMessagePanel.addInstantMessage(current, empty);
	}

	public void addInstantMessage(InstantMessage im) {
		instantMessagePanel.addInstantMessage(im);
	}

	public boolean isActive() {
		return mainFrame.isActive();
	}

	public void updateUserInfo(StateModifiedInfo info) {
		userinfoTablePanel.updateUserInfo(info.getUID(), info.getState());
	}

	public void setUserInfos(UserInfo[] usersArray) {
		userinfoTablePanel.setUserInfos(usersArray);
	}

	public void addUserInfo(UserInfo info) {
		userinfoTablePanel.addUserInfo(info);
	}

	public void reconnectAfter(final Runnable callback) {
		logger.info("サーバーへの再接続を試みます。");
		putSystemMessage("Client", "サーバーとの接続が切断されました。" + ClientConstants.RECONNECT_TIME_DELAY + "秒後再接続をします。");
		ScheduledExecutorService exs = Executors.newScheduledThreadPool(10);
		exs.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					resetConnection();
					logger.info("サーバーへの再接続を要求します。");
					firstContact();
					logger.info("再接続に成功しました。");
					putSystemMessage("Client", "再接続に成功しました。");
				} catch (IOException e) {
					putSystemMessage("Client", "再接続に失敗しました。");
					logger.log(Level.WARNING, "再接続に失敗しました。", e);
					callback.run();
				}
			}
		}, ClientConstants.RECONNECT_TIME_DELAY, TimeUnit.SECONDS);
		exs.shutdown();
	}
}
