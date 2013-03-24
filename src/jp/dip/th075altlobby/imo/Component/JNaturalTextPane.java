package jp.dip.th075altlobby.imo.Component;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;

public class JNaturalTextPane extends JPanel {
    /**
     * 生成シリアルID
     */
    private static final long serialVersionUID = 5476822471779804565L;
    private JTextArea _innerTextPane;
    private JDefaultMenuForEditControl menu;

    public JNaturalTextPane() {
        menu = new JDefaultMenuForEditControl();

        _innerTextPane = new JTextArea();
        _innerTextPane.setFont(new Font("ＭＳ Ｐゴシック", Font.PLAIN, 12));

        _innerTextPane.setLineWrap(true);
        _innerTextPane.setPreferredSize(null);
        _innerTextPane.setComponentPopupMenu(menu);

        setLayout(new BorderLayout());
        add(_innerTextPane, BorderLayout.CENTER);
    }

    public JTextArea getInnerTextPane() {
        return _innerTextPane;
    }

    /**
     * <h1>setEditable</h1>
     * <p>
     * 内部の TextComponent が編集可能かどうかを設定します。
     * </p>
     * <p>
     * 状態が変更されると、PropertyChange イベント (editable) が発生します。
     * </p>
     * 
     * @param b
     *            設定される boolean
     */
    public void setEditable(boolean b) {
        _innerTextPane.setEditable(b);
    }

    /**
     * 内部で保持しているJTextAreaのgetTextを呼び出します。
     * 
     * @return 内部テキストペインの文字列
     * @see JTextComponent#getText()
     */
    public String getText() {
        return _innerTextPane.getText();
    }

    /**
     * 内部で保持しているJTextAreaのsetTextを呼び出します。
     * 
     * @param text
     *            設定するテキスト
     * @see JTextComponent#setText(String)
     */
    public void setText(String t) {
        this._innerTextPane.setText(t);
    }

    /**
     * キーリスナーを追加する
     * 
     * @see JTextArea#addKeyListener(KeyListener)
     */
    public void addKeyListener(KeyListener keylistener) {
        _innerTextPane.addKeyListener(keylistener);
    }

    /**
     * <p>
     * 指定されたテキストをドキュメントの末尾に追加します。
     * </p>
     * <p>
     * このメソッドは例外的にスレッドセーフです。{@link JTextArea#append(String)}を参照してください。
     * </p>
     * 
     * @see JTextArea#append(String)
     * @param s
     *            追加するテキスト
     */
    public void append(String s) {
        _innerTextPane.append(s);
    }

    /**
     * 現在の位置で改行する
     */
    public void newline() {
        _innerTextPane.insert("\r\n", _innerTextPane.getCaretPosition());
    }

    /**
     * デフォルトのポップアップメニュー
     * 
     * @author Cyrill
     */
    class JDefaultMenuForEditControl extends JPopupMenu {
        /**
         * 生成シリアルID
         */
        private static final long serialVersionUID = 7355093294721005139L;
        private JMenuItem undoMenuItem, cutMenuItem, copySelectedMenuItem,
                SelectALLMenuItem, pasteMenuItem;

        public JDefaultMenuForEditControl() {
            // TODO 自動生成されたコンストラクター・スタブ
            undoMenuItem = new JMenuItem("元に戻す(U)");
            cutMenuItem = new JMenuItem("切り取り(X)");
            copySelectedMenuItem = new JMenuItem("コピー(C)");
            pasteMenuItem = new JMenuItem("貼り付け(P)");
            SelectALLMenuItem = new JMenuItem("すべて選択(A)");
            undoMenuItem.setMnemonic(KeyEvent.VK_U);
            cutMenuItem.setMnemonic(KeyEvent.VK_X);
            cutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X,
                    InputEvent.CTRL_DOWN_MASK));
            copySelectedMenuItem.setMnemonic(KeyEvent.VK_C);
            copySelectedMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
            SelectALLMenuItem.setMnemonic(KeyEvent.VK_A);
            SelectALLMenuItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
            pasteMenuItem.setMnemonic(KeyEvent.VK_V);
            pasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V,
                    InputEvent.CTRL_DOWN_MASK));

            // メニューに追加する
            // add(undoMenuItem);
            // addSeparator();
            add(cutMenuItem);
            add(copySelectedMenuItem);
            add(pasteMenuItem);
            addSeparator();
            add(SelectALLMenuItem);

            MenuActionListener listener = new MenuActionListener();
            undoMenuItem.addActionListener(listener);
            cutMenuItem.addActionListener(listener);
            copySelectedMenuItem.addActionListener(listener);
            pasteMenuItem.addActionListener(listener);
            SelectALLMenuItem.addActionListener(listener);
        }

        class MenuActionListener implements ActionListener, ClipboardOwner {
            @Override
            public void actionPerformed(ActionEvent e) {
                // TODO 自動生成されたメソッド・スタブ
                if (e.getSource().equals(undoMenuItem))
                    // innerTextPane.;
                    ;
                else if (e.getSource().equals(cutMenuItem))
                    _innerTextPane.cut();
                else if (e.getSource().equals(copySelectedMenuItem))
                    _innerTextPane.copy();
                else if (e.getSource().equals(pasteMenuItem))
                    _innerTextPane.paste();
                else if (e.getSource().equals(SelectALLMenuItem))
                    _innerTextPane.selectAll();
            }

            @Override
            public void lostOwnership(Clipboard clipboard, Transferable contents) {
            }
        }
    }

    /**
     * キャレットの位置を行末に移動します。 この操作を行うことで、テキストコンポーネントのスクロールが行われ、行末が表示されます。
     * 
     */
    public void setCaretPositionBottom() {
        _innerTextPane.setCaretPosition(_innerTextPane.getText().length());
    }
}
