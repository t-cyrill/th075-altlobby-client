package jp.dip.th075altlobby.imo.Constants;

/**
 * クライアントアプリケーションが使う各種定数を定義します
 * 
 * @author Cyrill
 */
public interface ClientConstants {
    /**
     * 接続ウィンドウのキャプション文字列
     */
    final Object ClientVersionString = "OP rev45";
    final String ConnectionWindowCaption = "東方萃夢想 Alt Lobby Client ("
            + ClientVersionString + ")";
    final String SettingWindowCaption = "設定";
    final String MainWindowCaption = "東方萃夢想 Alt Lobby Client ("
            + ClientVersionString + ")";

    final short PROTOCOL_VERSION = 4;
    final int RECONNECT_TIME_DELAY = 10;
    final int KEEP_ALIVE_TIME_FIRST_DELAY = 5;
    final int KEEP_ALIVE_TIME_DELAY = 15;
    final int SOCKET_CLOSE_TIME_DELAY = 20;
}
