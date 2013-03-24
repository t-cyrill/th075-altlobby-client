package jp.dip.th075altlobby.imo.CasterAdapter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.dip.th075altlobby.imo.CasterAdapter.Listeners.StateChangeListener;
import jp.dip.th075altlobby.imo.Data.communication.UserInfo;
import jp.dip.th075altlobby.imo.ProcessAdapter.CallBackRunnable;
import jp.dip.th075altlobby.imo.ProcessAdapter.ProcessAdapter;

/**
 * <h1>IOStreamCasterAdapter</h1>
 * <p>
 * ProcessのIOストリームを使いCasterとの情報交換を行う機能を提供します。
 * </p>
 * <p>
 * このクラスはCasterの出力を拾いイベントにラップします。 必要なタイミングでイベントは{@link CasterEventInterface}または
 * {@link StateChangeListener}に通知されます。
 * </p>
 * 
 * @version rev27
 * @author Cyrill
 * 
 */
public class IOStreamCasterAdapter extends AbstractCasterProcessAdapter {
    private final ProcessAdapter _process;
    private final CasterEventInterface _eventInterface;
    private final AtomicInteger margin = new AtomicInteger(0);

    public IOStreamCasterAdapter(CasterEventInterface eventInterface,
            StateChangeListener listener, String... command) throws IOException {
        _process = new ProcessAdapter(new CasterOutputRunnable(),
                new ClosedRunnable(), command);
        _eventInterface = eventInterface;
        setStateChangeListener(listener);
    }

    /**
     * イベント通知用のインターフェイスを取得します。
     * 
     * @return イベント通知用インターフェイス
     */
    public CasterEventInterface getEventInterface() {
        return _eventInterface;
    }

    class ClosedRunnable implements Runnable {
        @Override
        public void run() {
            fireStateChangeEvent(UserInfo.WAITING);
            _eventInterface.closed();
        }
    }

    /**
     * <h2>プロセスを終了する</h2>
     * <p>
     * 管理しているcasterプロセスを終了させます。(<strong>任意のオペレーション</strong>)
     * </p>
     * <p>
     * このメソッドのオペレーションをサポートするクラスはプロセスの終了が確認できるまでブロックします。
     * </p>
     * 
     */
    public void close() {
        _process.close();
        return;
    }

    /**
     * <h1>setMargin</h1> <h2>Margin値を上書きする</h2>
     * <p>
     * Margin値を上書きします。
     * </p>
     * 
     * @param margin
     */
    public void setMargin(int margin) {
        this.margin.set(margin);
    }

    /**
     * <h1>getMargin</h1> <h2>Margin値を読み取る</h2>
     * <p>
     * Margin値を読み取ります。
     * </p>
     * 
     */
    public int getMargin() {
        return this.margin.get();
    }

    /**
     * Marginが入力されたときに呼ばれる
     * 
     * @param margin
     *            マージン値
     */
    public void fireInputedMarginEvent(int margin) {
        // プロセスに書き出す
        _process.write(String.valueOf(margin) + "\n");

        if (margin != 0)
            fireStateChangeEvent(UserInfo.FIGHTING);
    }

    /**
     * 選択されたときに呼ばれる
     * 
     * @param selected
     */
    public void fireSelectedWatchEvent(int selected) {
        // プロセスに書き出す
        _process.write(String.valueOf(selected) + "\n");

        if (selected == 1)
            fireStateChangeEvent(UserInfo.WATCHING);
    }

    /**
     * 選択されたときに呼ばれる
     * 
     * @param selected
     */
    public void fireSelectedEvent(int selected) {
        // プロセスに書き出す
        _process.write(String.valueOf(selected) + "\n");
    }

    /**
     * casterの出力が行われたときに呼び出されるメソッド。
     * 
     * @param s
     *            出力された文字列
     * @since rev30
     */
    protected void casterOutput(String s) {

    }

    /**
     * <h2>casterストリームに文字列を書き出す。</h2>
     * <p>
     * Casterの入力ストリームに書き出します。文字列sの後ろには改行コード\nが自動的に付加されます。
     * </p>
     * <p>
     * このメソッドは柔軟な操作を行う機能を提供する一方で、想定されない操作を実行する可能性があります。 一般にはカプセル化された
     * {@link IOStreamCasterAdapter#fireInputedMarginEvent(int)}
     * のようなイベント着火型のメソッドを呼び出してください。 イベントカプセル化を行うクラスはこのメソッドを呼び出すべきではありません。
     * </p>
     * <p>
     * このメソッドは主にユーザーの手動でのオペレートを可能にするために提供されます。
     * </p>
     * 
     * @param s
     *            書き出す文字列
     * @see IOStreamCasterAdapter#fireInputedMarginEvent(int)
     * @see IOStreamCasterAdapter#fireSelectedEvent(int)
     * @see IOStreamCasterAdapter#fireSelectedWatchEvent(int)
     * @see IOStreamCasterAdapter#fireStateChangeEvent(int)
     * @since rev31
     * 
     */
    protected void writeCasterStream(String s) {
        _process.write(s);
    }

