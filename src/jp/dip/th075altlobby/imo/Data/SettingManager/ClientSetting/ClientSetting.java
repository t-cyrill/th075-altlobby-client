package jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting;

/**
 * <h1>ClientSetting</h1>
 * <h2>クライアントアプリケーションの設定をやり取りするクラス</h2>
 * <p>クライアントアプリケーションの設定をやり取りに使用します。</p>
 * <p>このクラスのインスタンスは不可変です。複数のスレッドから安全に読み出すことができます。</p>
 *
 * @author Cyrill
 */
public class ClientSetting {
	private final ConnectionSetting clientSetting;
	private final InstantMessageSetting imSetting;
	private final InstantMessageSoundSetting imsoundSetting;
	private final CasterSetting casterSetting;
	private final WindowSetting windowSetting;
	private final TableSetting tableSetting;
	private final String ngUserlist;
	private final boolean hostOnlyFlag;

	public ClientSetting(
		ConnectionSetting c,
		InstantMessageSetting im,
		InstantMessageSoundSetting ims,
		CasterSetting cas,
		WindowSetting w,
		TableSetting t,
		String ng,
		boolean hostOnly
	) {
		clientSetting = c;
		imSetting = im;
		imsoundSetting = ims;
		casterSetting = cas;
		windowSetting = w;
		tableSetting = t;
		ngUserlist = ng;
		hostOnlyFlag = hostOnly;
	}

	/**
	 * すべてデフォルト値を使用するインスタンスを生成する
	 */
	public ClientSetting() {
		clientSetting = new ConnectionSetting("名無しさん", "切断自由です", "切断自由です", false);
		imSetting = new InstantMessageSetting(true, "1000", 0, 0, "30");
		imsoundSetting = new InstantMessageSoundSetting(false, "", false, "", true, true);
		casterSetting = new CasterSetting("", 7500);
		windowSetting = new WindowSetting(278, 360, 360, 360, 10, 10, 800, 600, 0, 0, true);
		tableSetting = new TableSetting("0,1,2,3,4,5", 56, 52, 52, 48, 28, 72);
		ngUserlist = "";
		hostOnlyFlag = false;
	}

	public ConnectionSetting getConnectionSetting() {
		return clientSetting;
	}

	public InstantMessageSetting getImSetting() {
		return imSetting;
	}

	public InstantMessageSoundSetting getImsoundSetting() {
		return imsoundSetting;
	}

	public CasterSetting getCasterSetting() {
		return casterSetting;
	}

	public WindowSetting getWindowSetting() {
		return windowSetting;
	}

	public TableSetting getTableSetting() {
		return tableSetting;
	}

	public String getNGUserlist() {
		return ngUserlist;
	}

	public boolean getHostOnlyFlag() {
		return hostOnlyFlag;
	}
}
