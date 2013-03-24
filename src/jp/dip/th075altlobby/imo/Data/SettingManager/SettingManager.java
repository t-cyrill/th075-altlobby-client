package jp.dip.th075altlobby.imo.Data.SettingManager;

import java.io.BufferedInputStream;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.html.StyleSheet;

import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.CasterSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ClientSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ConnectionSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.InstantMessageSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.InstantMessageSoundSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.TableSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.WindowSetting;
import jp.dip.th075altlobby.imo.Logger.InitLogger;

public class SettingManager {
    private static Logger logger;

    public SettingManager() {
        logger = InitLogger.initLogger("imo.Window.SettingManager");
    }

    public static void storeSetting(File f, ClientSetting setting) {
        Properties prop = new Properties();
        BufferedOutputStream bos = null;

        ConnectionSetting connectionSetting = setting.getConnectionSetting();
        InstantMessageSetting imSetting = setting.getImSetting();
        InstantMessageSoundSetting imSoundSetting = setting.getImsoundSetting();
        CasterSetting casterSetting = setting.getCasterSetting();
        WindowSetting windowSetting = setting.getWindowSetting();
        TableSetting tableSetting = setting.getTableSetting();

        try {
            // 接続設定
            prop.setProperty("name", connectionSetting.getName());
            prop.setProperty("message", connectionSetting.getMessage());
            prop.setProperty("short_msg", connectionSetting.getShortMessage());
            prop.setProperty("ip_hide_flag", connectionSetting.getIPHideFlag()
                    .toString());

            // Caster設定
            prop.setProperty("caster_path", casterSetting.getCasterPath());
            prop.setProperty("port", casterSetting.getPort().toString());

            // IM設定(1)
            prop.setProperty("im_auto_logging", imSetting.getIMAutoLogging()
                    .toString());
            prop.setProperty("old_im_read_counter", imSetting
                    .getOldIMReadCounter().toString());
            prop.setProperty("im_log_hold_counter", imSetting
                    .getIMLogHoldLimit().toString());
            prop.setProperty("im_state", imSetting.getIMState().toString());
            prop.setProperty("im_send_key", imSetting.getIMSendKey().toString());

            // IM設定(2)
            prop.setProperty("play_wave", imSoundSetting.getWholeIMPlaySound()
                    .toString());
            prop.setProperty("wave_path", imSoundSetting.getWavePath());
            prop.setProperty("tome_play_wave", imSoundSetting
                    .getToMePlaySound().toString());
            prop.setProperty("tome_wave_path", imSoundSetting.getToMeWavePath());
            prop.setProperty("non_im_playing", imSoundSetting
                    .getNonIMSoundIfPlaying().toString());
            prop.setProperty("non_im_playing_if_window_focused", imSoundSetting
                    .getNonIMPlayingIfWindowFocused().toString());

            // -- ウィンドウ
            prop.setProperty("main_x", windowSetting.getMainWindowXpos()
                    .toString());
            prop.setProperty("main_y", windowSetting.getMainWindowYPos()
                    .toString());
            prop.setProperty("main_width", windowSetting.getMainWindowWidth()
                    .toString());
            prop.setProperty("main_height", windowSetting.getMainWindowHeight()
                    .toString());
            prop.setProperty("baseSPP", windowSetting
                    .getHorizontalSplitDeviderLocation().toString());
            prop.setProperty("leftSPP", windowSetting
                    .getLeftVerticalSplitDeviderLocation().toString());
            prop.setProperty("rightSPP", windowSetting
                    .getRightVerticalSplitDeviderLocation().toString());
            prop.setProperty(
                    "rightSPP2",
                    windowSetting
                            .getRightVerticalSplitInstantMessageToCasterTerminalPaneDeviderLocation()
                            .toString());

            // -- IMコントロール
            prop.setProperty("IM_Color", windowSetting.getIM_Color().toString());
            prop.setProperty("IM_Filter", windowSetting.getIM_Filter()
                    .toString());
            prop.setProperty("console_auto_scroll", windowSetting
                    .getAutoScroll().toString());

            // -- テーブル
            prop.setProperty("table_headers_order",
                    tableSetting.getTable_headers_order());
            prop.setProperty("table_name_width", tableSetting
                    .getTable_name_width().toString());
            prop.setProperty("table_ip_width", tableSetting.getTable_ip_width()
                    .toString());
            prop.setProperty("table_uid_width", tableSetting
                    .getTable_uid_width().toString());
            prop.setProperty("table_state_width", tableSetting
                    .getTable_state_width().toString());
            prop.setProperty("table_im_width", tableSetting.getTable_im_width()
                    .toString());
            prop.setProperty("table_short_msg_width", tableSetting
                    .getTable_short_msg_width().toString());

            // -- NGユーザー
            prop.setProperty("NGUserlist", setting.getNGUserlist());

            // -- ホストオンリーの状態
            prop.setProperty("HostOnly",
                    String.valueOf(setting.getHostOnlyFlag()));

            bos = new BufferedOutputStream(new FileOutputStream(f));
            prop.storeToXML(bos, "config file for Alt Lobby System b1");
            bos.close();
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "ファイルが見つかりませんでした。以下にトレースを示します。", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO例外が発生しました。以下にトレースを示します。", e);
            try {
                if (bos != null)
                    bos.close();
            } catch (IOException e2) {
                // TODO: handle exception
                logger.log(Level.SEVERE, "IO例外が発生しました。以下にトレースを示します。", e2);
            }
        }
    }

