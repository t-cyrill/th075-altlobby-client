package jp.dip.th075altlobby.imo.Window.SettingWindow;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.CasterSetting;
import jp.dip.th075altlobby.imo.Resource.Resource;

public class CasterSettingPanel extends JPanel {
    /**
     * 生成シリアルバージョン(やり取りしない)
     */
    private static final long serialVersionUID = 1L;
    private final JButton casterChooseButton;
    private final JButton casterPortCheck;
    private final JTextField casterPathTextField;
    private final JTextField casterPortTextField;
    private CasterSettingPanel own;
    private final ResourceBundle resource = Resource.getBundle();

    public CasterSettingPanel(CasterSetting setting) {

        setLayout(null);

        JLabel casterPathLabel = new JLabel(
                resource.getString("config.tab.caster.path"));
        casterPathLabel.setBounds(10, 10, 160, 20);

        casterPathTextField = new JTextField(setting.getCasterPath());
        casterPathTextField.setBounds(20, 35, 160, 20);

        casterChooseButton = new JButton("...");
        casterChooseButton.setBounds(181, 35, 20, 20);

        JLabel casterPortLabel = new JLabel(
                resource.getString("config.tab.caster.port"));
        casterPortLabel.setBounds(10, 60, 160, 20);

        casterPortTextField = new JTextField(setting.getPort().toString());
        casterPortTextField.setBounds(140, 85, 40, 20);

        casterPortCheck = new JButton("Portチェック");
        casterPortCheck.setBounds(60, 120, 120, 20);

        add(casterPathLabel);
        add(casterPathTextField);
        add(casterChooseButton);
        add(casterPortLabel);
        add(casterPortTextField);
        add(casterPortCheck);
    }

    /**
     * コンポーネントにリスナーを追加する
     */
    public void registerLisners() {
        own = this;
        casterChooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 既に設定されている場合、親パスを求め、そのディレクトリをカレントにする。
                File f = new File(casterPathTextField.getText());

                JFileChooser filechooser = new JFileChooser(f.getParent());
                filechooser.setDialogTitle(resource
                        .getString("config.tab.caster.chooser_caption"));
                CasterFilter casterfilter;
                filechooser.setFileFilter(casterfilter = new CasterFilter());
                filechooser.setAcceptAllFileFilterUsed(false);
                filechooser.addChoosableFileFilter(new CowCasterFilter());
                filechooser.addChoosableFileFilter(new RollCasterFilter());
                filechooser.setFileFilter(casterfilter);

                int selected = filechooser.showOpenDialog(own);
                if (selected == JFileChooser.APPROVE_OPTION) {
                    File file = filechooser.getSelectedFile();
                    casterPathTextField.setText(file.getPath());
                }
            }
        });

        casterPortCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int port = 7500;
                try {
                    port = Integer.parseInt(casterPortTextField.getText());
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }

                new CasterPortCheck(port).start();
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
     * このメソッドはSwingテキストコンポーネントのgetTextを呼び出します。
     * このメソッドはSwingのイベントディスパッチスレッドから呼び出さなければなりません。
     * </p>
     * 
     * @return このパネルに乗っているコンポーネントの設定値
     */
    public CasterSetting getSettingValues() {
        return new CasterSetting(casterPathTextField.getText(),
                casterPortTextField.getText());
    }

    public static class CasterFilter extends FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            if (f.getName() != null) {
                if (f.getName().equals("th075Caster.exe")) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        public String getDescription() {
            return "Caster(th075Caster.exe)";
        }
    }

    public static class CowCasterFilter extends FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            if (f.getName() != null) {
                if (f.getName().equals("CowCaster.exe")) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        public String getDescription() {
            return "CowCaster(CowCaster.exe)";
        }
    }

    public static class RollCasterFilter extends FileFilter {
        public boolean accept(File f) {
            if (f.isDirectory()) {
                return true;
            }

            if (f.getName() != null) {
                if (f.getName().equals("RollCaster.exe")) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }

        public String getDescription() {
            return "RollCaster(RollCaster.exe)";
        }
    }
}
