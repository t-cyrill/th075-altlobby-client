package jp.dip.th075altlobby.imo.Data.InstantMessageProvideManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.dip.th075altlobby.imo.Data.communication.InstantMessage;

public class PlainTextInstantMessageFormatter implements InstantMessageFormatter {
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

	@Override
	public String format(InstantMessage IM) {
		return sdf.format(new Date(IM.getTime())) + " " + IM.getFrom() + " -> " + IM.getTo() + " : " + IM.getMessage();
	}

	/**
	 * <h1>format</h1>
	 * <h2>IM配列をプレーンテキストの形でフォーマットし、文字列として返す。</h2>
	 * <h3>スレッドセーフ性</h3>
	 * <p>このメソッドは同期化されません。並行スレッドから安全に呼び出すことはできません。</p>
	 */
	@Override
	public String format(InstantMessage[] IMArray) {
		StringBuilder sb = new StringBuilder();
		for(InstantMessage im : IMArray) {
			sb.append(format(im));
			sb.append(System.getProperty("line.separator"));
		}
		return sb.toString();
	}
}
