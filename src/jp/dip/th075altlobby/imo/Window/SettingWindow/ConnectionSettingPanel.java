package jp.dip.th075altlobby.imo.Window.SettingWindow;

import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import jp.dip.th075altlobby.imo.Component.JNaturalTextPane;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ConnectionSetting;
import jp.dip.th075altlobby.imo.Resource.Resource;

public class ConnectionSettingPanel extends JPanel {
	/**
	 * 生成シリアルバージョン(やり取りしない)
	 */
	private static final long serialVersionUID = 1L;

	private final JCheckBox IPHideCheckBox;
	private final JTextField nameTextField;
	private final JTextField shortMessageTextField;
	private JNaturalTextPane messageTextField;
	private final ResourceBundle resource = Resource.getBundle();

	public ConnectionSettingPanel(ConnectionSetting setting) {
		setLayout(null);

		JLabel nameLabel = new JLabel(resource.getString("config.tab.connection.name"));
		JLabel shortMessageLabel = new JLabel(resource.getString("config.tab.connection.short_msg"));
		JLabel messageLabel = new JLabel(resource.getString("config.tab.connection.message"));

		nameLabel.setBounds( 10, 10, 100, 20);
		shortMessageLabel.setBounds( 10, 60, 100, 20);
		messageLabel.setBounds( 10, 105, 100, 20);

		IPHideCheckBox = new JCheckBox(resource.getString("config.tab.connection.IPhide"));

		nameTextField = new JTextField(16);
		shortMessageTextField = new JTextField(16);
		messageTextField = new JNaturalTextPane();

		nameTextField.setText(setting.getName());
		nameTextField.setBounds( 14, 35, 200, 20);
		messageTextField.getInnerTextPane().setText(setting.getMessage());
		shortMessageTextField.setBounds(14,80,200,20);
		shortMessageTextField.setText(setting.getShortMessage());

		IPHideCheckBox.setSelected(setting.getIPHideFlag());

		JScrollPane scroll = new JScrollPane(messageTextField);
		scroll.setBounds( 14, 125, 200, 80);

		IPHideCheckBox.setBounds( 10, 210, 160, 20);

		add(nameLabel);
		add(nameTextField);
		add(shortMessageLabel);
		add(shortMessageTextField);
		add(IPHideCheckBox);

		add(messageLabel);
		add(scroll);
	}

	/**
	 * <h1>getSettingValues</h1>
	 * <p>現在このパネル上で設定されている値を取得します</p>
	 *
	 * <h3>スレッドセーフ</h3>
	 * <p>このメソッドはSwingテキストコンポーネントのgetText等を呼び出します。
	 * このメソッドはSwingのイベントディスパッチスレッドから呼び出さなければなりません。</p>
	 *
	 * @return このパネルに乗っているコンポーネントの設定値
	 */
	public ConnectionSetting getSettingValues() {
		return new ConnectionSetting(nameTextField.getText(), messageTextField.getText(), shortMessageTextField.getText(), IPHideCheckBox.isSelected());
	}
}
