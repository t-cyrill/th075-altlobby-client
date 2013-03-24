package jp.dip.th075altlobby.imo.Window.MainWindow;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultCaret;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import jp.dip.th075altlobby.imo.Component.JWheelableComboBox;
import jp.dip.th075altlobby.imo.Data.InstantMessageProvideManager.HTMLCS;
import jp.dip.th075altlobby.imo.Data.InstantMessageProvideManager.HTMLInstantMessageFormatter;
import jp.dip.th075altlobby.imo.Data.InstantMessageProvideManager.InstantMessageProvideManager;
import jp.dip.th075altlobby.imo.Data.InstantMessageProvideManager.PlainTextInstantMessageFormatter;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.ClientSetting;
import jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting.WindowSetting;
import jp.dip.th075altlobby.imo.Data.communication.InstantMessage;
import jp.dip.th075altlobby.imo.Resource.Resource;
import jp.dip.th075altlobby.imo.Window.EventAdapter.RunnableCallActionListener;

public class InstantMessagesPanel extends JPanel {
    /**
     * 生成シリアルバージョン(やり取りしない)
     */
    private static final long serialVersionUID = 1L;
    private final JEditorPane editorPane;
    private final ResourceBundle resource = Resource.getBundle();
    private JScrollPane console_scroll;
    private final JPopupMenu menu = new JPopupMenu();
    private final JMenuItem copySelectedMenuItem = new JMenuItem(
            resource.getString("main.InstantMessage.menu.copy"));
    private final JMenuItem selectALLMenuItem = new JMenuItem(
            resource.getString("main.InstantMessage.menu.AllSel"));
    private final JMenuItem copyALLMenuItem = new JMenuItem(
            resource.getString("main.InstantMessage.menu.AllCopy"));
    private final JMenuItem storeAllIM = new JMenuItem(
            resource.getString("main.InstantMessage.menu.store"));
    private final JMenuItem storeAllIMXHTML = new JMenuItem(
            resource.getString("main.InstantMessage.menu.storeToXML"));
    private final InstantMessageProvideManager instantMessageProvideManager = new InstantMessageProvideManager();
    private InnerBottomPanel bottomPanel;

    private final static Logger logger = Logger
            .getLogger("jp.dip.th075altlobby.imo.Application");

