package jp.dip.th075altlobby.imo.Window.MainWindow;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import jp.dip.th075altlobby.imo.Component.UserInfoTableModel;
import jp.dip.th075altlobby.imo.Data.DataSpliterImploder.DataSpliterImploder;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ClientSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.TableSetting;
import jp.dip.th075altlobby.imo.Data.communication.UserInfo;
import jp.dip.th075altlobby.imo.Resource.Resource;
import jp.dip.th075altlobby.imo.Window.EventAdapter.RunnableCallActionListener;

/**
 * <h1>UserInfoTablePanel</h1> <h2>ユーザー一覧のテーブルが配置されるパネル</h2>
 * <p>
 * このパネルは内部に配置されたテーブルの限定的な操作を提供します。
 * </p>
 * 
 * @author Cyrill
 */
class UserInfoTablePanel extends JPanel {
    /**
     * 生成シリアルバージョン(やり取りしない)
     */
    private static final long serialVersionUID = 1L;

    private final ResourceBundle resource = Resource.getBundle();
    private final JTable innerTable;
    private final UserInfoTableModel innerModel;
    private final JMenuItem setIMSendUser = new JMenuItem(
            resource.getString("main.usertable.menu.sendTo")),
            fightSelectedPlayer = new JMenuItem(
                    resource.getString("main.usertable.menu.fight")),
            addNGUser = new JMenuItem(
                    resource.getString("main.usertable.menu.setNG")),
            removeNGUser = new JMenuItem(
                    resource.getString("main.usertable.menu.removeNG"));
    private final JPopupMenu menu;
    private final HashSet<String> ngUserSet = new HashSet<String>();

    // private final static Logger logger =
    // Logger.getLogger("jp.dip.th075altlobby.imo.Application");

    /**
     * インスタンスを生成する
     * 
     * @param setting
     */
    public UserInfoTablePanel(ClientSetting setting) {
        menu = new JPopupMenu();
        menu.add(setIMSendUser);
        menu.add(fightSelectedPlayer);
        menu.addSeparator();
        menu.add(addNGUser);
        menu.add(removeNGUser);

        innerModel = new UserInfoTableModel();
        innerTable = new JTable(innerModel);
        innerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        innerTable.setShowGrid(false);
        innerTable.setIntercellSpacing(new Dimension(0, 0));
        innerTable.setFillsViewportHeight(true);
        innerTable.setAutoCreateRowSorter(true);
        innerTable.setPreferredSize(null);
        innerTable.setDefaultEditor(Object.class, null);
        innerTable.setComponentPopupMenu(menu);
        innerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // NGユーザー一覧を読み込む
        String NGUserListTemp = setting.getNGUserlist();
        setNGUserList(NGUserListTemp);

        // テーブルヘッダーにイベントリスナを追加する
        JTableHeader tableHeader = innerTable.getTableHeader();
        tableHeader.setReorderingAllowed(true);

        // テーブルのカラムを設定する
        TableSetting tableSetting = setting.getTableSetting();
        int[] order = DataSpliterImploder.split(
                tableSetting.getTable_headers_order(), ",");

        TableColumnModel columnModel = innerTable.getColumnModel();
        columnModel
                .getColumn(
                        columnModel
                                .getColumnIndex(UserInfoTableModel.columnHeadersString[0]))
                .setPreferredWidth(tableSetting.getTable_name_width());
        columnModel
                .getColumn(
                        columnModel
                                .getColumnIndex(UserInfoTableModel.columnHeadersString[1]))
                .setPreferredWidth(tableSetting.getTable_ip_width());
        columnModel
                .getColumn(
                        columnModel
                                .getColumnIndex(UserInfoTableModel.columnHeadersString[2]))
                .setPreferredWidth(tableSetting.getTable_uid_width());
        columnModel
                .getColumn(
                        columnModel
                                .getColumnIndex(UserInfoTableModel.columnHeadersString[3]))
                .setPreferredWidth(tableSetting.getTable_state_width());
        columnModel
                .getColumn(
                        columnModel
                                .getColumnIndex(UserInfoTableModel.columnHeadersString[4]))
                .setPreferredWidth(tableSetting.getTable_im_width());
        columnModel
                .getColumn(
                        columnModel
                                .getColumnIndex(UserInfoTableModel.columnHeadersString[5]))
                .setPreferredWidth(tableSetting.getTable_short_msg_width());

        // 順番を並び替える
        setHeadersOrder(order[0], order[1], order[2], order[3], order[4],
                order[5]);
        resetRender(ngUserSet);

        JScrollPane scrollPane = new JScrollPane(innerTable);
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0d;
        gbc.weighty = 1.0d;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        layout.setConstraints(scrollPane, gbc);
        add(scrollPane);
    }

