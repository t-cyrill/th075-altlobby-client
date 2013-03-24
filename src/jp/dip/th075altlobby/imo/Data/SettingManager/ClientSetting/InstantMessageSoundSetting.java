package jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting;

/**
 * <h1>InstantMessageSoundSetting</h1>
 * <h2>インスタントメッセージの設定をやり取りするクラス</h2>
 * <p>インスタントメッセージのサウンド設定をやり取りするときに使用します。</p>
 * <p>このクラスのインスタンスは不可変です。複数のスレッドから安全に読み出すことができます。</p>
 *
 * @author Cyrill
 */
public class InstantMessageSoundSetting extends Setting {
	private final Boolean 	wholeIMPlaySound,
							toMePlaySound,
							nonIMSoundIfPlaying,
							nonIMPlayingIfWindowFocused;

	private final String	wavePath,
							toMeWavePath;

	public InstantMessageSoundSetting(
		boolean wholeIMPlaySound,
		String wavePath,
		boolean toMePlaySound,
		String toMeWavePath,
		boolean nonIMSoundIfPlaying,
		boolean nonIMPlayingIfWindowFocused
	) {
		this.wholeIMPlaySound = wholeIMPlaySound;
		this.wavePath = wavePath;
		this.toMePlaySound = toMePlaySound;
		this.toMeWavePath = toMeWavePath;
		this.nonIMSoundIfPlaying = nonIMSoundIfPlaying;
		this.nonIMPlayingIfWindowFocused = nonIMPlayingIfWindowFocused;
	}

	public InstantMessageSoundSetting(
		String wholeIMPlaySound,
		String wavePath,
		String toMePlaySound,
		String toMeWavePath,
		String nonIMSoundIfPlaying,
		String nonIMPlayingIfWindowFocused
	) {
		this.wholeIMPlaySound = Boolean.valueOf(wholeIMPlaySound);
		this.wavePath = wavePath;
		this.toMePlaySound = Boolean.valueOf(toMePlaySound);
		this.toMeWavePath = toMeWavePath;
		this.nonIMSoundIfPlaying = Boolean.valueOf(nonIMSoundIfPlaying);
		this.nonIMPlayingIfWindowFocused = Boolean.valueOf(nonIMPlayingIfWindowFocused);
	}

	public Boolean getWholeIMPlaySound() {
		return wholeIMPlaySound;
	}

	public Boolean getToMePlaySound() {
		return toMePlaySound;
	}

	public Boolean getNonIMSoundIfPlaying() {
		return nonIMSoundIfPlaying;
	}

	public Boolean getNonIMPlayingIfWindowFocused() {
		return nonIMPlayingIfWindowFocused;
	}

	public String getWavePath() {
		return wavePath;
	}

	public String getToMeWavePath() {
		return toMeWavePath;
	}
}
