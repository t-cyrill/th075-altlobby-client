package jp.dip.th075altlobby.imo.Window.SettingWindow;

import java.awt.BorderLayout;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import jp.dip.th075altlobby.imo.Constants.ClientConstants;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ClientSetting;
import jp.dip.th075altlobby.imo.Resource.Resource;
import jp.dip.th075altlobby.imo.System.Icon.WindowIcon;

public class SettingWindow extends JFrame {
	/**
	 * 生成シリアルバージョン(やり取りしない)
	 */
	private static final long serialVersionUID = 1L;
	private final ArrayBlockingQueue<Boolean> queue = new ArrayBlockingQueue<Boolean>(5);
	private JFrame ownerFrame;

	private final ClientSetting settingValues;

	private final ConnectionSettingPanel connectionSetting;
	private final CasterSettingPanel casterSetting;
	private final InstantMessageSettingPanel IMSetting;
	private final InstantMessageSoundSettingPanel IMSoundSetting;
	private final ButtonPanel buttonPanel;

	/**
	 * <h1>open</h1>
	 * <h2>設定ウィンドウを開く</h2>
	 * <p>設定ウィンドウを開きます。設定された結果がClientSettingとして返ります。</p>
	 * <p>このメソッドは、引数として渡されたJFrameをロックします。</p>
	 *
	 * @param owner 親ウィンドウ
	 * @return 設定されたデータ
	 */
	public ClientSetting open(JFrame owner)  {
		if(owner == null)
			throw new NullPointerException();

		final JFrame own = this;
		this.ownerFrame = owner;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ownerFrame.setEnabled(false);
				own.setVisible(true);
			}
		});

		Boolean ret = null;
		try {
			ret = queue.take();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}

		ClientSetting setting = settingValues;
		if(ret != null) {
			// OKボタンが押された場合のみtrueが入る
			if(ret.booleanValue() == true) {
				setting = new ClientSetting(
					connectionSetting.getSettingValues(),
					IMSetting.getSettingValues(),
					IMSoundSetting.getSettingValues(),
					casterSetting.getSettingValues(),
					settingValues.getWindowSetting(),
					settingValues.getTableSetting(),
					settingValues.getNGUserlist(),
					settingValues.getHostOnlyFlag()
				);
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ownerFrame.setEnabled(true);
				own.setVisible(false);
			}
		});

		return setting;
	}

	public SettingWindow(ClientSetting setting) {
		ResourceBundle resource = Resource.getBundle();

		// コンポーネントの配置とデザイン
		setSize(240, 340);
		setLocationRelativeTo(null);
		setTitle(ClientConstants.SettingWindowCaption);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		buttonPanel = new ButtonPanel();

		// タブペイン
		connectionSetting = new ConnectionSettingPanel(setting.getConnectionSetting());
		casterSetting = new CasterSettingPanel(setting.getCasterSetting());
		IMSetting = new InstantMessageSettingPanel(setting.getImSetting());
		IMSoundSetting = new InstantMessageSoundSettingPanel(setting.getImsoundSetting());

		casterSetting.registerLisners();
		IMSoundSetting.registerListeners();

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(resource.getString("config.tab.connection")	, connectionSetting);
		tabbedPane.addTab(resource.getString("config.tab.caster")		, casterSetting);
		tabbedPane.addTab(resource.getString("config.tab.IM")			, IMSetting);
		tabbedPane.addTab(resource.getString("config.tab.IMsound")		, IMSoundSetting);
		tabbedPane.setSelectedIndex(0);

		add(tabbedPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

		// ボタンのイベントリスナを追加する
		ButtonEventListener lisner = new ButtonEventListener();
		buttonPanel.getOKButton().addActionListener(lisner);
		buttonPanel.getCancelButton().addActionListener(lisner);

		WindowIcon.getInstance().setIcon(this);

		settingValues = setting;
	}

	static class ButtonPanel extends JPanel {
		/**
		 * Serial Version UID
		 */
		private static final long serialVersionUID = 1L;

		private JButton OK_Button;
		private JButton Cancel_Button;

		public ButtonPanel() {
			// TODO 自動生成されたコンストラクター・スタブ
			setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
			setPreferredSize(new Dimension(240,40));

			OK_Button = new JButton("OK");
			Cancel_Button = new JButton("CANCEL");

			add(Box.createHorizontalStrut(40));
			add(OK_Button);
			add(Box.createHorizontalStrut(20));
			add(Cancel_Button);
		}

		public JButton getOKButton(){
			return this.OK_Button;
		}

		public JButton getCancelButton(){
			return this.Cancel_Button;
		}
	}

	class ButtonEventListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			Object s = e.getSource();
			if(s.equals(buttonPanel.getOKButton()))
				queue.add(Boolean.TRUE);
			else
				queue.add(Boolean.FALSE);
		}
	}
}
