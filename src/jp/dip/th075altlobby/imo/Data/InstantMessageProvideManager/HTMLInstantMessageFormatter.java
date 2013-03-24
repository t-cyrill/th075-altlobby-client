package jp.dip.th075altlobby.imo.Data.InstantMessageProvideManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import jp.dip.th075altlobby.imo.Data.communication.InstantMessage;

public class HTMLInstantMessageFormatter implements InstantMessageFormatter {
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss z");
	private final String header1, header2, footer;
	private String title = "";

	public HTMLInstantMessageFormatter(String header1, String header2, String footer) {
		this.header1 = header1;
		this.header2 = header2;
		this.footer = footer;
	}


	private synchronized String getTitle() {
		return title;
	}


	public synchronized void setTitle(String title) {
		this.title = title;
	}


	/**
	 * <h1>format</h1>
	 * <h2>単一のインスタントメッセージをHTML形式にフォーマットし、文字列として返す</h2>
	 * <p>単一のIMをHTML形式にフォーマットします。</p>
	 * <p>このクラスのformatメソッドは&lt;li&gt;を用いたリストにてIMを表現します。</p>
	 * <p>formatメソッドは必要ならばIMに含まれる特定の文字列を
	 * 一般にHTMLエスケープと呼ばれる方法でエスケープします。
	 * すべての&amp;は&amp;amp;で置き換えられ、&lt;は&amp;lt;に置き換えられます。その他の特別な文字も同様に置き換えられます。</p>
	 */
	@Override
	public String format(InstantMessage IM) {
		Date date = new Date(IM.getTime());
		String from = IM.getFrom();
		String to = IM.getTo();
		int nFlag = 0;
		if(to.equals("all"))
			nFlag = 1;
		else if(from.equals("System") && IM.getTo().equals("*"))
			nFlag = 3;
		// case breakしていないのは意図的である
		switch (nFlag) {
		case 0:
			to = to + "...";
		case 1:
			from = from + "...";
		default:
			break;
		}
		return "<div class=\"from_to\"><span class=\"from\">" + HTMLEscapeProvider.escape(from, false) + "</span> -&gt; " +
				"<span class=\"to\">" +HTMLEscapeProvider.escape(to, false) + "</span>　　" +
				"<span class=\"date\">" + sdf.format(date) + "</span></div>" +
				"<div class=\"im\"><span class=\"c-"+IM.getColor()+"\">" + HTMLEscapeProvider.escape(IM.getMessage(), false) + "</span></div>";
	}

	/**
	 * <h1>format</h1>
	 * <h2>複数のインスタントメッセージをHTML形式にフォーマットし、文字列として返す</h2>
	 * <p>IM配列を{@link HTMLInstantMessageFormatter#format(InstantMessage)}の方法でHTML形式にフォーマットします。</p>
	 * <p>このメソッドはコンストラクタで指定されたheaderおよびfooterとsetTitleで指定されたタイトルを使って完全なHTMLを返します。</p>
	 * <h3>スレッドセーフ</h3>
	 * <p>このメソッドは複数の並行スレッドから安全に呼び出すことはできません。</p>
	 */
	@Override
	public String format(InstantMessage[] IMArray) {
		StringBuilder sbuf = new StringBuilder(1024);
		for(InstantMessage im : IMArray){
			String escaped = format(im);
			sbuf.append("<div>" + escaped + "</div>");
		}
		return header1 + getTitle() + header2 + sbuf.toString() + footer;
	}


}