    private void setNGUserList(String ngUserListString) {
        if (ngUserListString == null)
            return;
        String[] splited = ngUserListString.split("&");
        synchronized (ngUserSet) {
            for (String uid : splited)
                ngUserSet.add(uid);
        }
    }

    /**
     * <h1>getNGUserListString</h1> <h2>NGユーザーのUIDセットを"&"区切りで返す</h2>
     * <p>
     * NGユーザーのUIDが格納されたセットを結合したStringを返します。
     * </p>
     * <p>
     * 区切り文字は"&"です。
     * </p>
     * <p>
     * 内部セットの要素がない場合、空の文字列""を返します。
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このクラスは内部で保持しているセットの同期化を行います。このメソッドは並行スレッドから安全に呼び出すことができます。
     * </p>
     * 
     * @return &で結合されたUID文字列
     */
    public String getNGUserListString() {
        if (ngUserSet.size() == 0)
            return "";

        StringBuffer sb = new StringBuffer();
        for (String uid : ngUserSet) {
            if (!uid.equals(""))
                sb.append(uid).append("&");
        }

        if (sb.length() == 0)
            return "";
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 
     * <h2>UIDのユーザーがNGユーザーかどうかを調べる</h2>
     * 
     * @return
     */
    public boolean isNGUser(String UID) {
        return ngUserSet.contains(UID);
    }

    /**
     * <h1>regiserLisners</h1> <h2>イベントリスナーを登録する</h2>
     * <p>
     * イベントリスナーを登録します。
     * </p>
     * <p>
     * 引数で渡されたRunnableはイベントリスナーから呼ばれますが、別スレッドで実行されます。
     * </p>
     * 
     * @param setIMSendUserRunnable
     *            IMの送信先に設定が押されたときに走るRunnable
     * @param fightSelectedPlayerRunnable
     *            対戦するが押されたときに走るRunnable
     * @param resetCallbackRunnable
     *            テーブルがクリックされたかテーブル内でキー移動があったときに走るRunnable
     */
    public void regiserListeners(Runnable setIMSendUserRunnable,
            Runnable fightSelectedPlayerRunnable,
            final Runnable resetCallbackRunnable) {
        setIMSendUser.addActionListener(new RunnableCallActionListener(
                setIMSendUserRunnable));
        fightSelectedPlayer.addActionListener(new RunnableCallActionListener(
                fightSelectedPlayerRunnable));

        innerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1)
                    callRunnable(resetCallbackRunnable);
            }
        });
        innerTable.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                callRunnable(resetCallbackRunnable);
            }
        });

        addNGUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserInfo selected = getSelectedRow();
                if (selected == null)
                    return;

                String selectedUID = selected.getUID();
                if (!ngUserSet.contains(selectedUID)) {
                    ngUserSet.add(selectedUID);
                    resetRender(ngUserSet);
                }
            }
        });

        removeNGUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserInfo selected = getSelectedRow();
                if (selected == null)
                    return;

                String selectedUID = selected.getUID();
                if (ngUserSet.contains(selectedUID)) {
                    ngUserSet.remove(selectedUID);
                    resetRender(ngUserSet);
                }
            }
        });
    }

    private void callRunnable(Runnable r) {
        ExecutorService exs = Executors.newCachedThreadPool();
        exs.execute(r);
        exs.shutdown();
    }

    /**
     * <h1>addUserInfo</h1> <h2>新しいユーザーの情報を追加する</h2>
     * <p>
     * 内部で保持しているテーブルモデルに新しいユーザーの情報を追加します。
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
    public void addUserInfo(UserInfo p) {
        innerModel.add(p);
    }

    /**
     * <h1>removeUserInfo</h1> <h2>単一のユーザー情報を取り除く</h2>
     * <p>
     * list.indexOf(p)が返す要素のデータをテーブルモデル及び内部リストから取り除きます。
     * </p>
     * <p>
     * このメソッドはテーブルモデルを書き換えます。Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     * 
     * @param p
     *            取り除くUserinfo
     */
    public void removeUserInfo(UserInfo p) {
        innerModel.remove(p);
    }

    /**
     * <h1>setUserInfos</h1> <h2>複数のユーザー情報を設定する</h2>
     * <p>
     * このテーブルモデルを新たに設定します。このメソッド呼び出し前のすべての操作はこのメソッドにより破棄されます。
     * このメソッドは一般に初期状態設定時に呼び出すべきです。
     * </p>
     * <p>
     * このメソッドはUserInfoの配列をアトミックに設定します。並行処理される複数のスレッドから安全に呼び出すことができますが、
     * Swingの制約からEDT以外から呼び出すことは推奨されません。
     * </p>
     * 
     * @param infos
     *            設定するユーザー情報が格納された配列
     * @see UserInfoTableModel#setUserInfos(UserInfo[])
     * 
     */
    public void setUserInfos(UserInfo[] infos) {
        innerModel.setUserInfos(infos);
    }

    public void updateUserInfo(String UID, byte state) {
        innerModel.update(UID, state);
    }

    /**
     * 対戦するの項目の有効無効を切り替える
     * <p>
     * 対戦するの項目の有効無効を切り替えます。
     * </p>
     * <p>
     * このメソッドは SwingUtilities.invokeLater(Runnable) を読み出します。
     * Swingのイベントディスパッチスレッド以外からでも呼び出すことができます。
     * </p>
     * *
     * 
     * @param b
     *            trueの場合有効になる
     */
    public void setFightSelectedPlayerEnabled(final boolean b) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fightSelectedPlayer.setEnabled(b);
            }
        });
    }

    /**
     * ヘッダーのオーダーを設定します。 それぞれの位置には、0～5の整数値を設定してください。
     * いずれかの値が同じの場合、または0～5の範囲外の値を指定した場合、デフォルトのオーダーになります。
     * 
     * @param nameColumnOrder
     *            名前カラムの位置
     * @param stateColumnOrder
     *            状態カラムの位置
     * @param ipColumnOrder
     *            IPカラムの位置
     * @param userIDColumnOrder
     *            uidカラムの位置
     * @param imColumnOrder
     *            IMカラムの位置
     * @param shortMessageColumnOrder
     *            ショートメッセージカラムの位置
     */
    public void setHeadersOrder(int nameColumnOrder, int stateColumnOrder,
            int ipColumnOrder, int userIDColumnOrder, int imColumnOrder,
            int shortMessageColumnOrder) {
        TableColumnModel columnModel = innerTable.getTableHeader()
                .getColumnModel();

        List<Integer> list = new ArrayList<Integer>();
        List<Integer> checker = new ArrayList<Integer>();

        for (int i = 0; i < 6; i++)
            checker.add(i);

        // オーダーを調べる
        list.add(nameColumnOrder);
        list.add(stateColumnOrder);
        list.add(ipColumnOrder);
        list.add(userIDColumnOrder);
        list.add(imColumnOrder);
        list.add(shortMessageColumnOrder);

        // 0～5を含んでいない場合
        if (!list.containsAll(checker)) {
            nameColumnOrder = 0;
            stateColumnOrder = 1;
            ipColumnOrder = 2;
            userIDColumnOrder = 3;
            imColumnOrder = 4;
            shortMessageColumnOrder = 5;
        }

        TableColumn[] columnBacket = new TableColumn[6];
        columnBacket[nameColumnOrder] = columnModel.getColumn(columnModel
                .getColumnIndex(UserInfoTableModel.columnHeadersString[0]));
        columnBacket[stateColumnOrder] = columnModel.getColumn(columnModel
                .getColumnIndex(UserInfoTableModel.columnHeadersString[1]));
        columnBacket[ipColumnOrder] = columnModel.getColumn(columnModel
                .getColumnIndex(UserInfoTableModel.columnHeadersString[2]));
        columnBacket[userIDColumnOrder] = columnModel.getColumn(columnModel
                .getColumnIndex(UserInfoTableModel.columnHeadersString[3]));
        columnBacket[imColumnOrder] = columnModel.getColumn(columnModel
                .getColumnIndex(UserInfoTableModel.columnHeadersString[4]));
        columnBacket[shortMessageColumnOrder] = columnModel
                .getColumn(columnModel
                        .getColumnIndex(UserInfoTableModel.columnHeadersString[5]));

        for (int i = 0; i < 6; i++)
            columnModel.addColumn(columnBacket[i]);

        for (int i = 0; i < 6; i++)
            columnModel.removeColumn(columnModel.getColumn(0));
    }

    /**
     * Renderを再設定します。 このメソッドは、resetRender(Set<String> NGList, int
     * userIDsColumnNumber, int stateColumnNumber)の簡易版です。
     * userIDsColumnNumberとstateColumnNumberは
     * {@link UserInfoTableModel#columnHeadersString}の[1]と[3]から検索されます。
     * 
     * @param NGList
     *            NGユーザーのリスト
     */
    private void resetRender(Set<String> NGList) {
        TableColumnModel columnModel = innerTable.getTableHeader()
                .getColumnModel();

        if (NGList != null)
            UserInfoRenderer.setNGUserID(NGList);

        for (int i = 0; i < columnModel.getColumnCount(); i++) {
            UserInfoRenderer currentRender = new UserInfoRenderer(
                    (DefaultTableCellRenderer) innerTable
                            .getDefaultRenderer(innerTable.getColumnClass(i)));
            columnModel.getColumn(i).setCellRenderer(currentRender);
        }
    }

    /**
     * <h1>getSelectedRow</h1> <h2>ビューで現在選択されている行のUserInfoを返す</h2>
     * <p>
     * このパネル内部のテーブルで選択されているUserInfoを返します。
     * 選択されていない場合やデータの不整合がある場合(必ずEDTから呼び出してください)、 このメソッドはnullを返すことがあります。
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドは内部で{@link JTable#getSelectedRow(int)},
     * {@link JTable#convertRowIndexToModel(int)},
     * {@link UserInfoTableModel#getRow(int)}
     * を連続して呼び出します。Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     * 
     * @return 選択されたユーザーの情報
     */
    public UserInfo getSelectedRow() {
        int selected = innerTable.getSelectedRow();
        if (selected != -1) {
            // テーブルの項目が選択されている
            // モデルの表示と実際のデータの変換
            selected = innerTable.convertRowIndexToModel(selected);
            return innerModel.getRow(selected);
        } else
            return null;
    }

    public String getTableHeadersOrder() {
        TableColumnModel columnModel = innerTable.getColumnModel();

        // カラムの順番調べる
        int[] order = new int[6];
        for (int i = 0; i < order.length; i++)
            order[i] = columnModel
                    .getColumnIndex(UserInfoTableModel.columnHeadersString[i]);

        return DataSpliterImploder.implode(order, ",");
    }

    /**
     * テーブルヘッダーの幅を取得する 入れ替わっている場合でも、もともとの位置にあったヘッダーの幅を取得します。
     * 
     * @param i
     * @return
     */
    public int getTableHeaderWidth(int i) {
        TableColumnModel columnModel = innerTable.getColumnModel();
        return columnModel
                .getColumn(
                        columnModel
                                .getColumnIndex(UserInfoTableModel.columnHeadersString[i]))
                .getWidth();
    }

    /**
     * <h1>getUserInfoByUID</h1> <h2>ユーザー情報をUIDから取得する</h2>
     * <p>
     * {@link UserInfoTableModel#getUserInfoByUID(String)}
     * を呼び出し、UIDからUserInfoを取得します。
     * </p>
     * <p>
     * 一致するUIDが存在しない場合やfromUIDがnullの場合、このメソッドはnullを返します。
     * </p>
     * 
     * @param fromUID
     * @return UIDに一致するユーザーの情報
     */
    public UserInfo getUserInfoByUID(String fromUID) {
        return innerModel.getUserInfoByUID(fromUID);
    }
}

