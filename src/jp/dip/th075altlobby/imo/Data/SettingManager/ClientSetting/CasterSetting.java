package jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting;

/**
 * <h1>CasterSetting</h1> <h2>Casterの設定をやり取りするクラス</h2>
 * <p>
 * Casterの設定をやり取りに使用します。
 * </p>
 * <p>
 * このクラスのインスタンスは不可変です。複数のスレッドから安全に読み出すことができます。
 * </p>
 * 
 * @author Cyrill
 */
public class CasterSetting extends Setting {
    // Caster Setting
    private final String caster_path;
    private final Integer port;

    public CasterSetting(String casterPath, int port) {
        this.caster_path = casterPath;
        this.port = port;
    }

    /**
     * <h1>CasterSetting</h1> <h2>Casterの設定をやり取りするクラスのインスタンスを生成する</h2>
     * <p>
     * Casterの設定をやり取りに使用します。
     * </p>
     * <p>
     * このクラスのインスタンスは不可変です。複数のスレッドから安全に読み出すことができます。
     * </p>
     * 
     * @param casterPath
     *            casterのパス
     * @param port
     *            Port番号を表す文字列
     */
    public CasterSetting(String casterPath, String port) {
        this.caster_path = casterPath;
        this.port = toInt(port, 7000);
    }

    public String getCasterPath() {
        return caster_path;
    }

    public Integer getPort() {
        return port;
    }
}
