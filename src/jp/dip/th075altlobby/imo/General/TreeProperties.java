package jp.dip.th075altlobby.imo.General;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

/**
 * <h1>TreeProperties</h1>
 * <h2>キーソートされたProperties</h2>
 * <p>keySetをオーバーライドすることによりTreeSetの使用を強制し自然順序付けによりキーがソートされるPropertiesです。</p>
 * <p>このクラスの実装は将来のJavaの仕様変更により無効となる場合があります。</p>
 *
 */
public class TreeProperties extends Properties {
	private static final long serialVersionUID = 1L;

	@Override
	public Set<Object> keySet() {
		return Collections.unmodifiableSet(new TreeSet<Object>(super.keySet()));
	}

}
