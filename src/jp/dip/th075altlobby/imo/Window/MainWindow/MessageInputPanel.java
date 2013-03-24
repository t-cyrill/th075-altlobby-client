package jp.dip.th075altlobby.imo.Window.MainWindow;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ResourceBundle;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import jp.dip.th075altlobby.imo.Component.JNaturalTextPane;
import jp.dip.th075altlobby.imo.Component.JWheelableComboBox;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ClientSetting;
import jp.dip.th075altlobby.imo.Data.communication.InstantMessage;
import jp.dip.th075altlobby.imo.Data.communication.UserInfo;
import jp.dip.th075altlobby.imo.Resource.Resource;
import jp.dip.th075altlobby.imo.Window.EventAdapter.RunnableCallActionListener;

public class MessageInputPanel extends JPanel {
	/**
	 * 生成シリアルバージョン(やり取りしない)
	 */
	private static final long serialVersionUID = 1L;
	private final innerTopPanel topPanel;
	private final innerCenterPanel centerPanel;
	private final innerBottomPanel bottomPanel;

	private final ResourceBundle resource = Resource.getBundle();

	public MessageInputPanel(ClientSetting setting) {
		// コンストラクタ
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(320, 40));

		topPanel = new innerTopPanel(setting);
		centerPanel = new innerCenterPanel(setting);
		bottomPanel = new innerBottomPanel();

		add(topPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}

	public void registerListeners(
			Runnable enterPushedInInnerTextAreaRunnable,
			Runnable gatherButtonPushedRunnable,
			Runnable gatherSwitchButtonPushedRunnable,
			Runnable closeCasterButtonPushedRunnable) {
		centerPanel.registerListeners(enterPushedInInnerTextAreaRunnable);
		bottomPanel.registerListeners(
			gatherButtonPushedRunnable,
			gatherSwitchButtonPushedRunnable,
			closeCasterButtonPushedRunnable
		);
	}

	/**
	 * 内部で保持しているJTextAreaのgetTextを呼び出します。
	 *
	 * @return 内部テキストペインの文字列
	 * @see innerCenterPanel#getText()
	 * @see JNaturalTextPane#getText()
	 * @see JTextComponent#getText()
	 */
	public String getText() {
		return centerPanel.getText();
	}

	public int getSelectedColor() {
		return topPanel.getSelectedColor();
	}

	public boolean isWholeIMCheckSelected() {
		return topPanel.isWholeIMCheckSelected();
	}

	public void setNameFieldText(String name) {
		topPanel.setText(name);
	}

	/**
	 * 内部のボタン状態を切り替える
	 * <h3>スレッドセーフ</h3>
	 * <p>このメソッドは {@link SwingUtilities#invokeLater(Runnable)} を呼び出します。
	 * Swingのイベントディスパッチスレッド以外からでも呼び出すことができます。
	 * </p>
	 * @param i
	 * 0 : 募集ボタンのみ有効
	 * 1 : 閉じるボタンのみ有効
	 * 2 : 両方のボタンを無効にする
	 */
	public void switchButton(int i) {
		bottomPanel.switchButton(i);
	}

	/**
	 * <h1>setText</h1>
	 * <h2>JTextComponent#setTextを呼び出す</h2>
	 * <p>内部で保持しているJTextAreaのsetTextを呼び出します。</p>
	 * <h3>スレッドセーフ</h3>
	 * <p>このメソッドはSwingのイベントディスパッチスレッド以外から呼び出すことはできません。</p>
	 *
	 * @return 内部テキストペインの文字列
	 * @see innerCenterPanel#setText(String)
	 * @see JNaturalTextPane#setText(String)
	 * @see JTextComponent#setText(String)
	 */
	public void setText(String t) {
		centerPanel.setText(t);
	}

