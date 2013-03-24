package jp.dip.th075altlobby.imo.Application.test;

import jp.dip.th075altlobby.imo.Data.IPConverter.IPConverter;

public class IPConverterTest {
	public static void main(String[] args) {
		new IPConverterTest().run();
	}

	public void run() {
		byte[] ret = IPConverter.toByteArray("192.168.0.1");
		for(byte b : ret)
			System.out.println(b);
	}
}
