package jp.dip.th075altlobby.imo.Application.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import jp.dip.th075altlobby.imo.ProcessAdapter.CallBackRunnable;
import jp.dip.th075altlobby.imo.Window.MainWindow.CasterTerminalPanel;

public class CasterTerminalPanelTest extends JFrame {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final static Logger logger = Logger.getLogger("jp.dip.th075altlobby.imo.Application");
	public CasterTerminalPanelTest() {
		setTitle("CasterTerminalPanelTest");
		setSize(320,240);
		setLocationRelativeTo(null);
		final CasterTerminalPanel panel = new CasterTerminalPanel(new CallBackRunnable(){
			@Override
			public void run(String s) {
				logger.log(Level.INFO, s);
			}
		});
		add(panel);
		setVisible(true);

		ScheduledExecutorService exs = Executors.newScheduledThreadPool(5);
		exs.scheduleAtFixedRate(new Runnable() {
			int i = 0;
			@Override
			public void run() {
				i++;
				panel.append(i + "回目の呼び出し", true);
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	public static void main(String[] args) {
		new CasterTerminalPanelTest();
	}
}
