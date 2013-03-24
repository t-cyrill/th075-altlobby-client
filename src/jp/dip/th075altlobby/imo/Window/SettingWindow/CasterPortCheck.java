package jp.dip.th075altlobby.imo.Window.SettingWindow;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ResourceBundle;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import jp.dip.th075altlobby.imo.Resource.Resource;

public class CasterPortCheck {
	private final int port;
	private final ResourceBundle resource = Resource.getBundle();

	public CasterPortCheck(int port) {
		this.port = port;
	}

	class UDPRecieveThreadRunnable implements Runnable {
		private final BlockingQueue<Integer> queue;
		public UDPRecieveThreadRunnable(BlockingQueue<Integer> queue) {
			this.queue = queue;
		}

		@Override
		public void run() {
			try {
				final DatagramSocket udp = new DatagramSocket(port);
				byte[] buf = new byte[2];
				DatagramPacket p = new DatagramPacket(buf, 1);

				ScheduledExecutorService exs = Executors.newScheduledThreadPool(2);
				exs.submit(new Runnable() {
					@Override
					public void run() {
						try {
							Thread.sleep(5000);
							udp.close();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						}
					}
				});
				exs.shutdown();
				udp.receive(p);

				queue.add(0);
			} catch (SocketException e) {
				queue.add(1);
			} catch (IOException e) {
				queue.add(2);
			}
		}
	}

	public void start() {
		ExecutorService exs = Executors.newCachedThreadPool();
		exs.execute(new Runnable() {
			private final ProgressWindow window = new ProgressWindow();
			@Override
			public void run() {
				final ScheduledExecutorService exs = Executors.newScheduledThreadPool(2);
				BlockingQueue<Integer> queue = new ArrayBlockingQueue<Integer>(10);
				exs.execute(new UDPRecieveThreadRunnable(queue));

				window.setVisible(true);
				window.setLabelText(resource.getString("config.casterPort.msg1"));
				window.setProgress(0 * 100 / (4-1));

				try {
					Socket socket = new Socket("th075altlobby.dip.jp", 9557);
					DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

					window.setLabelText(resource.getString("config.casterPort.msg2"));
					window.setProgress(1 * 100 / (4-1));
					dos.writeByte(1);
					dos.writeInt(port);
					dos.close();
					socket.close();
				} catch (IOException e) {
					queue.add(2);
				}

				window.setLabelText(resource.getString("config.casterPort.msg3"));
				window.setProgress(2 * 100 / (4-1));

				int ret;
				try {
					ret = queue.take();
					window.setProgress(3 * 100 / (4-1));
					switch (ret) {
					case 0:
						window.setLabelText(resource.getString("config.casterPort.ok") + " : " + port);
						break;
					case 1:
						window.setLabelText(resource.getString("config.casterPort.socketex") + " : " + port);
						break;
					case 2:
						window.setLabelText(resource.getString("config.casterPort.ioex") + " : " + port);
						break;
					}
					exs.shutdown();
					Thread.sleep(2000);
					window.dispose();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		exs.shutdown();
	}
}
