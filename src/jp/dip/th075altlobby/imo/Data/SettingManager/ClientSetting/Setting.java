package jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting;

public class Setting {
    /**
     * Integer.valueOfを呼び出しStringをintに変換します。
     * NumberFormatExceptionは内部でキャッチされます。変換に失敗した場合はdefaultを返します。
     * 
     * @param string
     *            変換する文字列
     * @param defalut
     *            変換できない場合のデフォルト値
     * @return 変換した結果
     */
    protected int toInt(String string, int defalut) {
        if (string == null)
            return defalut;
        try {
            return Integer.valueOf(string);
        } catch (NumberFormatException e) {
            return defalut;
        }
    }
}
