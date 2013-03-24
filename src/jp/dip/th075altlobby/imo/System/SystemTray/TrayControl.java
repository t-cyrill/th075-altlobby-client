package jp.dip.th075altlobby.imo.System.SystemTray;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;

/**
 * タスクトレイの管理を行う
 * @author Cyrill
 */
public class TrayControl {
	private static TrayIcon trayIcon = null;

	public static void addIcon(Image icon, String desc, PopupMenu popup){
		SystemTray tray = SystemTray.getSystemTray();

		if(SystemTray.isSupported()) {
			trayIcon = new TrayIcon(icon, desc, popup);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}

	public static void setIconActionLister(ActionListener listener){
		trayIcon.addActionListener(listener);
	}

	public static void removeIcon(){
		if (SystemTray.isSupported()) {
			if(trayIcon != null){
				SystemTray.getSystemTray().remove(trayIcon);
				trayIcon = null;
			}
		}
	}
}