	/**
	 * <h1>setUserStateTextLabelState</h1>
	 * <h2>ラベルテキストを変更する</h2>
	 * <p>ユーザー状態を表すラベルのテキストを変更します。</p>
	 * <p>このメソッドで設定されるテキストは{@link UserInfo#getStateString(int)}に依存します。</p>
	 * <h3>スレッドセーフ</h3>
	 * <p>このメソッドはSwingのイベントディスパッチスレッド以外からでも呼び出すことができます。</p>
	 *
	 * @param state ユーザーの状態
	 */
	public void setUserStateTextLabelState(final byte state) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				bottomPanel.setUserStateTextLabelState(state);
			}
		});
	}
	
	public void setGatherButtonText(boolean hostOnlyFlag) {
		bottomPanel.setGatherButtonText(hostOnlyFlag);
	}

	class innerTopPanel extends JPanel {
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private final JTextField toIMUser;
		private final JCheckBox wholeIMCheck;
		private final JWheelableComboBox IMColor;


		public innerTopPanel(ClientSetting setting) {
			GridBagLayout layout = new GridBagLayout();
			setLayout(layout);

			JLabel toIMLabel = new JLabel(resource.getString("main.MessageInput.toIM"));
			wholeIMCheck = new JCheckBox(resource.getString("main.MessageInput.wholeIM"));
			toIMUser = new JTextField(16);
			Component dummy = Box.createGlue();
			toIMUser.setEditable(false);
			toIMUser.setMinimumSize(new Dimension(92,20));
			wholeIMCheck.setSelected(true);

			Dimension d = new Dimension(100,20);
			/* IMの色選択用コンボボックスのモデルとデータ */
			DefaultComboBoxModel colorModel = new DefaultComboBoxModel();
			for (int i = 0 ; i < InstantMessage.color_set.length; i++){
				/* 色つきのラベルを作成する */
				StringBuffer sb = new StringBuffer();
				sb.append("<html><font color=\"");
				sb.append(InstantMessage.color_set[i]);
				sb.append("\">■ "+InstantMessage.color_set[i]+"</font></html>");

				colorModel.addElement(new String(sb));
			}

			IMColor = new JWheelableComboBox(colorModel);
			IMColor.listener_init();
			IMColor.setMinimumSize(d);
			IMColor.setMaximumSize(d);
			IMColor.setPreferredSize(d);
			IMColor.setSelectedIndex(setting.getWindowSetting().getIM_Color());

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(10,10,10,10);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0d;
			gbc.weighty = 0d;
			gbc.fill = GridBagConstraints.NONE;
			layout.setConstraints(toIMLabel, gbc);

			gbc.gridx = 1;
			layout.setConstraints(toIMUser, gbc);

			gbc.gridx = 2;
			layout.setConstraints(wholeIMCheck, gbc);

			gbc.gridx = 3;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			layout.setConstraints(IMColor, gbc);

			gbc.gridx = 4;
			gbc.weightx = 1.0d;
			layout.setConstraints(dummy, gbc);

			add(toIMLabel);
			add(toIMUser);
			add(wholeIMCheck);
			add(IMColor);

			add(dummy);
		}

		public void setText(String name) {
			toIMUser.setText(name);
			toIMUser.setCaretPosition(0);
		}

		public int getSelectedColor() {
			return IMColor.getSelectedIndex();
		}

		public boolean isWholeIMCheckSelected() {
			return wholeIMCheck.isSelected();
		}
	}

	static class innerCenterPanel extends JPanel {
		/**
		 *
		 */
		private static final long serialVersionUID = -7715839730169322455L;
		private final JNaturalTextPane IMInputArea;
		private final int sendIMKey;

		public innerCenterPanel(ClientSetting setting) {
			GridBagLayout layout = new GridBagLayout();
			setLayout(layout);

			IMInputArea = new JNaturalTextPane();
			JScrollPane scroll = new JScrollPane(IMInputArea);

			scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(10,10,10,10);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1.0d;
			gbc.weighty = 1.0d;
			gbc.fill = GridBagConstraints.BOTH;
			layout.setConstraints(scroll, gbc);

			sendIMKey = setting.getImSetting().getIMSendKey();

			add(scroll);
		}

		/**
		 * 内部で保持しているJTextAreaのgetTextを呼び出します。
		 *
		 * @return 内部テキストペインの文字列
		 * @see JNaturalTextPane#getText()
		 * @see JTextComponent#getText()
		 */
		public String getText() {
			return IMInputArea.getText();
		}

		/**
		 * 内部で保持しているJTextAreaのsetTextを呼び出します。
		 *
		 * @return 内部テキストペインの文字列
		 * @see JNaturalTextPane#setText(String)
		 * @see JTextComponent#setText(String)
		 */
		public void setText(String t) {
			IMInputArea.setText(t);
		}

		public void registerListeners(final Runnable enterPushedInInnerTextAreaRunnable) {
			IMInputArea.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						boolean send_flag = false;
						int mod = e.getModifiersEx();

						if((sendIMKey == 0) && (mod == 0)) {
							// Enter only
							send_flag = true;
						} else if((sendIMKey == 1) && ((mod & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK)){
							// Shift + Enter
							send_flag = true;
						}

						if(send_flag)
							enterPushedInInnerTextAreaRunnable.run();
						else
							IMInputArea.newline();

						e.consume();
					}
				}
			});
		}
	}

	class innerBottomPanel extends JPanel{
		/**
		 *
		 */
		private static final long serialVersionUID = 1L;
		private final JPanel gatherButtonBasePanel;
		private final JButton gatherButton;
		private final JButton gatherSwitchButton;
		private final JLabel stateLabel;
		private final JButton closeCasterButton;

		public innerBottomPanel() {
			GridBagLayout layout = new GridBagLayout();
			setLayout(layout);
			
			gatherButtonBasePanel = new JPanel();

			gatherButton 		= new JButton(resource.getString("main.MessageInput.button.gather"));
			gatherSwitchButton 	= new JButton("..");
			stateLabel 			= new JLabel(UserInfo.getStateString(UserInfo.WAITING), JLabel.CENTER);
			closeCasterButton 	= new JButton(resource.getString("main.MessageInput.button.close"));
			closeCasterButton.setEnabled(false);
			
			gatherButton.setMinimumSize(new Dimension(120, 30));
			gatherButton.setMaximumSize(new Dimension(120, 30));
			gatherButton.setPreferredSize(new Dimension(120, 30));
			gatherSwitchButton.setSize(30, 30);
			gatherSwitchButton.setPreferredSize(new Dimension(30, 30));

			stateLabel.setMinimumSize(new Dimension(60, 30));
			stateLabel.setMaximumSize(new Dimension(60, 30));
			stateLabel.setPreferredSize(new Dimension(60, 30));
			
			GridBagLayout layoutBase = new GridBagLayout();
			gatherButtonBasePanel.setLayout(layoutBase);

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.weightx = 1.0d;
			gbc.weighty = 0d;
			gbc.gridx = 0;
			gbc.gridwidth = 1;
			gbc.ipady = 10;
			gbc.insets = new Insets(10, 20, 10, 0);
			gbc.fill = GridBagConstraints.HORIZONTAL;
			layoutBase.setConstraints(gatherButton, gbc);

			gbc.weightx = 0d;
			gbc.weighty = 0d;
			gbc.gridx = 1;
			gbc.gridwidth = 1;
			gbc.ipadx = 0;
			gbc.ipady = 10;
			gbc.insets = new Insets(0, -5, 0, 0);
			gbc.fill = GridBagConstraints.NONE;
			layoutBase.setConstraints(gatherSwitchButton, gbc);
			
			gatherButton.setToolTipText("対戦を募集します");
			closeCasterButton.setToolTipText("募集をやめ、Casterを終了します");
			gatherButtonBasePanel.add(gatherButton);
			gatherButtonBasePanel.add(gatherSwitchButton);

			gbc.weightx = 1.0d;
			gbc.weighty = 0d;
			gbc.gridwidth = 1;
			gbc.ipadx = 10;
			gbc.ipady = 10;
			gbc.insets = new Insets(0, 0, 0, 0);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			layout.setConstraints(gatherButtonBasePanel, gbc);

			gbc.gridx = 2;
			gbc.insets = new Insets(10, 20, 10, 20);
			layout.setConstraints(closeCasterButton, gbc);

			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.ipadx = 0;
			gbc.ipady = 0;
			gbc.insets = new Insets(0, 0, 5, 0);
			// stateLabel.setText("");
			// ImageIcon icon1 = new ImageIcon("./gathering.png");
			// stateLabel.setIcon(icon1);
			layout.setConstraints(stateLabel, gbc);

			add(gatherButtonBasePanel);
			add(stateLabel);
			add(closeCasterButton);
		}

		public void registerListeners(
				Runnable gatherButtonPushedRunnable,
				Runnable gatherSwitchButtonPushedRunnable,
				Runnable closeCasterButtonPushedRunnable
		)
		{
			gatherButton.addActionListener(new RunnableCallActionListener(gatherButtonPushedRunnable));
			gatherSwitchButton.addActionListener(new RunnableCallActionListener(gatherSwitchButtonPushedRunnable));
			closeCasterButton.addActionListener(new RunnableCallActionListener(closeCasterButtonPushedRunnable));
		}

		/**
		 * 内部のボタン状態を切り替える
		 *
		 * @param i
		 * 0 : 募集ボタンのみ有効
		 * 1 : 閉じるボタンのみ有効
		 * 2 : 両方のボタンを無効にする
		 */
		public void switchButton(int i) {
			switch(i) {
				case 0:
					gatherButton.setEnabled(true);
					gatherSwitchButton.setEnabled(true);
					closeCasterButton.setEnabled(false);
					break;
				case 1:
					gatherButton.setEnabled(false);
					gatherSwitchButton.setEnabled(false);
					closeCasterButton.setEnabled(true);
					break;
				case 2:
					gatherButton.setEnabled(false);
					gatherSwitchButton.setEnabled(false);
					closeCasterButton.setEnabled(false);
					break;
				default:
					break;
			}
		}

		/**
		 * <h1>setUserStateTextLabelState</h1>
		 * <h2>ラベルテキストを変更する</h2>
		 * <p>ユーザー状態を表すラベルのテキストを変更します。</p>
		 * <p>このメソッドで設定されるテキストは{@link UserInfo#getStateString(int)}に依存します。</p>
		 * <h3>スレッドセーフ</h3>
		 * <p>このメソッドはSwingのイベントディスパッチスレッド以外から呼び出すことはできません。</p>
		 * @param state ユーザーの状態
		 */
		public void setUserStateTextLabelState(byte state) {
			stateLabel.setText(UserInfo.getStateString(state));
		}
		
		public void setGatherButtonText(boolean hostOnlyFlag) {
			String text;
			if (hostOnlyFlag) {
				text = resource.getString("main.MessageInput.button.gatherHostOnly");
			} else {
				text = resource.getString("main.MessageInput.button.gather");				
			}
			gatherButton.setText(text);
		}
	}


}