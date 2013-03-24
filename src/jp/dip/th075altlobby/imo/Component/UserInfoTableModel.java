package jp.dip.th075altlobby.imo.Component;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import jp.dip.th075altlobby.imo.Data.IPConverter.IPConverter;
import jp.dip.th075altlobby.imo.Data.communication.UserInfo;

public class UserInfoTableModel extends DefaultTableModel {
    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    private final List<UserInfo> _innerList = new ArrayList<UserInfo>();

    public static final String[] columnHeadersString = { "名前", "状態", "IP",
            "uid", "IM", "ショートメッセージ" };

    private static final ColumnContext[] columnArray = {
            new ColumnContext(columnHeadersString[0], String.class, false),
            new ColumnContext(columnHeadersString[1], String.class, false),
            new ColumnContext(columnHeadersString[2], String.class, false),
            new ColumnContext(columnHeadersString[3], String.class, false),
            new ColumnContext(columnHeadersString[4], String.class, false),
            new ColumnContext(columnHeadersString[5], String.class, false) };

    public UserInfoTableModel() {
        super();
    }

    /**
     * <h1>add</h1> <h2>新しいユーザーの情報を追加する</h2>
     * <p>
     * このモデルに新しいユーザーの情報を追加します。
     * </p>
     * <p>
     * ただし、IP非表示を設定しているユーザーのIPアドレスは"*.*.*.*"で置き換えられます。
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはテーブルモデルを書き換えます。Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     * 
     * @param p
     *            追加するUserinfo
     */
    public void add(UserInfo p) {
        String IPString = IPConverter.toString(p.getIpAddress());
        if (p.getIPHideFlag() == 1)
            IPString = "*.*.*.*";
        String[] newUserInfo = { p.getPublicName(),
                UserInfo.getStateString(p.getState()), IPString, p.getUID(),
                UserInfo.getIMPolicyString(p.getIMPolicy()),
                p.getPublicShortMessage() };

        synchronized (_innerList) {
            _innerList.add(p);
            super.addRow(newUserInfo);
        }
    }

    /**
     * row行目のUserInfoを取得します. このメソッドはrowが範囲外の場合にnullを返します。
     * 
     * @param row
     *            取得する行番号
     * @return row番目のUserInfo
     */
    public UserInfo getRow(int row) {
        UserInfo info;
        synchronized (_innerList) {
            if (row < 0 || row >= _innerList.size())
                return null;

            info = _innerList.get(row);
        }
        return info;
    }

    /**
     * <h1>remove</h1> <h2>モデルに含まれるUserInfoを取り除く</h2>
     * <p>
     * pに一致するユーザー情報をこのモデルから取り除きます
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはテーブルモデルを書き換えます。Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     * 
     * @param p
     *            取り除く要素
     * @return 取り除く要素がない場合falseを返す
     */
    public boolean remove(UserInfo p) {
        synchronized (_innerList) {
            int index = _innerList.indexOf(p);
            if (index == -1)
                return false;
            _innerList.remove(index);
            super.removeRow(index);
        }
        return true;
    }

    /**
     * <h1>removeAll</h1> <h2>モデルに含まれるすべてのUserInfoを取り除く</h2>
     * <p>
     * このモデルに含まれるすべてのUserInfoを取り除きます
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはテーブルモデルを書き換えます。Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     */
    public void removeAll() {
        synchronized (_innerList) {
            // int c = getRowCount();
            int c = _innerList.size();
            for (int i = 0; i < c; i++) {
                super.removeRow(0);
                _innerList.remove(0);
            }
        }
    }

    /**
     * <h1>update</h1> <h2>UIDが一致するユーザーの状態を書き換える</h2>
     * <p>
     * ユーザーの状態を書き換えます
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはテーブルモデルを書き換えます。Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     * 
     * @param UID
     *            書き換えるユーザーのUID
     * @param state
     *            新しい状態
     */
    public void update(String UID, byte state) {
        synchronized (_innerList) {
            for (int i = 0; i < _innerList.size(); i++) {
                UserInfo current = _innerList.get(i);
                if (current.getUID().equals(UID)) {
                    current.setState(state);
                    setValueAt(UserInfo.getStateString(state), i, 1);
                    break;
                }
            }
        }
    }

    /**
     * <h1>setUserInfos</h1> <h2>複数のユーザー情報を設定する</h2>
     * <p>
     * このテーブルモデルを新たに設定します。このメソッド呼び出し前のすべての操作はこのメソッドにより破棄されます。
     * </p>
     * <p>
     * このメソッドはUserInfoの配列をアトミックに設定します。並行処理される複数のスレッドから安全に呼び出すことができますが、
     * Swingの制約からEDT以外から呼び出すことは推奨されません。
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはテーブルモデルを書き換えますが、内部でSwingUtilities.invokeAndWaitを呼び出すため、
     * Swingのイベントディスパッチスレッドから呼び出すことはできません。
     * </p>
     * 
     * @param infos
     *            設定するユーザー情報が格納された配列
     */
    public void setUserInfos(final UserInfo[] infos) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // すべての要素を取り除く
                // removeAllが終わるまで次の処理は行われません。
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (_innerList) {
                            removeAll();

                            // 追加する
                            for (UserInfo info : infos)
                                add(info);
                        }
                    }
                });
            }
        }).start();
    }

    public boolean isCellEditable(int row, int col) {
        return columnArray[col].isEditable;
    }

    public Class<?> getColumnClass(int modelIndex) {
        return columnArray[modelIndex].columnClass;
    }

    public int getColumnCount() {
        return columnArray.length;
    }

    public String getColumnName(int modelIndex) {
        return columnArray[modelIndex].columnName;
    }

    /**
     * <h1>ColumnContext</h1>
     * <p>
     * http://terai.xrea.jp/Swing/LineFocusTable.htmlを参考にしました。
     * </p>
     */
    private static class ColumnContext {
        public final String columnName;
        public final Class<String> columnClass;
        public final boolean isEditable;

        public ColumnContext(String columnName, Class<String> columnClass,
                boolean isEditable) {
            this.columnName = columnName;
            this.columnClass = columnClass;
            this.isEditable = isEditable;
        }
    }

    /**
     * <h1>getUserInfoByUID</h1> <h2>ユーザー情報をUIDから取得する</h2>
     * <p>
     * 内部Listを走査しUIDからUserInfoを取得します。
     * </p>
     * <p>
     * 一致するUIDが存在しない場合やUIDがnullの場合、このメソッドはnullを返します。
     * </p>
     * 
     * @param UID
     *            検索対象のUID
     * @return ユーザー情報
     */
    public UserInfo getUserInfoByUID(String UID) {
        if (UID == null)
            return null;

        UserInfo ret_info = null;
        synchronized (_innerList) {
            for (UserInfo info : _innerList) {
                if (info.getUID().equals(UID)) {
                    ret_info = info;
                    break;
                }
            }
        }
        return ret_info;
    }
}
