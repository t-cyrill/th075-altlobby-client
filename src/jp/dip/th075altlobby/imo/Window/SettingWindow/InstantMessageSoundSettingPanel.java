package jp.dip.th075altlobby.imo.Window.SettingWindow;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.InstantMessageSoundSetting;
import jp.dip.th075altlobby.imo.Resource.Resource;

public class InstantMessageSoundSettingPanel extends JPanel {
    /**
     * 生成シリアルバージョン(やり取りしない)
     */
    private static final long serialVersionUID = 1L;
    private final JCheckBox IMPlaySoundCheckBox;
    private final JTextField wavePathTextField;
    private final JButton waveChooseButton;
    private final JCheckBox toMeIMPlaySoundCheckBox;
    private final JTextField toMeIMPlayWavePathTextField;
    private final JCheckBox nonIMSoundPlayDuringMatchingComboBox;
    private final JCheckBox nonIMSoundPlayIfWindowForcused;
    private final JButton toMeWaveFileChooseButton;

    private final ResourceBundle resource = Resource.getBundle();

    private InstantMessageSoundSettingPanel own;

    public InstantMessageSoundSettingPanel(InstantMessageSoundSetting setting) {
        setLayout(null);

        IMPlaySoundCheckBox = new JCheckBox(
                resource.getString("config.tab.IMsound.playsound1"));
        IMPlaySoundCheckBox.setBounds(5, 10, 220, 20);
        IMPlaySoundCheckBox.setSelected(setting.getWholeIMPlaySound());

        JLabel IMSoundLabel = new JLabel(
                resource.getString("config.tab.IMsound.playsound1.wav"));
        IMSoundLabel.setBounds(30, 35, 200, 20);

        wavePathTextField = new JTextField(setting.getWavePath());
        wavePathTextField.setBounds(40, 60, 140, 20);

        waveChooseButton = new JButton("...");
        waveChooseButton.setBounds(181, 60, 20, 20);

        toMeIMPlaySoundCheckBox = new JCheckBox(
                resource.getString("config.tab.IMsound.playsound2"));
        toMeIMPlaySoundCheckBox.setBounds(5, 105, 220, 20);
        toMeIMPlaySoundCheckBox.setSelected(setting.getToMePlaySound());

        JLabel IMTomeSoundLabel = new JLabel(
                resource.getString("config.tab.IMsound.playsound2.wav"));
        IMTomeSoundLabel.setBounds(30, 130, 200, 20);

        toMeIMPlayWavePathTextField = new JTextField(setting.getToMeWavePath());
        toMeIMPlayWavePathTextField.setBounds(40, 155, 140, 20);

        toMeWaveFileChooseButton = new JButton("...");
        toMeWaveFileChooseButton.setBounds(181, 155, 20, 20);

        nonIMSoundPlayDuringMatchingComboBox = new JCheckBox(
                resource.getString("config.tab.IMsound.non_im.matching"));
        nonIMSoundPlayDuringMatchingComboBox.setBounds(5, 190, 200, 20);
        nonIMSoundPlayDuringMatchingComboBox.setSelected(setting
                .getNonIMSoundIfPlaying());

        nonIMSoundPlayIfWindowForcused = new JCheckBox(
                resource.getString("config.tab.IMsound.non_im.forcused"));
        nonIMSoundPlayIfWindowForcused.setBounds(5, 210, 200, 20);
        nonIMSoundPlayIfWindowForcused.setSelected(setting
                .getNonIMPlayingIfWindowFocused());

        add(IMPlaySoundCheckBox);
        add(IMSoundLabel);
        add(wavePathTextField);
        add(waveChooseButton);

        add(toMeIMPlaySoundCheckBox);
        add(IMTomeSoundLabel);
        add(toMeIMPlayWavePathTextField);
        add(toMeWaveFileChooseButton);

        add(nonIMSoundPlayDuringMatchingComboBox);
        add(nonIMSoundPlayIfWindowForcused);
    }

    public void registerListeners() {
        own = this;
        waveChooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // waveファイル参照
                // 既に設定されている場合、親パスを求め、そのディレクトリをカレントにする。
                File f = new File(wavePathTextField.getText());

                JFileChooser filechooser = new JFileChooser(f.getParent());
                filechooser.setDialogTitle("IMダウンロード時に鳴らすWaveファイルのパスを選択してください");
                filechooser.addChoosableFileFilter(new FileNameExtensionFilter(
                        "WAVEファイル", "wav"));
                filechooser.setAcceptAllFileFilterUsed(false);

                int selected = filechooser.showOpenDialog(own);
                if (selected == JFileChooser.APPROVE_OPTION) {
                    File file = filechooser.getSelectedFile();
                    wavePathTextField.setText(file.getPath());
                }
            }
        });

        toMeWaveFileChooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // waveファイル参照
                // 既に設定されている場合、親パスを求め、そのディレクトリをカレントにする。
                File f = new File(wavePathTextField.getText());

                JFileChooser filechooser = new JFileChooser(f.getParent());
                filechooser.setDialogTitle("IMダウンロード時に鳴らすWaveファイルのパスを選択してください");
                filechooser.addChoosableFileFilter(new FileNameExtensionFilter(
                        "WAVEファイル", "wav"));
                filechooser.setAcceptAllFileFilterUsed(false);

                int selected = filechooser.showOpenDialog(own);
                if (selected == JFileChooser.APPROVE_OPTION) {
                    File file = filechooser.getSelectedFile();
                    toMeIMPlayWavePathTextField.setText(file.getPath());
                }
            }
        });
    }

    /**
     * <h1>getSettingValues</h1>
     * <p>
     * 現在このパネル上で設定されている値を取得します
     * </p>
     * 
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはSwingテキストコンポーネントのgetText等を呼び出します。
     * このメソッドはSwingのイベントディスパッチスレッドから呼び出さなければなりません。
     * </p>
     * 
     * @return このパネルに乗っているコンポーネントの設定値
     */
    public InstantMessageSoundSetting getSettingValues() {
        return new InstantMessageSoundSetting(IMPlaySoundCheckBox.isSelected(),
                wavePathTextField.getText(),
                toMeIMPlaySoundCheckBox.isSelected(),
                toMeIMPlayWavePathTextField.getText(),
                nonIMSoundPlayDuringMatchingComboBox.isSelected(),
                nonIMSoundPlayIfWindowForcused.isSelected());
    }
}
