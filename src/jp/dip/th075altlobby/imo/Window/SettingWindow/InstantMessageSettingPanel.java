package jp.dip.th075altlobby.imo.Window.SettingWindow;

import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.InstantMessageSetting;
import jp.dip.th075altlobby.imo.Resource.Resource;

public class InstantMessageSettingPanel extends JPanel {
	/**
	 * 生成シリアルバージョン(やり取りしない)
	 */
	private static final long serialVersionUID = 1L;
	private JCheckBox IMAutoLoggingCheckBox;
	private JTextField IMLogHoldCounterTextField;
	private JTextField IMHistoryRequestLimitTextField;
	private JComboBox IMAcceptPolicyComboBox;
	private JComboBox IMSendKeyComboBox;

	private final ResourceBundle resource = Resource.getBundle();

	public InstantMessageSettingPanel(InstantMessageSetting setting) {
		setLayout(null);

		IMAutoLoggingCheckBox = new JCheckBox(resource.getString("config.tab.IM.autoLogging"));
		IMAutoLoggingCheckBox.setBounds( 20, 20, 200, 20);
		IMAutoLoggingCheckBox.setSelected(setting.getIMAutoLogging());
		IMAutoLoggingCheckBox.setToolTipText(resource.getString("config.tab.IM.autoLogging.tip"));

		JLabel IMLogHoldCounterLabel = new JLabel(resource.getString("config.tab.IM.hold_limit"));
		IMLogHoldCounterLabel.setBounds( 20, 55, 100, 20);
		IMLogHoldCounterTextField = new JTextField();
		IMLogHoldCounterTextField.setBounds( 120, 55, 60, 20);
		IMLogHoldCounterTextField.setText(setting.getIMLogHoldLimit().toString());
		IMLogHoldCounterTextField.setToolTipText(resource.getString("config.tab.IM.hold_limit.tip"));


		JLabel IMHistoryRequestLabel = new JLabel(resource.getString("config.tab.IM.history"));
		IMHistoryRequestLimitTextField = new JTextField();
		IMHistoryRequestLabel.setBounds( 20,80,140,20);
		IMHistoryRequestLimitTextField.setBounds(160,80, 20,20);
		IMHistoryRequestLimitTextField.setText(setting.getOldIMReadCounter().toString());
		IMHistoryRequestLabel.setToolTipText(resource.getString("config.tab.IM.history.tip"));

		JLabel IMStateLabel = new JLabel(resource.getString("config.tab.IM.IMPolicy"));
		IMStateLabel.setBounds( 20, 110, 160, 20);

		JLabel IMSendShortcutLabel = new JLabel("IM送信ショートカットキー");
		IMSendShortcutLabel.setBounds( 20,165,160,20);

		IMAcceptPolicyComboBox = new JComboBox();
		IMAcceptPolicyComboBox.setBounds( 30, 140, 160, 20);
		IMAcceptPolicyComboBox.addItem(resource.getString("config.tab.IM.IMPolicy.item1"));
		IMAcceptPolicyComboBox.addItem(resource.getString("config.tab.IM.IMPolicy.item2"));
		IMAcceptPolicyComboBox.addItem(resource.getString("config.tab.IM.IMPolicy.item3"));
		IMAcceptPolicyComboBox.setSelectedIndex(setting.getIMState());

		IMSendKeyComboBox = new JComboBox();
		IMSendKeyComboBox.setBounds( 30,190,160,20);
		IMSendKeyComboBox.addItem(resource.getString("config.tab.IM.sendkey.item1"));
		IMSendKeyComboBox.addItem(resource.getString("config.tab.IM.sendkey.item2"));
		IMSendKeyComboBox.setSelectedIndex(setting.getIMSendKey());

		add(IMAutoLoggingCheckBox);
		add(IMLogHoldCounterLabel);
		add(IMLogHoldCounterTextField);
		add(IMHistoryRequestLabel);
		add(IMHistoryRequestLimitTextField);
		add(IMStateLabel);
		add(IMAcceptPolicyComboBox);
		add(IMSendShortcutLabel);
		add(IMSendKeyComboBox);
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
	public InstantMessageSetting getSettingValues() {
		return new InstantMessageSetting(IMAutoLoggingCheckBox.isSelected(),
				IMLogHoldCounterTextField.getText(),
				IMAcceptPolicyComboBox.getSelectedIndex(),
				IMSendKeyComboBox.getSelectedIndex(),
				IMHistoryRequestLimitTextField.getText());
	}
}
