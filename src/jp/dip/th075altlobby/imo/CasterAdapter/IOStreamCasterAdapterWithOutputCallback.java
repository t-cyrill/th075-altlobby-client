package jp.dip.th075altlobby.imo.CasterAdapter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jp.dip.th075altlobby.imo.CasterAdapter.Listeners.StateChangeListener;
import jp.dip.th075altlobby.imo.ProcessAdapter.CallBackRunnable;

/**
 * <h2>casterの出力を一行ごとに取得可能に拡張したIOStreamCasterAdapter</h2>
 * <p>
 * rev 27から追加された{@link IOStreamCasterAdapter#casterOutput(String)}をオーバーライドすることで、
 * 一行ごとの出力を通知可能に拡張した{@link IOStreamCasterAdapter}です。
 * </p>
 * <p>
 * 一行ごとの出力を拾う以外はIOStreamCasterAdapterと同等の機能を持ちます。
 * </p>
 * 
 * @since rev27
 * @author Cyrill
 * 
 */
public class IOStreamCasterAdapterWithOutputCallback extends
        IOStreamCasterAdapter {
    private final CallBackRunnable callback;
    private final Logger logger = Logger.getLogger("jp.dip.th075altlobby");

    public IOStreamCasterAdapterWithOutputCallback(
            CasterEventInterface eventInterface, StateChangeListener listener,
            CallBackRunnable outputCallBack, String... command)
            throws IOException {
        super(eventInterface, listener, command);
        callback = outputCallBack;
    }

    @Override
    protected void casterOutput(String s) {
        if (callback == null)
            logger.log(Level.SEVERE, "callbackがnullです。");
        if (s == null)
            logger.log(Level.SEVERE, "sがnullです。");
        callback.run(s);
    }

    @Override
    public void writeCasterStream(String s) {
        super.writeCasterStream(s);
    }
}
