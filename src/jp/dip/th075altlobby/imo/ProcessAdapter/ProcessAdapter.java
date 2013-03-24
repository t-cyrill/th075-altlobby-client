package jp.dip.th075altlobby.imo.ProcessAdapter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

/**
 * Casterとの通信を行う中間クラスです。
 *
 * @author Cyrill
 */
public class ProcessAdapter implements ProcessAdapterInterface {
	private final Process process;
	private final InputThread input;
	private final OutputThread output;
	private final AtomicInteger inputStreamThreadExitStatus = new AtomicInteger(0);
	private final AtomicInteger outputStreamThreadExitStatus = new AtomicInteger(0);
	private final AtomicBoolean isAliveStatus = new AtomicBoolean(false);
	private final Logger logger = Logger.getLogger("jp.dip.th075altlobby");

	/**
	 * <h1>ProcessAdapter</h1>
	 * <h2>コマンドを指定して、ProcessAdapterを構築する</h2>
	 * <p>Processではなく、Stringで実行するコマンドを指定してProcessAdapterを構築します。</p>
	 * <p>このコンストラクタは</p>
	 * <code>this(runnable, closedRunnable, new ProcessBuilder(command).start());</code>
	 * <p>を実行します。具体的な処理は{@link ProcessAdapter#ProcessAdapter(CallBackRunnable, Runnable, Process)}を参照してください。</p>
	 * <p>{@link ProcessBuilder#start()}はプロセスの起動に失敗する場合、{@link IOException}をスローします。
	 * このコンストラクタはprocがnullになることはないため、{@link NullPointerException}がスローされることはありません。</p>
	 *
	 * @param runnable プロセスからの出力があったときに呼ばれるCallbackRunnable
	 * @param closedRunnable 終了時に呼ばれるRunnable
	 * @param command 実行されるコマンド
	 * @throws IOException ProcessBuilderがIO例外を発生させた場合（多くの場合、実行コマンドが存在しない）
	 */
	public ProcessAdapter(CallBackRunnable runnable,final Runnable closedRunnable, String... command) throws IOException {
		this(runnable, closedRunnable, new ProcessBuilder(command).start());
	}

	/**
	 * <h1>ProcessAdapter</h1>
	 * <h2>非同期に実行されるプロセスとのデータ交換をするオブジェクト</h2>
	 * <p>ProcessAdapterはProcessBuilderが返す一般的なProcessをラップし、簡単な方法でプロセスを操作する方法を提供します。</p>
	 * <p>このコンストラクタでは、出力があったときに出力を引数として渡されるrunnable、プロセス終了時に呼ばれるclosedRunnable、対象となるprocを引数にとります。</p>
	 * <p>runnableはプロセスが一行の出力を行うたびに呼び出されます。この機能は{@link BufferedReader#readLine()}により実装されます。
	 * 一行の出力が行われるまではreadLineでブロックされます。</p>
	 * <p>closedRunnableはプロセスの終了方法に関わらず、プロセスの終了を検出した時点で速やかに呼び出されます。
	 * この時点でisAliveStatusはfalseに設定されます。一般にclosedRunnableは別スレッドとして起動されます。
	 * この機能は{@link Process#waitFor()}により実装されます。</p>
	 * <p>procにnullを指定することはできません。この場合、コンストラクタは非チェック例外である{@link NullPointerException}をスローします。</p>
	 *
	 * @param runnable プロセスからの出力があったときに呼ばれるCallbackRunnable
	 * @param closedRunnable 終了時に呼ばれるRunnable
	 * @param proc 監視対象とするプロセス
	 * @throws NullPointerException 指定されたProcessがnullの場合
	 */
	public ProcessAdapter(CallBackRunnable runnable, final Runnable closedRunnable, Process proc) {
		if(proc == null)
			throw new NullPointerException("processがnullです。");

		isAliveStatus.set(true);
		process = proc;

		input = new InputThread(process.getInputStream(), runnable);
		output = new OutputThread(process.getOutputStream());

		ExecutorService exs = Executors.newCachedThreadPool();
		exs.execute(input);
		exs.execute(output);
		exs.execute(new Runnable() {
			@Override
			public void run() {
				try {
					process.waitFor();
				} catch (InterruptedException e) {
					close();
				}
				isAliveStatus.set(false);
				ExecutorService exs = Executors.newCachedThreadPool();
				exs.execute(closedRunnable);
				exs.shutdown();
			}
		});
		exs.shutdown();
	}

