package jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting;

/**
 * <h1>InstantMessageSetting</h1> <h2>インスタントメッセージの設定をやり取りするクラス</h2>
 * <p>
 * インスタントメッセージの設定をやり取りするときに使用します。
 * </p>
 * <p>
 * このクラスのインスタンスは不可変です。複数のスレッドから安全に読み出すことができます。
 * </p>
 * 
 * @author Cyrill
 */
public class InstantMessageSetting extends Setting {
    // IM Setting
    private final Boolean imAutoLogging;

    private final Integer IMLogHoldLimit, IMState, IMSendKey, oldIMReadCounter;

    public InstantMessageSetting(boolean imAutoLogging, String IMLogHoldLimit,
            int IMState, int IMSendKey, String oldIMReadCounter) {
        this.imAutoLogging = imAutoLogging;
        this.IMLogHoldLimit = toInt(IMLogHoldLimit, 1000);
        this.IMState = IMState;
        this.IMSendKey = IMSendKey;
        this.oldIMReadCounter = toInt(oldIMReadCounter, 30);
    }

    public InstantMessageSetting(String imAutoLogging, String IMLogHoldLimit,
            String IMState, String IMSendKey, String oldIMReadCounter) {

        this.imAutoLogging = Boolean.valueOf(imAutoLogging);
        this.IMLogHoldLimit = toInt(IMLogHoldLimit, 1000);
        this.IMState = toInt(IMState, 0);
        this.IMSendKey = toInt(IMSendKey, 0);
        this.oldIMReadCounter = toInt(oldIMReadCounter, 30);
    }

    public Boolean getIMAutoLogging() {
        return imAutoLogging;
    }

    public Integer getIMLogHoldLimit() {
        return IMLogHoldLimit;
    }

    public Integer getIMState() {
        return IMState;
    }

    public Integer getIMSendKey() {
        return IMSendKey;
    }

    public Integer getOldIMReadCounter() {
        return oldIMReadCounter;
    }
}
