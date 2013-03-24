package jp.dip.th075altlobby.imo.Window.EventAdapter;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <h1>RunnableCallActionListener</h1>
 * <h2>Runnableを呼び出すActionListener</h2>
 * <p>内部でRunnableを呼び出すActionListenerです。</p>
 * <p>ExecutorServiceを利用し、内部でスレッドを生成します。イベントは別スレッドで処理されます。</p>
 *
 * @author Cyrill
 */
public class RunnableCallActionListener implements ActionListener {
	private Runnable data;

	public RunnableCallActionListener(Runnable r) {
		this.data = r;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ExecutorService exs = Executors.newCachedThreadPool();
		exs.execute(data);
		exs.shutdown();
	}
}