    public static ClientSetting getDefaultSetting() {
        return new ClientSetting();
    }

    /**
     * <h1>loadSetting</h1> <h2>設定をファイルから読み取りClientSettingとして返す</h2>
     * <p>
     * 設定をXMLファイルからファイルから読み取り結果をClientSettingに格納して返します。
     * </p>
     * 
     * @param f
     *            読み取る設定ファイル
     * @return 読み取った設定値
     * @throws FileNotFoundException
     *             ファイルが見つからない場合
     * @throws IOException
     *             入出力エラーが発生した場合
     * @throws InvalidPropertiesFormatException
     *             入力ストリーム上のデータが、要求されたドキュメント型を持つ有効な XML ドキュメントにならない場合
     */
    public static ClientSetting loadSetting(File f)
            throws InvalidPropertiesFormatException, IOException {
        Properties prop = new Properties();

        BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(f));
        prop.loadFromXML(bis);

        String name = prop.getProperty("name", "名無し");
        String message = prop.getProperty("message", "自由切断です。");
        String short_msg = prop.getProperty("short_msg", "自由切断です。");
        String caster_path = prop.getProperty("caster_path", "");
        String wholeIMPlaySound = prop.getProperty("play_wave", "false");
        String toMeWavePath = prop.getProperty("tome_wave_path", "false");
        String wavePath = prop.getProperty("wave_path", "");
        String toMePlaySound = prop.getProperty("tome_play_wave", "");
        String ip_hide_flag = prop.getProperty("ip_hide_flag", "true");
        String im_auto_logging = prop.getProperty("im_auto_logging", "true");
        String nonIMSoundIfPlaying = prop
                .getProperty("non_im_playing", "false");
        String nonIMPlayingIfWindowFocused = prop.getProperty(
                "non_im_playing_if_window_focused", "true");
        String console_auto_scroll = prop.getProperty("console_auto_scroll",
                "false");

        String port = prop.getProperty("port", "7500");
        String im_state = prop.getProperty("im_state", "0");
        String IMLogHoldCounter = prop.getProperty("im_log_hold_counter", "0");
        String im_send_key = prop.getProperty("im_send_key", "0");
        String old_im_read_counter = prop.getProperty("old_im_read_counter",
                "0");

