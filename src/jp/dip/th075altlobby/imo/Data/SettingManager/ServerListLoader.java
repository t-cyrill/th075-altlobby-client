package jp.dip.th075altlobby.imo.Data.SettingManager;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;

public class ServerListLoader {
	public static String[] load(String file) throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		ArrayDeque<String> queue = new ArrayDeque<String>();

		try {
			while (true) {
				String line = br.readLine();
				if(line == null)
					break;
				if(line != "" && line.matches(".+\\(.+:\\d+\\)"))
					queue.add(line);
			}
		} finally {
			br.close();
		}

		return queue.toArray(new String[0]);
	}
}
