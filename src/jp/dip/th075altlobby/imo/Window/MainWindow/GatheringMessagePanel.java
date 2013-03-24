package jp.dip.th075altlobby.imo.Window.MainWindow;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

import jp.dip.th075altlobby.imo.Component.JNaturalTextPane;

public class GatheringMessagePanel extends JPanel {
	/**
	 * 生成シリアルバージョン(やり取りしない)
	 */
	private static final long serialVersionUID = 1L;
	private final JNaturalTextPane gathering_message;

	/**
	 * 募集文を表示するパネルのインスタンスを生成する
	 */
	public GatheringMessagePanel() {
		// 募集文の表示エリア
		gathering_message = new JNaturalTextPane();
		gathering_message.setEditable(false);
		JScrollPane scroll = new JScrollPane(gathering_message);
		scroll.setPreferredSize(new Dimension(240,240));

		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.weightx = 1.0d;
		gbc.weighty = 1.0d;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(10,10,10,10);
		layout.setConstraints(scroll, gbc);
		add(scroll);

		setCaretPolicy(false);
	}

	/**
	 * <h1>setCaretPolicy</h1>
	 * <h2>TextPaneのキャレット移動ポリシーを変更する</h2>
	 * <p>内部で保持しているTextPaneのキャレット移動ポリシーを変更します。</p>
	 * <h3>スレッドセーフ</h3>
	 * <p>このメソッドはSwingのイベントディスパッチスレッド以外から呼び出すことはできません。</p>
	 *
	 * @param flag キャレットを移動させるかどうか、trueの場合末尾移動する
	 */
	public void setCaretPolicy(boolean flag) {
		// TODO 自動生成されたメソッド・スタブ
		int policy = flag ? DefaultCaret.ALWAYS_UPDATE : DefaultCaret.NEVER_UPDATE;

		DefaultCaret c = (DefaultCaret) gathering_message.getInnerTextPane().getCaret();
		c.setUpdatePolicy(policy);
		gathering_message.getInnerTextPane().setCaret(c);
	}

	/**
	 * <h1>setText</h1>
	 * <h2>募集文の更新を行う</h2>
	 * <p>内部のTextPaneのテキストを変更する</p>
	 * <h3>スレッドセーフ</h3>
	 * <p>このメソッドはSwingのイベントディスパッチスレッド以外から呼び出すことはできません。</p>
	 *
	 * @param text 設定するテキスト
	 * @see JNaturalTextPane#setText(String)
	 * @see JTextComponent#setText(String)
	 */
	public void setText(String text){
		gathering_message.setText(text);
	}
}
