package jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting;

/**
 * <h1>ConnectionSetting</h1> <h2>接続設定を格納するクラス</h2>
 * <p>
 * クライアントの設定のうち接続設定を格納するクラスです。
 * </p>
 * <p>
 * このクラスのインスタンスは不可変です。複数のスレッドから安全に読み取ることができます。
 * </p>
 * 
 * @author Cyrill
 * 
 */
public class ConnectionSetting {
    // Connection Setting
    private final String message, name, short_msg;
    private final Boolean ip_hide_flag;

    public ConnectionSetting(String name, String message, String short_msg,
            boolean ip_hide_flag) {
        this.name = name;
        this.message = message;
        this.short_msg = short_msg;
        this.ip_hide_flag = ip_hide_flag;
    }

    /**
     * このインスタンスが保持している公開メッセージを取得する
     * 
     * @return インスタンスが保持している公開メッセージ
     */
    public String getMessage() {
        return message;
    }

    /**
     * このインスタンスが保持している公開名を取得する
     * 
     * @return インスタンスが保持している公開名
     */
    public String getName() {
        return name;
    }

    /**
     * このインスタンスが保持しているショートメッセージ
     * 
     * @return インスタンスが保持しているショートメッセージ
     */
    public String getShortMessage() {
        return short_msg;
    }

    /**
     * このインスタンスが保持しているIP非公開フラグ
     * 
     * @return インスタンスが保持しているIP非公開フラグ
     */
    public Boolean getIPHideFlag() {
        return ip_hide_flag;
    }
}
