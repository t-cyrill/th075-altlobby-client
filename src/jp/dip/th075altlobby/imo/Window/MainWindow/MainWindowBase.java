package jp.dip.th075altlobby.imo.Window.MainWindow;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

abstract public class MainWindowBase {
	/**
	 * ウィンドウが閉じられたことを示します
	 */
	protected static final int WINDOW_CLOSED = 0;
	protected static final int WINDOW_CLOSE_AND_EXIT = 1;
	
	protected final static Logger logger = Logger.getLogger("jp.dip.th075altlobby.imo.Application");
	protected final Map<String, String> messageMap = new HashMap<String, String>();
}