    protected class CasterOutputRunnable extends CallBackRunnable {
        @Override
        public void run(String s) {
            casterOutput(s);

            // 接続待ちサイド
            if (s.matches("Access from (.*)"))
                accessFrom(s);

            // ディレイの観測
            else if (s.matches("About (.*)\\[ms\\] delay exist in a round\\."))
                existsDelay(s);
            else if (s.equals("Now waiting for buffer margin value."))
                waitingOppositePlayerInput();
            else if (s.matches("Delay : About (.*)\\[ms\\]"))
                delayInfomation(s);

            // 入力されたmargin値の出力
            // Roll 入力側
            else if (s.matches("Buffer margin : (\\d+)"))
                inputedBufferMargin(s);
            // 全タイプ待ち側
            else if (s
                    .matches("Buffer margin : (\\d+) \\( Observed about (.*)\\[.*"))
                inputedBufferMarginWithObservedDelay(s);

            else if (s.equals("1 : Continue"))
                alreadyFighting();

            else if (s.matches("debug : Buffer Margin ( delayTime ) (\\d+)"))
                bufferMarginPrinted(s);

            // else if(s.equals("11: Script"))
            // eventInterface.rollCasterWaitingInput();
            // else if(s.matches("  Input delay : (\\d+)"))
            // rollInputDelayPrinted(s);
            // else if(s.matches("Rewind frames : (\\d+)"))
            // eventInterface.rewindFrames(s);
            // else if(s.length() > 8 && s.substring(0, 8).equals("Phase : "))
            // eventInterface.phasePrinted(s);
            // else if(s.equals("3 : Specific Port"))
            // eventInterface.portSetting();

            // エラー処理
            else if (s.equals("debug : Bind error."))
                _eventInterface.failed(CasterEventInterface.BIND_ERROR);
            else if (s.equals("Input >ERROR : TIMEOUT ( Away )"))
                _eventInterface.failed(CasterEventInterface.INPUT_TIMEOUT);
            else if (s.equals("Input Buffer [\\d+] >ERROR : TIMEOUT ( Away )"))
                _eventInterface.failed(CasterEventInterface.INPUT_TIMEOUT);
            else if (s.equals("ERROR : TIMEOUT ( Away )"))
                _eventInterface.failed(CasterEventInterface.TIMEOUT_AWAY);
            else if (s.equals("ERROR : TIMEOUT ( Access )"))
                _eventInterface.failed(CasterEventInterface.TIMEOUT_ACCESS);
            else if (s.equals("ERROR : th075 start failed."))
                _eventInterface.failed(CasterEventInterface.TH075FAILED);

            // デフォルト
            else
                _eventInterface.defaultCall(s);
        }

        private void bufferMarginPrinted(String s) {
            Pattern pt = Pattern
                    .compile("debug : Buffer Margin ( delayTime ) (\\d+)");
            Matcher matcher = pt.matcher(s);
            if (matcher.matches())
                _eventInterface.bufferMarginPrinted(Integer.parseInt(matcher
                        .group(1)));
        }

        private void alreadyFighting() {
            fireStateChangeEvent(UserInfo.SETTING);
            _eventInterface.alreadyFighting();
        }

        private void waitingOppositePlayerInput() {
            fireStateChangeEvent(UserInfo.SETTING);
            _eventInterface.waitingOppositePlayerInput();
        }

        private void accessFrom(String s) {
            Pattern pt = Pattern.compile("Access from (.*)");
            Matcher matcher = pt.matcher(s);
            String connected_from_ip = "";
            if (matcher.matches())
                connected_from_ip = matcher.group(1);
            String[] splited = connected_from_ip.split(":");
            _eventInterface.connected(splited[0]);
        }

        private void inputedBufferMargin(String s) {
            Pattern pt = Pattern.compile("Buffer margin : (\\d+)");
            Matcher matcher = pt.matcher(s);
            if (matcher.matches()) {
                int delay_buffer = Integer.parseInt(matcher.group(1));
                _eventInterface.inputedBufferMargin(delay_buffer);
            }
        }

        private void inputedBufferMarginWithObservedDelay(String s) {
            Pattern pt = Pattern
                    .compile("Buffer margin : (\\d+) \\( Observed about (.*)\\[.*");
            Matcher matcher = pt.matcher(s);
            if (matcher.matches()) {
                int delay_buffer = Integer.parseInt(matcher.group(1));
                double real_delay = Double.parseDouble(matcher.group(2));

                _eventInterface.observedDelay(real_delay);
                _eventInterface.inputedBufferMargin(delay_buffer);

                fireStateChangeEvent(UserInfo.FIGHTING);
            }
        }

        private void existsDelay(String s) {
            Pattern pt = Pattern
                    .compile("About (.*)\\[ms\\] delay exist in a round\\.");
            Matcher matcher = pt.matcher(s);
            double real_delay = -1;
            if (matcher.matches())
                real_delay = Double.parseDouble(matcher.group(1));
            fireStateChangeEvent(UserInfo.SETTING);
            _eventInterface.observedDelay(real_delay);
            _eventInterface.inputBufferMarginTiming();
        }

        public void delayInfomation(String s) {
            Pattern pt = Pattern.compile("Delay : About (.*)\\[ms\\]");
            Matcher matcher = pt.matcher(s);
            double real_delay = -1;
            if (matcher.matches())
                real_delay = Double.parseDouble(matcher.group(1));
            _eventInterface.observedDelay(real_delay);
            _eventInterface.inputBufferMarginTiming();
        }
    }
}
