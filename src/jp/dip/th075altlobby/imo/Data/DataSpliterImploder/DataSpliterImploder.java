package jp.dip.th075altlobby.imo.Data.DataSpliterImploder;

public class DataSpliterImploder {
    /**
     * 文字列を分解して、数値型配列にパースする。
     * 
     * @param string
     *            分割する文字列
     * @param splitter
     *            スプリッター
     */
    public static int[] split(String string, String splitter) {
        int[] ret = new int[6];
        if (string == null)
            throw new NullPointerException("第一引数 stringがnullです。");
        if (splitter == null)
            throw new NullPointerException("第二引数 splitterがnullです。");
        String[] dummy = string.split(splitter);
        for (int i = 0; i < dummy.length; i++) {
            try {
                ret[i] = Integer.valueOf(dummy[i]);
            } catch (NumberFormatException e) {
                ret[i] = 0;
            }
        }
        return ret;
    }

    /**
     * 数値配列を結合して、ひとつの文字列にする。
     * 
     * @param array
     *            結合する数値配列
     * @param splitter
     *            スプリッター
     * @return 結合された文字列
     */
    public static String implode(int[] array, String splitter) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++)
            sb.append(String.valueOf(array[i])).append(",");
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}
