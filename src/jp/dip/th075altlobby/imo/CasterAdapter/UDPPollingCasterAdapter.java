package jp.dip.th075altlobby.imo.CasterAdapter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import jp.dip.th075altlobby.imo.CasterAdapter.Listeners.StateChangeListener;
import jp.dip.th075altlobby.imo.Data.communication.UserInfo;

public class UDPPollingCasterAdapter extends AbstractCasterProcessAdapter {
    private final DatagramSocket socket;
    private final InputThread inputThread;
    private final OutputThread outputThread;
    private final int port;

    // 受信タイムアウト定数[ms]
    private static final int REC_TIMEOUT = 1000;
    private static final long TIMER_PERIOD = 5;

    private final AtomicInteger phase = new AtomicInteger(0);
    private final AtomicInteger mode = new AtomicInteger(0);
    private final AtomicInteger state = new AtomicInteger(0);

    /**
     * このクラスのインスタンスを生成します。
     * 
     * @throws SocketException
     *             ソケットを開くことができなかった場合、 または指定されたローカルポートにソケットをバインドできなかった場合
     * @throws UnknownHostException
     *             接続先ホストが見つからない場合
     */
    public UDPPollingCasterAdapter(int port, StateChangeListener listener)
            throws SocketException, UnknownHostException {
        inputThread = new InputThread();
        outputThread = new OutputThread();
        socket = new DatagramSocket();
        this.port = port;
        setStateChangeListener(listener);
    }

    /**
     * IOスレッドを立ち上げUDPパケットの送受信を可能にします。 startメソッドは一度だけ呼び出す必要があります。
     */
    public void start() {
        ScheduledExecutorService exs = Executors.newScheduledThreadPool(10);
        exs.execute(inputThread);
        exs.execute(outputThread);
        exs.scheduleAtFixedRate(new OutputTimer(), 0, TIMER_PERIOD,
                TimeUnit.MILLISECONDS);
    }

    /**
     * IOスレッドを閉じます。 closeメソッドを呼び出すと内部のDatagramSocketも閉じられます。
     */
    public void close() {
        socket.close();
    }

    class OutputTimer implements Runnable {
        private final byte[] bytes = { 1, 0, 0, 0, 5 };

        @Override
        public void run() {
            outputThread.put(bytes);
        }
    }

    class InputThread implements Runnable {
        @Override
        public void run() {
            byte[] bytes = new byte[64];
            try {
                socket.setSoTimeout(REC_TIMEOUT);
                while (true) {
                    DatagramPacket dp = new DatagramPacket(bytes, 64);

                    try {
                        socket.receive(dp);
                        byte[] bytesData = dp.getData();

                        byte new_phase = bytesData[21];
                        byte new_mode = bytesData[37];

                        if (new_phase != phase.get() || new_mode != mode.get()) {
                            phase.set(new_phase);
                            mode.set(new_mode);

                            int new_state = 0;
                            switch (new_phase) {
                            case CasterConst.phase_none:
                                switch (new_mode) {
                                case CasterConst.mode_default:
                                    // 何もない状態
                                    new_state = CASTER_NONE;
                                    break;
                                }
                                break;
                            case CasterConst.phase_default:
                                switch (new_mode) {
                                case CasterConst.mode_default:
                                    // デフォルトメニュー
                                    new_state = CASTER_MENU;
                                    break;
                                case CasterConst.mode_root:
                                    // 相手の入力待ち
                                    new_state = WAITING_MARGIN_INPUT;
                                    break;

                                case CasterConst.mode_wait:
                                    // 接続待ち
                                    new_state = GATHERING;
                                    break;

                                case CasterConst.mode_access:
                                    // 接続
                                    new_state = CASTER_ACCESS;
                                    break;

                                }
                                break;
                            case CasterConst.phase_menu:
                                /*
                                 * System.out.println("got [" + dp.getLength() +
                                 * "]"); for(int i = 0; i < dp.getLength(); i++)
                                 * { System.out.print(bytesData[i] + " "); if(i
                                 * % 16 == 15) System.out.println(); }
                                 * System.out.println();
                                 */
                                break;
                            }

                            if (new_state != state.get()) {
                                // 新しいモードに変わったことを通知する
                                state.set(new_state);
                                switch (new_state) {
                                case WAITING_MARGIN_INPUT:
                                    fireStateChangeEvent(UserInfo.SETTING);
                                    break;

                                }
                                System.out.println("phase/mode/state = "
                                        + new_phase + "/" + new_mode + "/"
                                        + new_state);
                            }
                        }
                    } catch (SocketTimeoutException e) {
                        // タイムアウト
                        int new_state = NON_RESPONSE;
                        if (state.get() != new_state) {
                            state.set(new_state);
                            fireStateChangeEvent(UserInfo.WAITING);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class OutputThread implements Runnable {
        private final BlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(
                16);
        private final InetAddress server;

        public OutputThread() throws UnknownHostException {
            server = InetAddress.getByName("192.168.0.5");
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // 受け取ったバイト列を格納する
                    byte[] b = this.queue.take();

                    // byte列を処理
                    DatagramPacket dp = new DatagramPacket(b, b.length, server,
                            port);
                    socket.send(dp);
                }
            } catch (InterruptedException e) {
                // 中断された場合
            } catch (IOException e) {
                // IO例外が発生した場合
            }
        }

        public void put(byte[] b) {
            this.queue.add(b);
        }
    }
}