    public InstantMessagesPanel(StyleSheet style, ClientSetting setting) {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        setPreferredSize(new Dimension(320, 380));
        setMinimumSize(new Dimension(480, 240));

        editorPane = new JEditorPane();
        console_scroll = new JScrollPane(editorPane);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0d;
        gbc.weighty = 1.0d;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        layout.setConstraints(console_scroll, gbc);

        bottomPanel = new InnerBottomPanel();
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.weighty = 0d;
        gbc.gridy = 1;
        layout.setConstraints(bottomPanel, gbc);

        add(console_scroll);
        add(bottomPanel);

        copySelectedMenuItem.setMnemonic(KeyEvent.VK_C);
        selectALLMenuItem.setMnemonic(KeyEvent.VK_A);
        copyALLMenuItem.setMnemonic(KeyEvent.VK_V);
        storeAllIM.setMnemonic(KeyEvent.VK_S);
        storeAllIMXHTML.setMnemonic(KeyEvent.VK_X);
        copyALLMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_SPACE, InputEvent.CTRL_DOWN_MASK));

        // 初期値を設定する
        WindowSetting windowSetting = setting.getWindowSetting();
        bottomPanel.setAutoScrollCheckboxState(windowSetting.getAutoScroll());
        bottomPanel.setIMFilterSelected(windowSetting.getIM_Filter());

        // メニューに追加する
        menu.add(copySelectedMenuItem);
        menu.add(selectALLMenuItem);
        menu.add(copyALLMenuItem);
        menu.addSeparator();
        menu.add(storeAllIM);
        menu.add(storeAllIMXHTML);

        HTMLEditorKit htmlek = new HTMLEditorKit();
        for (int i = 0; i < InstantMessage.color_set.length; i++)
            style.addRule(".c-" + i + " { color: "
                    + InstantMessage.color_set[i] + ";}");

        htmlek.setStyleSheet(style);
        editorPane.setComponentPopupMenu(menu);
        editorPane.setContentType("text/html");
        editorPane.setEditable(false);
        editorPane.setEditorKit(htmlek);
        editorPane.setMinimumSize(new Dimension(320, 240));

        instantMessageProvideManager.setAutoFileOutputFlag(setting
                .getImSetting().getIMAutoLogging());
    }

    /**
     * インスタントメッセージを追加する
     * 
     * @param im
     *            追加するインスタントメッセージ
     */
    public void addInstantMessage(InstantMessage im) {
        addInstantMessage(im, true);
    }

    /**
     * インスタントメッセージを追加する
     * 
     * @param im
     *            追加するインスタントメッセージ
     */
    public void addInstantMessage(InstantMessage im, boolean reset) {
        instantMessageProvideManager.append(im);
        if (reset)
            resetEditorPaneText();
    }

    /**
     * IM自動保存用出力ストリームを閉じ、フラッシュします。 このメソッドは終了時に一度だけ呼び出してください。
     */
    public void close() {
        instantMessageProvideManager.setAutoFileOutputFlag(false);
    }

    /**
     * <h1>resetEditorPaneText<h1>
     * <p>
     * EditorPaneの文字列を再設定します。
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * テキストの再設定はSwingのイベントディスパッチスレッドで行われます。
     * </p>
     * <p>
     * このメソッドはイベントディスパッチスレッド以外でも呼び出すことができます。
     * </p>
     */
    private void resetEditorPaneText() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                HTMLInstantMessageFormatter htmlFormatter = new HTMLInstantMessageFormatter(
                        HTMLCS.HTML32.HTMLHeader1, HTMLCS.HTML32.HTMLHeader2,
                        HTMLCS.HTML32.HTMLFooter);
                InstantMessage[] IMArray = instantMessageProvideManager
                        .getInnerIMtoArray();
                ArrayDeque<InstantMessage> IMqueue = new ArrayDeque<InstantMessage>();
                int filter = bottomPanel.getIMFilterSelected();
                for (InstantMessage im : IMArray) {
                    boolean onFlag = false;
                    if (filter == 0)
                        onFlag = true;
                    else if (filter == 1 && (im.getTo().equals("all")))
                        onFlag = true;
                    else if (filter == 2 && (!im.getTo().equals("all")))
                        onFlag = true;

                    if (onFlag)
                        IMqueue.add(im);
                }

                editorPane.setText(htmlFormatter.format(IMqueue
                        .toArray(new InstantMessage[0])));
            }
        });
    }

    /**
     * このIMProviderが保持しているIMをファイルに出力します
     * 
     * @param f
     *            書き出すファイル
     * @param title
     *            テキストにつけるタイトル
     */
    private void writeOutPlainText(File f, String title) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

            bw.write(title);
            bw.newLine();

            PlainTextInstantMessageFormatter formatter = new PlainTextInstantMessageFormatter();
            InstantMessage[] buf = instantMessageProvideManager
                    .getInnerIMtoArray();
            for (InstantMessage im : buf) {
                bw.write(formatter.format(im));
                bw.newLine();
            }

            bw.close();
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "保存先ファイルが見つかりません。", e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IO例外が発生しました。", e);
        }
    }

    /**
     * このIMProviderが保持しているIMをXHTML1.0に従ったフォーマットでファイルに出力します
     * 
     * @param f
     *            書き出すファイル
     * @param title
     *            テキストにつけるタイトル
     */
    private void writeOutXHTML10(File f, String title) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(f), "UTF-8"));

            bw.write(HTMLCS.XHTML10S.HTMLHeader1 + title
                    + HTMLCS.XHTML10S.HTMLHeader2);

            HTMLInstantMessageFormatter xhtmlFormatter = new HTMLInstantMessageFormatter(
                    HTMLCS.XHTML10S.HTMLHeader1, HTMLCS.XHTML10S.HTMLHeader2,
                    HTMLCS.XHTML10S.HTMLFooter);
            xhtmlFormatter.setTitle(title);
            InstantMessage[] buf = instantMessageProvideManager
                    .getInnerIMtoArray();
            for (InstantMessage im : buf) {
                String escaped = xhtmlFormatter.format(im);
                bw.write("    <div>" + escaped + "</div>");
                bw.newLine();
            }

            bw.write(HTMLCS.XHTML10S.HTMLFooter);
            bw.close();
        } catch (FileNotFoundException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }
    }

    class StoreAllIMRunnable implements Runnable {
        @Override
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // XHTMLで保存ボタンがクリックされた時
                    JFileChooser filechooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            resource.getString("main.InstantMessage.chooser.store"),
                            "txt");
                    filechooser.addChoosableFileFilter(filter);
                    int selected = filechooser.showSaveDialog(null);
                    if (selected == JFileChooser.APPROVE_OPTION) {
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy/MM/dd HH:mm:ss z");
                        File file = filechooser.getSelectedFile();
                        writeOutPlainText(file,
                                "# Lobby System " + sdf.format(new Date()));
                    }
                }
            });
        }
    }

    class StoreAllIMXMLRunnable implements Runnable {
        @Override
        public void run() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // XHTMLで保存ボタンがクリックされた時
                    JFileChooser filechooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            resource.getString("main.InstantMessage.chooser.storeToXML"),
                            "html", "htm");
                    filechooser.addChoosableFileFilter(filter);
                    int selected = filechooser.showSaveDialog(null);
                    if (selected == JFileChooser.APPROVE_OPTION) {
                        File file = filechooser.getSelectedFile();
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy/MM/dd HH:mm:ss z");
                        writeOutXHTML10(
                                file,
                                "Lobby IM Log ("
                                        + sdf.format(new Date(System
                                                .currentTimeMillis())) + ")");
                    }
                }
            });
        }
    }

    public void registerListeners() {
        copyALLMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int start = editorPane.getSelectionStart();
                int end = editorPane.getSelectionEnd();
                editorPane.selectAll();
                editorPane.copy();
                editorPane.select(start, end);
            }
        });
        selectALLMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorPane.selectAll();
            }
        });
        copySelectedMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editorPane.copy();
            }
        });

        storeAllIM.addActionListener(new RunnableCallActionListener(
                new StoreAllIMRunnable()));
        storeAllIMXHTML.addActionListener(new RunnableCallActionListener(
                new StoreAllIMXMLRunnable()));
    }

    public JEditorPane getEditorPane() {
        return editorPane;
    }

    /**
     * <h1>setCaretPolicy</h1> <h2>InstantMessageが表示されるTextPaneのキャレット移動ポリシーを変更する
     * </h2>
     * <p>
     * InstantMessageが表示されるTextPaneのキャレット移動ポリシーを変更します。
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはSwingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     * 
     * @param flag
     *            キャレットを移動させるかどうか、trueの場合末尾移動する
     */
    public void setCaretPolicy(boolean flag) {
        // TODO 自動生成されたメソッド・スタブ
        int policy = flag ? DefaultCaret.ALWAYS_UPDATE
                : DefaultCaret.NEVER_UPDATE;

        DefaultCaret c = (DefaultCaret) editorPane.getCaret();
        c.setUpdatePolicy(policy);
        editorPane.setCaret(c);
    }

    /**
     * <h1>isAutoScrollSelected</h1>
     * <p>
     * 内部のCheckBoxが選択状態を取得する
     * </p>
     * 
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはAbstractButton.isSelectedを呼び出します。
     * </p>
     * <p>
     * Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     * 
     * @return trueの場合チェック
     * @see InnerBottomPanel#isAutoScrollCheckboxState()
     * @see AbstractButton#isSelected()
     */
    public boolean isAutoScrollSelected() {
        return bottomPanel.isAutoScrollCheckboxState();
    }

    /**
     * <h1>getIMFilterSelectedIndex</h1>
     * <p>
     * 内部のComboBoxで選択されているインデックスを取得します
     * </p>
     * 
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはJComboBox.getSelectedIndexを呼び出します。
     * </p>
     * <p>
     * Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
     * </p>
     * 
     * @return 選択されている項目
     */
    public int getIMFilterSelectedIndex() {
        return bottomPanel.getIMFilterSelected();
    }

    class InnerBottomPanel extends JPanel {
        /**
         * 生成シリアルバージョン(やり取りしない)
         */
        private static final long serialVersionUID = 1L;
        private final JCheckBox autoScroll;
        private final JWheelableComboBox cIMFilter;
        private final String[] filter_string = {
                resource.getString("main.InstantMessage.filter.item1"),
                resource.getString("main.InstantMessage.filter.item2"),
                resource.getString("main.InstantMessage.filter.item3") };

        public InnerBottomPanel() {
            setPreferredSize(new Dimension(320, 30));

            GridBagLayout layout = new GridBagLayout();
            setLayout(layout);

            final Dimension d = new Dimension(100, 20);

            autoScroll = new JCheckBox(
                    resource.getString("main.InstantMessage.autoscroll"));
            autoScroll.setPreferredSize(d);

            cIMFilter = new JWheelableComboBox();
            cIMFilter.listener_init();

            cIMFilter.addItem(filter_string[0]);
            cIMFilter.addItem(filter_string[1]);
            cIMFilter.addItem(filter_string[2]);
            cIMFilter.setMinimumSize(d);
            cIMFilter.setMaximumSize(d);
            cIMFilter.setPreferredSize(d);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 10, 4, 10);
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.weightx = 0d;
            gbc.weighty = 0d;
            gbc.fill = GridBagConstraints.NONE;
            layout.setConstraints(autoScroll, gbc);

            gbc.gridx = 2;
            layout.setConstraints(cIMFilter, gbc);

            Component dummy = Box.createGlue();
            gbc.gridx = 0;
            gbc.weightx = 1.0d;
            layout.setConstraints(dummy, gbc);

            add(autoScroll);
            add(cIMFilter);

            add(dummy);
            registerListeners();
        }

        private void registerListeners() {
            cIMFilter.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED)
                        resetEditorPaneText();
                }
            });

            autoScroll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setCaretPolicy(autoScroll.isSelected());
                }
            });
        }

        /**
         * <h1>isAutoScrollCheckboxState</h1>
         * <p>
         * 内部のCheckBoxが選択状態を取得する
         * </p>
         * <h3>スレッドセーフ</h3>
         * <p>
         * このメソッドはAbstractButton.isSelectedを呼び出します。
         * </p>
         * <p>
         * Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
         * </p>
         * 
         * @return trueの場合チェック
         * @see AbstractButton#isSelected()
         */
        public boolean isAutoScrollCheckboxState() {
            return autoScroll.isSelected();
        }

        /**
         * <h1>setAutoScrollCheckboxState</h1>
         * <p>
         * 内部のCheckBoxが選択状態を設定する
         * </p>
         * <h3>スレッドセーフ</h3>
         * <p>
         * このメソッドはAbstractButton.setSelectedを呼び出します。
         * </p>
         * <p>
         * Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
         * </p>
         * 
         * @param b
         *            trueの場合チェック
         * @see AbstractButton#setSelected(boolean)
         */
        public void setAutoScrollCheckboxState(boolean b) {
            autoScroll.setSelected(b);
        }

        /**
         * <h1>setIMFilterSelected</h1>
         * <p>
         * インデックス anIndex にある項目を選択します。
         * </p>
         * <h3>スレッドセーフ</h3>
         * <p>
         * このメソッドはJComboBox.setSelectedIndexを呼び出します。
         * </p>
         * <p>
         * Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
         * </p>
         * 
         * @param anIndex
         *            選択するIndex, 0から始まる
         */
        public void setIMFilterSelected(int anIndex) {
            this.cIMFilter.setSelectedIndex(anIndex);
        }

        /**
         * <h1>getIMFilterSelected</h1>
         * <p>
         * 内部のComboBoxで選択されているインデックスを取得します
         * </p>
         * <h3>スレッドセーフ</h3>
         * <p>
         * このメソッドはJComboBox.getSelectedIndexを呼び出します。
         * </p>
         * <p>
         * Swingのイベントディスパッチスレッド以外から呼び出すことはできません。
         * </p>
         * 
         * @return 選択されている項目
         */
        public int getIMFilterSelected() {
            return this.cIMFilter.getSelectedIndex();
        }
    }
}
