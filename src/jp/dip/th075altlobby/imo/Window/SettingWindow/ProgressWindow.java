package jp.dip.th075altlobby.imo.Window.SettingWindow;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressWindow {
	private final JFrame frame = new JFrame();
	private final JProgressBar progress = new JProgressBar();
	private final JLabel label = new JLabel();
	public ProgressWindow() {
		frame.setTitle("進捗状況");
		frame.setSize(new Dimension(240,120));
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		Dimension d = new Dimension(200,12);
		progress.setPreferredSize(d);
		progress.setMaximumSize(d);
		progress.setAlignmentX(0.5f);

		label.setText("ステート");
		label.setPreferredSize(d);
		label.setAlignmentX(0.5f);

		JPanel p = new JPanel();
		BoxLayout layout = new BoxLayout(p , BoxLayout.Y_AXIS);
		p.setLayout(layout);
		p.add(Box.createVerticalStrut(20));
		p.add(progress);
		p.add(Box.createVerticalStrut(10));
		p.add(label);
		frame.getContentPane().add(p);
	}

	public void setClosedEvent(final Runnable closedRunnable){
		frame.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				closedRunnable.run();
			}
		});
	}

	/**
	 * 表示状態を切り替える
	 * @param b trueで表示, falseで非表示
	 */
	public void setVisible(final boolean b){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				frame.setVisible(b);
			}
		});
	}

	/**
	 * プログレスバーの表示を切り替える
	 * @param i 0-100のステート
	 */
	public void setProgress(final int i) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				progress.setValue(i);
			}
		});
	}

	/**
	 * ラベルのテキストを変更する
	 * @param s テキスト
	 */
	public void setLabelText(final String s) {
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				label.setText(s);
			}
		});
	}

	/**
	 * ウィンドウとサブコンポーネントを破棄する
	 */
	public void dispose() {
		frame.dispose();
	}

	public static void main(String[] args) {
		ProgressWindow p = new ProgressWindow();
		p.setClosedEvent(new Runnable(){
			@Override
			public void run() {
//				System.exit(0);
			}
		});
		p.setVisible(true);
		p.setProgress(100);
		p.setLabelText("ラベルテキスト");
	}
}
