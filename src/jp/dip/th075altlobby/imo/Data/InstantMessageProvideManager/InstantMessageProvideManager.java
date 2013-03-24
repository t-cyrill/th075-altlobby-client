package jp.dip.th075altlobby.imo.Data.InstantMessageProvideManager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Date;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import jp.dip.th075altlobby.imo.Data.communication.InstantMessage;

public class InstantMessageProvideManager {
    private final Deque<InstantMessage> buf;
    private final SimpleDateFormat sdfAuto = new SimpleDateFormat("yyyy-MM-dd");
    private final PlainTextInstantMessageFormatter textFormatter = new PlainTextInstantMessageFormatter();
    private final AtomicBoolean autoWriteFlag = new AtomicBoolean(false);
    private final AtomicInteger log_hold_limit = new AtomicInteger(
            Integer.MAX_VALUE);
    private BufferedWriter autoFileWriter;

    public InstantMessageProvideManager() {
        buf = new ArrayDeque<InstantMessage>();
    }

    /**
     * <h1>append</h1> <h2>IMを追加する</h2>
     * <p>
     * キューにIMを追加します。
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはフィールドbufにより同期化されます。
     * </p>
     * 
     * @param im
     *            追加するIM
     */
    public void append(InstantMessage im) {
        synchronized (buf) {
            buf.add(im);
            if (buf.size() > log_hold_limit.get())
                buf.remove();
            if (autoWriteFlag.get())
                autoWriteAppend(im);
        }
    }

    /**
     * <h1>autoWriteAppend</h1> このメソッドはフィールドautoFileWriterにより同期化されます。
     * 
     * @param im
     */
    private void autoWriteAppend(InstantMessage im) {
        if (autoFileWriter == null)
            return;

        synchronized (autoFileWriter) {
            try {
                autoFileWriter.write(textFormatter.format(im));
                autoFileWriter.newLine();
                autoFileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * <h1>getInnerIMtoArray</h1> <h2>内部で保持しているIMを配列として返す。</h2>
     * <p>
     * このオブジェクトが保持しているIMを配列として返します。 appendメソッドにより追加した順番は保障されます。
     * </p>
     * <h3>スレッドセーフ</h3>
     * <p>
     * このメソッドはフィールドbufにより同期化されます。
     * </p>
     * 
     * @return オブジェクトが保持しているIM配列
     */
    public InstantMessage[] getInnerIMtoArray() {
        synchronized (buf) {
            return buf.toArray(new InstantMessage[0]);
        }
    }

    /**
     * <h1>toString</h1> <h2>オブジェクトの文字列表現を返す。</h2>
     * <p>
     * {@link InstantMessageProvideManager}の toString メソッドは
     * appendで追加された文字列を適切なテキスト形式でフォーマットした文字列を返します。
     * </p>
     * <p>
     * このメソッドが返す文字列は内部キューを
     * {@link PlainTextInstantMessageFormatter#format(InstantMessage[])}
     * でフォーマットした文字列と等価です。
     * </p>
     * 
     * @return このオブジェクトの文字列表現
     */
    @Override
    public String toString() {
        return textFormatter.format(getInnerIMtoArray());
    }

    /**
     * <h1>setAutoFileOutputFlag</h1> <h2>自動ファイル書き出し機能の有効/無効を切り替える</h2>
     * <p>
     * オート書き出し機能の有効向こうを切り替えます。
     * trueが設定された場合、yyyy-mm-dd.txtのファイルストリームを開き、以降のappendメソッド呼び出し時に自動的に書き込まれます。
     * trueからfalseに切り替えられたとき、既に開かれているストリームは閉じられます。
     * </p>
     * <p>
     * 開かれているストリームはこのメソッドを用いて明示的に閉じられなければなりません。
     * </p>
     * 
     * @param f
     *            trueで有効、falseで無効
     */
    public void setAutoFileOutputFlag(boolean f) {
        // Writerを開いている場合、閉じる
        try {
            if (autoFileWriter != null) {
                autoFileWriter.close();
                autoFileWriter = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // スイッチ切り替え
        autoWriteFlag.set(f);
        if (f) {
            // ONの場合、新たにWriterを開く
            try {
                Date d = new Date();
                String filename = sdfAuto.format(d) + ".txt";
                autoFileWriter = new BufferedWriter(new FileWriter(new File(
                        filename), true));
            } catch (IOException e) {
                e.printStackTrace();
                f = false;
            }
        }
    }

    /**
     * <h1>closeAutoFileOutputStream</h1> <h2>
     * 自動ファイル書き出しで利用されているファイル書き出しストリームを閉じる</h2>
     * <p>
     * ファイル書き出し用ストリームを閉じます。このメソッドは、setAutoFileOutputFlag(false)と等価です。
     * </p>
     * <p>
     * setAutoFileOutputFlagまたはこのメソッドは終了時までに明示的に呼び出し、ストリームを閉じる必要があります。
     * </p>
     * 
     * @see InstantMessageProvideManager#setAutoFileOutputFlag(boolean)
     */
    public void closeAutoFileOutputStream() {
        setAutoFileOutputFlag(false);
    }

    public void setLogHoldLimit(int limit) {
        if (limit <= 0)
            limit = Integer.MAX_VALUE;

        log_hold_limit.set(limit);
    }

    /**
     * <h1>clearAll</h1> <h2>内部キューを空にする</h2>
     * <p>
     * {@link Collection#clear()}を呼び出し、このオブジェクトが内部で保持しているキューを空にします。
     * </p>
     */
    public void clearAll() {
        synchronized (buf) {
            buf.clear();
        }
    }
}