	/* (非 Javadoc)
	 * @see jp.dip.th075altlobby.imo.CasterAdapter.ProcessAdapterInterface#write(java.lang.String)
	 */
	public void write(String s) {
		output.add(s);
	}

	/* (非 Javadoc)
	 * @see jp.dip.th075altlobby.imo.CasterAdapter.ProcessAdapterInterface#getInputStreamThreadExitStatus()
	 */
	public int getInputStreamThreadExitStatus() {
		return inputStreamThreadExitStatus.get();
	}

	/**
	 * <h1>close</h1>
	 * <h2>プロセスを終了し、IOスレッドを終了する</h2>
	 * <p>このクラスが管理しているProcessのdestroyメソッドを呼び出し、プロセスを破棄します。</p>
	 * <p>プロセスのIOを管理するスレッドも同時に終了されます。</p>
	 * <p>このメソッド呼出し後にwrite等のメソッドを呼び出すことはできません。</p>
	 *
	 * @see Process#destroy()
	 * @see jp.dip.th075altlobby.imo.CasterAdapter.ProcessAdapterInterface#close()
	 */
	public void close() {
		process.destroy();
		input.close();
		output.add("");
	}

	/* (非 Javadoc)
	 * @see jp.dip.th075altlobby.imo.CasterAdapter.ProcessAdapterInterface#getOutputStreamThreadExitStatus()
	 */
	public int getOutputStreamThreadExitStatus() {
		return outputStreamThreadExitStatus.get();
	}

	/* (非 Javadoc)
	 * @see jp.dip.th075altlobby.imo.CasterAdapter.ProcessAdapterInterface#isAlive()
	 */
	public boolean isAlive() {
		return isAliveStatus.get();
	}

	class InputThread implements Runnable {
		private final BufferedReader br;
		private final CallBackRunnable runnable;

		public InputThread(InputStream in, CallBackRunnable runnable) {
			br = new BufferedReader(new InputStreamReader(in));
			this.runnable = runnable;
		}

		@Override
		public void run() {
			while (true) {
				try {
					String line = br.readLine();
					if (line == null)
						break ;
					runnable.run(line);
				} catch (IOException e) {
					logger.log(Level.WARNING, "IO例外が発生しました。", e);
					output.add("");
					inputStreamThreadExitStatus.set(-1);
					break ;
				} catch (RuntimeException e) {
					logger.log(Level.SEVERE, "RuntimeExceptionが発生しました。", e);
					output.add("");
					inputStreamThreadExitStatus.set(-2);
					JOptionPane.showMessageDialog(null, "Casterスレッド上でRuntimeExceptionが発生しました。ログを確認してください。", "致命的エラー", JOptionPane.ERROR_MESSAGE);
					break;
				}
			}

			output.add("");
			close();
		}

		/**
		 * 入力スレッドを閉じる.
		 *
		 * closeメソッドを呼ぶと、BufferedReaderを閉じます。
		 * 出力待ちループではIO例外が発生し、自動的にループを抜けます。この場合、終了ステータスは-1が設定されます。
		 */
		public void close() {
			try {
				br.close();
			} catch (IOException e) {
				logger.warning("InputThreadがクローズできませんでした。Assert");
			}
		}
	}

	class OutputThread implements Runnable {
		private final BufferedWriter bw;
		private final BlockingQueue<String> queue = new ArrayBlockingQueue<String>(4);
		public OutputThread(OutputStream out) {
			bw = new BufferedWriter(new OutputStreamWriter(out));
		}

		@Override
		public void run() {
			try {
				while (true) {
					String s = queue.take();
					if (s.equals(""))
						break;
					bw.write(s);
					bw.write("\n");
					bw.flush();
				}
			} catch (InterruptedException e) {
				outputStreamThreadExitStatus.set(-2);
				input.close();
			} catch (IOException e) {
				outputStreamThreadExitStatus.set(-1);
				input.close();
			}

			try {
				bw.close();
			} catch (IOException e) {
				logger.warning("OutputThreadでBufferedReaderがブロックされました");
			}
		}

		/**
		 * casterに文字列を送信する.
		 * 出力スレッドのキューに値を追加し、casterに文字列を送信します。
		 * キューに積まれた値は、実行中のスレッドから読みだされ、自動的にcasterへ送信されます。
		 * このメソッドはキューに値を追加するだけです。呼び出しによって、ブロックされることはありません。
		 *
		 * @param str casterに送信する文字列
		 */
		public void add(String str) {
			queue.add(str);
		}
	}
}