class UserInfoRenderer extends DefaultTableCellRenderer {
    /**
	 *
	 */
    private static final long serialVersionUID = 1L;
    private static final DotBorder dotBorder = new DotBorder(2, 2, 2, 2);
    private static final Border emptyBorder = BorderFactory.createEmptyBorder(
            2, 2, 2, 2);
    private final DefaultTableCellRenderer delegate;

    private static final List<String> NGUsers = new LinkedList<String>();

    private final static int COLUMN_USER_ID = 1;
    private final static int COLUMN_STATE = 2;

    /**
     * <h1>setNGUserID</h1> <h2>NGユーザーIDのリストを設定する</h2>
     * <p>
     * このメソッドは内部のリストからすべての要素を取り除いた後、NGidsのすべての要素を追加します。
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドを呼び出す間、NGidsに構造的な変更を加えないでください。
     * </p>
     * 
     * @param NGList
     *            追加するユーザーID
     */
    public static void setNGUserID(Set<String> NGList) {
        synchronized (NGUsers) {
            NGUsers.clear();
            Iterator<String> it = NGList.iterator();
            while (it.hasNext())
                NGUsers.add(it.next());
        }
    }

    public UserInfoRenderer(DefaultTableCellRenderer delegate) {
        this.delegate = delegate;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = delegate.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);
        if (c instanceof JComponent) {
            int lsi = table.getSelectionModel().getLeadSelectionIndex();
            ((JComponent) c).setBorder(row == lsi ? dotBorder : emptyBorder);
            dotBorder.setLastCellFlag(row == lsi
                    && column == table.getColumnCount() - 1);
        }

