package jp.dip.th075altlobby.imo.Window.MainWindow;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import jp.dip.th075altlobby.imo.Component.JNaturalTextPane;
import jp.dip.th075altlobby.imo.ProcessAdapter.CallBackRunnable;

public class CasterTerminalPanel extends JPanel {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final JNaturalTextPane textArea;
	private final JTextField inputArea;
	private final CallBackRunnable runnable;

	public CasterTerminalPanel(CallBackRunnable inputedRunnable) {
		textArea = new JNaturalTextPane();
		textArea.setEditable(false);
		JScrollPane scroll = new JScrollPane(textArea);
		scroll.setPreferredSize(new Dimension(240,240));

		inputArea = new JTextField(8);
		inputArea.setPreferredSize(new Dimension(240,20));

		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0d;
		gbc.weighty = 1.0d;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets( 6, 6, 2, 6);
		layout.setConstraints(scroll, gbc);

		gbc.gridy = 1;
		gbc.weightx = 1.0d;
		gbc.weighty = 0d;
		gbc.insets = new Insets( 0, 6, 6, 6);
		layout.setConstraints(inputArea, gbc);

		add(scroll);
		add(inputArea);

		this.runnable = inputedRunnable;

		// キーイベントリスナの登録
		inputArea.addKeyListener(new TextFieldKeyEvent());
	}

	/**
	 * <h2>JTextFieldに設定されるイベントアダプター</h2>
	 * <p>このクラスのkeyPressedメソッドはイベントディスパッチスレッドから呼ばれます。</p>
	 * <p>内部で呼び出す{@link CallBackRunnable#run(String)}もイベントディスパッチスレッドで実行されます。</p>
	 *
	 * @author Cyrill
	 * @since rev31
	 */
	class TextFieldKeyEvent extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_ENTER) {
				int mod = e.getModifiersEx();
				if(mod == 0) {
					// Enter only
					runnable.run(inputArea.getText());
					inputArea.setText("");
				}
				e.consume();
			}
		}
	}

	/**
	 * <h1>append</h1>
	 * <h2>テキストを追加する</h2>
	 * <p>このコントロールのテキストを追加します。
	 * 追加したテキストの最後には改行が自動的に設定されます。</p>
	 * <h3>スレッドセーフ</h3>
	 * <p>このメソッドは{@link JNaturalTextPane#append(String)}を読み出します。<br>
	 * このメソッドは例外的にスレッドセーフです。SwingのEDT以外からでも安全に呼び出すことができます。
	 * 詳細は{@link JTextArea#append(String)}を参照してください。</p>
	 *
	 * @param s 追加するテキスト
	 * @param scroll trueの場合自動的にスクロールする
	 */
	public void append(final String s, boolean scroll) {
		textArea.append(s + "\r\n");
		if (scroll == true)
			textArea.setCaretPositionBottom();
	}
}
