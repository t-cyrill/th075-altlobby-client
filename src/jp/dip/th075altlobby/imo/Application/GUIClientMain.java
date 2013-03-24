package jp.dip.th075altlobby.imo.Application;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

import java.util.InvalidPropertiesFormatException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import jp.dip.th075altlobby.imo.Constants.ClientConstants;
import jp.dip.th075altlobby.imo.Data.SettingManager.ServerListLoader;
import jp.dip.th075altlobby.imo.Data.SettingManager.SettingManager;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ClientSetting;
import jp.dip.th075altlobby.imo.Resource.Resource;
import jp.dip.th075altlobby.imo.Window.ConnectionWindow.ConnectionWindow;
import jp.dip.th075altlobby.imo.Window.MainWindow.MainWindow;
import jp.dip.th075altlobby.imo.Window.SettingWindow.SettingWindow;

public class GUIClientMain implements Runnable {
    private ConnectionWindow window;
    private final String[] server_list;
    private final File settingFile = new File("config_lobby_sys_c.xml");
    private final AtomicBoolean opened = new AtomicBoolean(false);

    private final Logger logger = Logger.getLogger("jp.dip.th075altlobby");
    private final ResourceBundle resource = Resource.getBundle();

    public GUIClientMain() {
        String[] temp;
        try {
            temp = ServerListLoader.load("server_list.txt");
        } catch (FileNotFoundException e) {
            temp = new String[1];
            temp[0] = "localhost(localhost:9555)";
        } catch (IOException e) {
            temp = new String[1];
            temp[0] = "localhost(localhost:9555)";
        }
        server_list = temp;
        window = new ConnectionWindow(server_list,
                new connectionButtonRunnable(), new settingButtonRunnable());
    }

    public static void main(String[] args) {
        try {
            UIManager
                    .setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
        }
        ExecutorService exs = Executors.newCachedThreadPool();
        exs.execute(new GUIClientMain());
        exs.shutdown();
    }

    @Override
    public void run() {
        checkClientVersion();
        window.setVisible(true);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void checkClientVersion() {
        try {
            URL clientVersionFile = new URL(
                    "http://th075altlobby.dip.jp/client_version.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    clientVersionFile.openConnection().getInputStream()));
            String line = br.readLine();
            br.close();

            if (line != null
                    && !line.equals(ClientConstants.ClientVersionString)) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        String[] message = {
                                resource.getString("connection.dialog.version_check.line1"),
                                resource.getString("connection.dialog.version_check.line2") };
                        if (JOptionPane.showConfirmDialog(
                                null,
                                message,
                                resource.getString("connection.dialog.version_check.caption"),
                                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            Desktop desktop = Desktop.getDesktop();
                            try {
                                desktop.browse(new URI(
                                        "http://th075altlobby.dip.jp/"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (URISyntaxException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "URLが解決できませんでした。", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO例外が発生しました。", e);
        }
    }

    private void errorReport(final int status) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String[] errorMessage = new String[3];
                switch (status) {
                case -1:
                    // 初回接続エラー
                    errorMessage[0] = resource
                            .getString("connection.errorReport.ioException.e1");
                    break;
                case -2:
                    // バージョン不一致エラー
                    errorMessage[0] = resource
                            .getString("connection.errorReport.versionMisMatch");
                    break;
                case -3:
                    // 通信スレッド内IO例外
                    errorMessage[0] = resource
                            .getString("connection.errorReport.ioException.e2");
                    break;
                case -5:
                    // InputThread内IO例外
                    errorMessage[0] = resource
                            .getString("connection.errorReport.ioException.e5");
                    break;

                case -20:
                    errorMessage[0] = resource
                            .getString("connection.errorReport.ioException.e3");
                    break;
                case -21:
                    errorMessage[0] = resource
                            .getString("connection.errorReport.ioException.e4.line1");
                    errorMessage[1] = resource
                            .getString("connection.errorReport.ioException.e4.line2");
                    break;
                case -22:
                    errorMessage[0] = resource
                            .getString("connection.errorReport.ioException.ex");
                    break;
                default:

                }

                if (status < 0)
                    JOptionPane.showMessageDialog(
                            window,
                            errorMessage,
                            resource.getString("connection.errorReport.caption"),
                            JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * <h1>connectionButtonRunnable</h1> <h2>接続ボタンが押されたときに走るRunnable</h2>
     * <p>
     * ConnectionWindowの接続ボタンが押されたときに走るRunnableです。
     * </p>
     * <p>
     * このRunnableはSwingのイベントディスパッチスレッドから呼ばれません。
     * </p>
     * 
     * @author Cyrill
     */
    class connectionButtonRunnable implements Runnable {
        @Override
        public void run() {
            if (!opened.get()) {
                synchronized (this) {
                    opened.set(true);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            window.setWindowVisible(false);
                            String SelectedServerString = window
                                    .getSelectedString();
                            String tempString = SelectedServerString
                                    .substring(SelectedServerString
                                            .indexOf("(") + 1);
                            tempString = tempString.substring(0,
                                    tempString.indexOf(")"));

                            final String hostaddr = tempString.substring(0,
                                    tempString.indexOf(":"));
                            final int hostport = Integer.valueOf(tempString
                                    .substring(tempString.indexOf(":") + 1));

                            ExecutorService exs = Executors
                                    .newCachedThreadPool();
                            exs.execute(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        logger.log(Level.INFO,
                                                "before mainWindow created");
                                        MainWindow mainWindow = new MainWindow(
                                                hostaddr, hostport,
                                                loadSetting(), window);
                                        mainWindow.registerListener();
                                        int status = mainWindow.open();
                                        if (status == 1)
                                            System.exit(0);
                                        errorReport(status);
                                    } catch (UnknownHostException e) {
                                        errorReport(-20);
                                    } catch (ConnectException e) {
                                        errorReport(-21);
                                    } catch (IOException e) {
                                        logger.log(
                                                Level.WARNING,
                                                "mainWindow.open前にIO例外が発生しました。",
                                                e);
                                        errorReport(-22);
                                    } finally {
                                        opened.set(false);
                                        window.setWindowVisible(true);
                                    }
                                }
                            });
                            exs.shutdown();
                        }
                    });
                }
            }
        }
    }

    /**
     * <h1>settingButtonRunnable</h1> <h2>設定ボタンが押されたときに走るRunnable</h2>
     * <p>
     * ConnectionWindowの設定ボタンが押されたときに走るRunnableです。
     * </p>
     * <p>
     * このRunnableはSwingのイベントディスパッチスレッドから呼ばれません。
     * </p>
     * 
     * @author Cyrill
     */
    class settingButtonRunnable implements Runnable {
        @Override
        public void run() {
            if (!opened.get()) {
                synchronized (this) {
                    opened.set(true);
                    ClientSetting setting = loadSetting();

                    SettingWindow settingWindow = new SettingWindow(setting);

                    setting = settingWindow.open(window);
                    SettingManager.storeSetting(settingFile, setting);
                    opened.set(false);
                }
            }
        }
    }

    private ClientSetting loadSetting() {
        ClientSetting setting = null;
        try {
            // 設定のロード
            setting = SettingManager.loadSetting(settingFile);
        } catch (InvalidPropertiesFormatException e) {
            logger.log(Level.WARNING, "設定ファイルのフォーマットが異常です。", e);
        } catch (IOException e) {
            logger.log(Level.WARNING, "設定ファイルの読み込みエラーが発生しました。", e);
        }

        if (setting == null)
            setting = SettingManager.getDefaultSetting();

        return setting;
    }
}