package jp.dip.th075altlobby.imo.Data.SettingManager;

public class ValueSet {
	public String	in;
	public Integer def;
	
	public ValueSet(String in,Integer def){
		this.in = in;
		this.def = def;
	}
	
	public Integer parse(){
		try {
			return Integer.valueOf(this.in);
		}catch (NumberFormatException e) {
			return Integer.valueOf(this.def);
		}
	}
}