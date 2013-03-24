package jp.dip.th075altlobby.imo.Data.InstantMessageProvideManager;

public interface HTMLCS {
    /**
     * HTML 3.2のヘッダー、フッターのString
     * 
     * @author Cyrill
     */
    public interface HTML32 {
        /**
         * HTML3.2のヘッダーString 1
         */
        final String HTMLHeader1 = "<HTML><HEAD><TITLE>";
        /**
         * HTML3.2のヘッダーString 2
         */
        final String HTMLHeader2 = "</TITLE></HEAD><BODY><DIV>";
        /**
         * HTML3.2のフッターString
         */
        final String HTMLFooter = "</DIV></BODY></HTML>";
    }

    /**
     * XHTML 1.0 Strictのヘッダー、フッターのString
     * 
     * @author Cyrill
     */
    public interface XHTML10S {
        /**
         * HTML1.0 StrictのヘッダーString 1
         */
        final String HTMLHeader1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
                + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\r\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"ja\" lang=\"ja\">\r\n"
                + " <head>\r\n" + "  <title>";
        /**
         * HTML1.0 StrictのヘッダーString 2
         */
        final String HTMLHeader2 = "</title>\r\n" + " </head>\r\n"
                + " <body>\r\n" + "  <div>\r\n";
        /**
         * HTML1.0のフッターString
         */
        final String HTMLFooter = "  </div>\r\n </body>\r\n</html>";
    }
}