        Color foreCol = c.getForeground();

        if (isSelected) {
            c.setForeground(foreCol);
            return c;
        }

        TableColumnModel columnModel = table.getTableHeader().getColumnModel();

        int matchColumn = 0;
        int stateColumnNumber = columnModel
                .getColumnIndex(UserInfoTableModel.columnHeadersString[1]);
        int userIDsColumnNumber = columnModel
                .getColumnIndex(UserInfoTableModel.columnHeadersString[3]);

        if (column == userIDsColumnNumber)
            matchColumn = COLUMN_USER_ID;
        if (column == stateColumnNumber)
            matchColumn = COLUMN_STATE;

        switch (matchColumn) {
        case COLUMN_USER_ID:
            Color color = NGUsers.contains(value) ? Color.red : Color.black;
            c.setForeground(color);
            break;
        case COLUMN_STATE:
            setStateComponent(c, (String) value);
            break;
        default:
            c.setForeground(Color.black);
        }
        return c;
    }

    private void setStateComponent(Component c, String state) {
        Color color = null;
        int userState = UserInfo.getState(state);
        switch (userState) {
        case UserInfo.WAITING:
            color = Color.black;
            break;
        case UserInfo.GATHERING:
            color = Color.red;
            setBoldFont(c);
            break;
        case UserInfo.FIGHTING:
            color = new Color(0, 0, 153);
            setBoldFont(c);
            break;
        case UserInfo.WATCHING:
            color = new Color(0, 128, 0);
            setBoldFont(c);
            break;
        case UserInfo.LEAVING:
            color = new Color(0, 128, 128);
            break;
        case UserInfo.SETTING:
            color = new Color(0x8b, 0, 0x8b);
            break;
        case UserInfo.HOST_GATHERING:
            color = new Color(0xee, 0, 0xee);
            setBoldFont(c);
            break;
        default:
            color = c.getForeground();
        }
        c.setForeground(color);
    }

    private void setBoldFont(Component c) {
        Font defFont = c.getFont();
        c.setFont(new Font(defFont.getFontName(), Font.BOLD, defFont.getSize()));
    }
}

class DotBorder extends EmptyBorder {
    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    private static final BasicStroke dashed = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f,
            (new float[] { 1.0f }), 0.0f);
    private static final Color dotColor = new Color(200, 150, 150);

    public DotBorder(int top, int left, int bottom, int right) {
        super(top, left, bottom, right);
    }

    private boolean isLastCell = false;

    public void setLastCellFlag(boolean flag) {
        isLastCell = flag;
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(x, y);
        g2.setPaint(dotColor);
        g2.setStroke(dashed);
        int cbx = c.getBounds().x;
        if (cbx == 0) {
            g2.drawLine(0, 0, 0, h);
        }
        if (isLastCell) {
            g2.drawLine(w - 1, 0, w - 1, h);
        }
        if (cbx % 2 == 0) {
            g2.drawLine(0, 0, w, 0);
            g2.drawLine(0, h - 1, w, h - 1);
        } else {
            g2.drawLine(1, 0, w, 0);
            g2.drawLine(1, h - 1, w, h - 1);
        }
        g2.translate(-x, -y);
    }
}
