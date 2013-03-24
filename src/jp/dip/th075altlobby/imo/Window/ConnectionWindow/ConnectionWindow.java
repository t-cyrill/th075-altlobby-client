package jp.dip.th075altlobby.imo.Window.ConnectionWindow;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jp.dip.th075altlobby.imo.Constants.ClientConstants;
import jp.dip.th075altlobby.imo.System.Icon.WindowIcon;

public class ConnectionWindow extends JFrame {
    /**
     * 生成シリアルバージョン(やり取りしない)
     */
    private static final long serialVersionUID = 1L;

    private final ConnectionWindowInnerPanel1 panel;

    /**
     * <h1>ConnectionWindow</h1> <h2>接続ウィンドウのフレームを生成する</h2>
     * <p>
     * ボタンが押されたときに走るRunnableを指定して、インスタンスを生成します。
     * </p>
     * <p>
     * 指定されるRunnableは別スレッドで実行されます。Swingのイベントディスパッチスレッドからは呼ばれません。
     * </p>
     * 
     */
    public ConnectionWindow(String[] server_list,
            Runnable connectionButtonRunnable, Runnable settingButtonRunnable) {
        panel = new ConnectionWindowInnerPanel1(server_list);
        panel.registerLisners(connectionButtonRunnable, settingButtonRunnable);
        getContentPane().add(panel);

        setTitle(ClientConstants.ConnectionWindowCaption);
        getContentPane().add(panel);
        setResizable(false);
        setSize(240, 120);
        setLocationRelativeTo(null);

        WindowIcon.getInstance().setIcon(this);
    }

    public void setWindowVisible(final boolean b) {
        final ConnectionWindow own = this;
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                own.setVisible(b);
            }
        });
    }

    /**
     * <h1>getSelectedString</h1> <h2>コンボボックスで選択された文字列を取得する</h2>
     * <p>
     * 内部で保持しているパネルのコンボボックスで選択された文字列を取得します。
     * </p>
     * <p>
     * このメソッドが呼び出すConnectionWindowInnerPanel1.getSelectedIndexは 内部でJComboBox.
     * getSelectedIndexを呼び出すためSwingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     * 
     * @return 選択された文字列
     */
    public String getSelectedString() {
        return panel.getSelectedString();
    }
}
