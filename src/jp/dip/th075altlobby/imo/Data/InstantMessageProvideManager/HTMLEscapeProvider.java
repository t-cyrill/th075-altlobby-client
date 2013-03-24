package jp.dip.th075altlobby.imo.Data.InstantMessageProvideManager;

public class HTMLEscapeProvider {
    public static String escape(String strVal, boolean xhtml) {
        StringBuffer strResult = new StringBuffer();
        for (int i = 0; i < strVal.length(); i++) {
            switch (strVal.charAt(i)) {
            case '&':
                strResult.append("&amp;");
                break;
            case '<':
                strResult.append("&lt;");
                break;
            case '>':
                strResult.append("&gt;");
                break;
            default:
                strResult.append(strVal.charAt(i));
                break;
            }
        }

        if (xhtml)
            return strResult.toString().replace("\n", "<BR>");
        return strResult.toString().replace("\n", "<br />");
    }
}
