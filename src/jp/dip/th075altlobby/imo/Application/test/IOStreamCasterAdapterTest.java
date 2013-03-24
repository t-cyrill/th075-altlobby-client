package jp.dip.th075altlobby.imo.Application.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import jp.dip.th075altlobby.imo.CasterAdapter.CasterEventInterface;
import jp.dip.th075altlobby.imo.CasterAdapter.IOStreamCasterAdapter;
import jp.dip.th075altlobby.imo.CasterAdapter.Listeners.StateChangeListener;

public class IOStreamCasterAdapterTest {
    private IOStreamCasterAdapter adapter;

    public static void main(String[] args) throws IOException {
        IOStreamCasterAdapterTest instance = new IOStreamCasterAdapterTest();
        instance.run();
    }

    public void run() throws IOException {
        adapter = new IOStreamCasterAdapter(new CasterEventListener(),
                new CasterStateChangeListener(),
                "R:\\ゲーム\\東方萃夢想\\RollCaster.exe", "-w");
    }

    class CasterStateChangeListener extends StateChangeListener {
        @Override
        public void stateChanged(int state) {
            System.out.println("stateChanged : " + state);
        }
    }

    class CasterEventListener implements CasterEventInterface {
        @Override
        public void alreadyFighting() {

        }

        @Override
        public void bufferMarginPrinted(int margin) {

        }

        @Override
        public void closed() {
            System.out.println("casterが終了されました。");
        }

        @Override
        public void connected(String s) {
            System.out.println("connected : " + s);
        }

        @Override
        public void defaultCall(String s) {

        }

        @Override
        public void failed(int reason) {
            System.out.println("Failed : " + reason);
        }

        @Override
        public void inputBufferMarginTiming() {
            System.out.println("入力待ちです");
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    System.in));
            try {
                int margin = Integer.parseInt(br.readLine());
                adapter.fireInputedMarginEvent(margin);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void inputedBufferMargin(int margin) {
            System.out.println("inputedBufferMargin : " + margin);
        }

        @Override
        public void observedDelay(double realDelay) {
            System.out.println("realDelay : " + realDelay);
        }

        @Override
        public void waitingOppositePlayerInput() {
            System.out.println("相手の入力待ちです。");
        }
    }
}
