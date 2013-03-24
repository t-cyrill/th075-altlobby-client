package jp.dip.th075altlobby.imo.Window.ConnectionWindow;

import java.awt.Dimension;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import jp.dip.th075altlobby.imo.Resource.Resource;
import jp.dip.th075altlobby.imo.Window.EventAdapter.RunnableCallActionListener;

/**
 * <h1>ConnectionWindowInnerPanel1</h1> <h2>ConnectionWindowで使われるパネル</h2>
 * <p>
 * ConnectionWindowで使われるパネルです。
 * </p>
 * <p>
 * 接続先のJLabel、接続先を選択するJComboBox、サーバーへ接続するJButton、設定ウィンドウを開くJButtonを持ちます。
 * </p>
 * 
 * @author Cyrill
 */
public class ConnectionWindowInnerPanel1 extends JPanel {
    /**
     * 生成シリアルバージョン(やり取りしない)
     */
    private static final long serialVersionUID = 1L;

    private String[] server_list;
    private final JComboBox server_list_box;
    private final JButton connectionButton;
    private final JButton settingButton;

    private final ResourceBundle resource = Resource.getBundle();

    /**
     * <h1>ConnectionWindowInnerPanel1</h1> <h2>このクラスのインスタンスを生成する</h2>
     * <p>
     * インスタンスを生成します。パネルにコンポーネントを配置します。
     * </p>
     * <p>
     * イベントリスナの登録はされません。インスタンスのregisterLisnerを呼び出します。
     * </p>
     * 
     * @param server_list
     *            コンボボックスに設定する文字列
     */
    public ConnectionWindowInnerPanel1(String[] server_list) {
        setServerList(server_list);

        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        JLabel label = new JLabel(
                resource.getString("connection.label.connect"));
        server_list_box = new JComboBox(server_list);
        server_list_box.setPreferredSize(new Dimension(180, 20));
        server_list_box.setMaximumSize(new Dimension(180, 20));
        server_list_box.setMinimumSize(new Dimension(180, 20));

        connectionButton = new JButton(
                resource.getString("connection.button.connect"));
        settingButton = new JButton(
                resource.getString("connection.button.config"));

        Dimension d = new Dimension(64, 24);
        connectionButton.setPreferredSize(d);
        settingButton.setPreferredSize(d);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 0, 1, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        layout.setConstraints(label, gbc);

        gbc.insets = new Insets(0, 2, 0, 2);
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        layout.setConstraints(server_list_box, gbc);

        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        layout.setConstraints(connectionButton, gbc);

        gbc.gridx = 2;
        layout.setConstraints(settingButton, gbc);

        add(label);
        add(server_list_box);
        add(connectionButton);
        add(settingButton);
    }

    /**
     * <h1>WheelListener</h1> <h2>内部のコンボボックスで利用されるマウスのホイールイベントリスナ</h2>
     * <p>
     * 内部のコンボボックスで利用されるマウスのホイールイベントリスナです。
     * </p>
     * <p>
     * 登録されたコンポーネントの上でマウスのホイールがされたときにそのホイール分コンボボックスをスライドホイールさせます。
     * </p>
     * 
     * @author Cyrill
     */
    class WheelListener implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            int move = e.getWheelRotation();
            int new_index = server_list_box.getSelectedIndex() + move;
            if (new_index < 0)
                new_index = 0;
            else if (new_index >= server_list_box.getItemCount())
                new_index = server_list_box.getItemCount() - 1;

            server_list_box.setSelectedIndex(new_index);
        }
    }

    /**
     * <h1>registerLisners</h1> <h2>内部のSwingコンポーネントにリスナーを登録する</h2>
     * <p>
     * 内部のSwingコンポーネントにリスナーを登録します。コンストラクタから呼び出すことはできません。
     * </p>
     * <p>
     * 指定されるRunnableは別スレッドで実行されます。Swingのイベントディスパッチスレッドからは呼ばれません。
     * </p>
     * 
     */
    public void registerLisners(Runnable connectionButtonRunnable,
            Runnable settingButtonRunnable) {
        connectionButton.addActionListener(new RunnableCallActionListener(
                connectionButtonRunnable));
        settingButton.addActionListener(new RunnableCallActionListener(
                settingButtonRunnable));
        server_list_box.addMouseWheelListener(new WheelListener());
    }

    /**
     * <h1>getSelectedString</h1> <h2>コンボボックスで選択された文字列を取得する</h2>
     * <p>
     * 内部のコンボボックスで選択された文字列を取得します。
     * </p>
     * <p>
     * このメソッドはJComboBox.
     * getSelectedIndexを呼び出すためSwingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     * 
     * @return 選択された文字列
     */
    public String getSelectedString() {
        int selected = server_list_box.getSelectedIndex();
        return server_list[selected];
    }

    /**
     * <h1>setServerList</h1> <h2>コンボボックスに表示される文字列を設定する</h2>
     * <p>
     * 内部のJComboBoxで使用される文字列を設定します。
     * </p>
     * 
     * @param list
     *            コンボボックスに表示される文字列
     */
    private void setServerList(String[] list) {
        server_list = new String[list.length];
        System.arraycopy(list, 0, server_list, 0, list.length);
    }
}