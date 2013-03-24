package jp.dip.th075altlobby.imo.Application.test;

import java.net.SocketException;
import java.net.UnknownHostException;

import jp.dip.th075altlobby.imo.CasterAdapter.UDPPollingCasterAdapter;
import jp.dip.th075altlobby.imo.CasterAdapter.Listeners.StateChangeListener;

public class UDPPollingCasterAdapterTest {
    public UDPPollingCasterAdapterTest() {

    }

    public static void main(String[] args) {
        UDPPollingCasterAdapterTest instance = new UDPPollingCasterAdapterTest();
        instance.run();
    }

    public void run() {
        try {
            UDPPollingCasterAdapter adapter = new UDPPollingCasterAdapter(7500,
                    new StateChangeListener() {
                        @Override
                        public void stateChanged(int state) {
                            System.out.println("state = " + state);
                        }
                    });
            adapter.start();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