        String main_x = prop.getProperty("main_x", "0");
        String main_y = prop.getProperty("main_y", "0");
        String main_width = prop.getProperty("main_width", "800");
        String main_height = prop.getProperty("main_height", "600");
        String baseSPP = prop.getProperty("baseSPP", "250");
        String leftSPP = prop.getProperty("leftSPP", "380");
        String rightSPP = prop.getProperty("rightSPP", "380");
        String rightSPP2 = prop.getProperty("rightSPP2", "360");

        String table_headers_order = prop.getProperty("table_headers_order",
                "0,1,2,3,4,5");
        String table_name_width = prop.getProperty("table_name_width", "70");
        String table_ip_width = prop.getProperty("table_ip_width", "60");
        String table_uid_width = prop.getProperty("table_uid_width", "30");
        String table_state_width = prop.getProperty("table_state_width", "45");
        String table_im_width = prop.getProperty("table_im_width", "40");
        String table_short_msg_width = prop.getProperty(
                "table_short_msg_width", "120");

        String IM_Color = prop.getProperty("IM_Color", "0");
        String IM_Filter = prop.getProperty("IM_Filter", "0");

        String NGUserlist = prop.getProperty("NGUserlist", "");
        String hostOnlyFlag = prop.getProperty("HostOnly", "false");

        name = name.length() > 1024 ? name.substring(0, 1024) : name;
        message = message.length() > 1024 * 16 ? message
                .substring(0, 1024 * 16) : message;
        short_msg = short_msg.length() > 1024 * 4 ? short_msg.substring(0,
                1024 * 4) : short_msg;

        ConnectionSetting connectionSetting = new ConnectionSetting(name,
                message, short_msg, Boolean.valueOf(ip_hide_flag));
        InstantMessageSetting instantMessageSetting = new InstantMessageSetting(
                im_auto_logging, IMLogHoldCounter, im_state, im_send_key,
                old_im_read_counter);
        InstantMessageSoundSetting instantMessageSoundSetting = new InstantMessageSoundSetting(
                wholeIMPlaySound, wavePath, toMePlaySound, toMeWavePath,
                nonIMSoundIfPlaying, nonIMPlayingIfWindowFocused);
        CasterSetting casterSetting = new CasterSetting(caster_path, port);
        WindowSetting windowSetting = new WindowSetting(baseSPP, leftSPP,
                rightSPP, rightSPP2, main_x, main_y, main_width, main_height,
                IM_Color, IM_Filter, console_auto_scroll);
        TableSetting tableSetting = new TableSetting(table_headers_order,
                table_name_width, table_ip_width, table_uid_width,
                table_state_width, table_im_width, table_short_msg_width);

        try {
            bis.close();
        } catch (IOException e) {
        }

        return new ClientSetting(connectionSetting, instantMessageSetting,
                instantMessageSoundSetting, casterSetting, windowSetting,
                tableSetting, NGUserlist, Boolean.valueOf(hostOnlyFlag));
    }

    /**
     * スタイルシートファイルを読み込んで結果をStyleSheetとして返す
     * 
     * @param f
     *            読み込むスタイルシート
     * @throws IOException
     *             入出力例外が発生した場合
     */
    public static StyleSheet loadStyleSheet(File f) throws IOException {
        StyleSheet style = new StyleSheet();

        if (!f.exists()) {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(".from_to { background-color: #ccffcc; }");
            bw.newLine();
            bw.write(".from { color: #0000ff }");
            bw.newLine();
            bw.write(".to { color: #ff0000}");
            bw.newLine();
            bw.write(".date { color: #0000ff }");
            bw.newLine();
            bw.write(".im { margin-left: 10px; }");
            bw.newLine();
            bw.close();
        }

        if (f.canRead()) {
            BufferedReader in = new BufferedReader(new FileReader(f));
            while (true) {
                String line = in.readLine();
                if (line == null)
                    break;
                style.addRule(line);
            }
            in.close();
        }

        style.addRule("* {margin:1; padding:0;}");
        style.addRule("div { font-family: \"ＭＳ Ｐゴシック\",serif; font-size: 12pt; list-style-type: none;}");

        return style;
    }
}
