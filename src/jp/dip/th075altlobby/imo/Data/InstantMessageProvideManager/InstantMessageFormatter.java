package jp.dip.th075altlobby.imo.Data.InstantMessageProvideManager;

import jp.dip.th075altlobby.imo.Data.communication.InstantMessage;

/**
 * <h1>InstantMessageFormater</h1>
 * <h2>インスタントメッセージ整形クラスの基底となるインターフェイス</h2>
 * <p>InstantMessageを適切なフォーマットに変換するすべての整形クラスが実装すべきインターフェイスの定義です。</p>
 * <p>単一のInstantMessageをフォーマットする{@link InstantMessageFormatter#format(InstantMessage)}および
 * 複数のIMをフォーマットし、完成した形で返す機能を提供する{@link InstantMessageFormatter#format(InstantMessage[])}を実装しなければなりません。</p>
 *
 * @author Cyrill
 *
 */
public interface InstantMessageFormatter {
	/**
	 * 単一のインスタントメッセージをフォーマットし、文字列として返します。
	 *
	 * @param IM 対象となるインスタントメッセージ
	 * @return フォーマットされたインスタントメッセージ文字列
	 */
	public String format(InstantMessage IM);
	/**
	 * 複数のインスタントメッセージをフォーマットし、文字列として返します。
	 * このメソッドが返す文字列はそれだけで完結していなければなりません。
	 *
	 * @param IMArray フォーマットされるインスタントメッセージ配列
	 * @return フォーマットされたインスタントメッセージ文字列
	 */
	public String format(InstantMessage[] IMArray);
}
