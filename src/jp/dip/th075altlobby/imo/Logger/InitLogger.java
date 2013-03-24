package jp.dip.th075altlobby.imo.Logger;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InitLogger {
    /**
     * Loggerの初期化
     * 
     * @return
     */
    public static Logger initLogger(String nameSpace) {
        Logger logger = Logger.getLogger(nameSpace);
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        logger.setLevel(Level.INFO);
        // logger.addHandler(consoleHandler);
        return logger;
    }
}
