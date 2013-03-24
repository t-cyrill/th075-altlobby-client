package jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting;

public class TableSetting extends Setting {
	// Table Setting
	private final String 	table_headers_order;
	private final Integer 	table_name_width,
							table_ip_width,
							table_uid_width,
							table_state_width,
							table_im_width,
							table_short_msg_width;

	public TableSetting(
		String table_headers_order,
		int table_name_width,
		int table_ip_width,
		int table_uid_width,
		int table_state_width,
		int table_im_width,
		int table_short_msg_width) {
			this.table_headers_order = table_headers_order;
			this.table_name_width = table_name_width;
			this.table_ip_width = table_ip_width;
			this.table_uid_width = table_uid_width;
			this.table_state_width = table_state_width;
			this.table_im_width = table_im_width;
			this.table_short_msg_width = table_short_msg_width;
	}

	public TableSetting(
			String table_headers_order,
			String table_name_width,
			String table_ip_width,
			String table_uid_width,
			String table_state_width,
			String table_im_width,
			String table_short_msg_width) {
				this.table_headers_order = table_headers_order;
				this.table_name_width = toInt(table_name_width, 100);
				this.table_ip_width = toInt(table_ip_width, 100);
				this.table_uid_width = toInt(table_uid_width, 100);
				this.table_state_width = toInt(table_state_width, 100);
				this.table_im_width = toInt(table_im_width, 100);
				this.table_short_msg_width = toInt(table_short_msg_width, 100);
		}

	public String getTable_headers_order() {
		return table_headers_order;
	}

	public Integer getTable_name_width() {
		return table_name_width;
	}

	public Integer getTable_ip_width() {
		return table_ip_width;
	}

	public Integer getTable_uid_width() {
		return table_uid_width;
	}

	public Integer getTable_state_width() {
		return table_state_width;
	}

	public Integer getTable_im_width() {
		return table_im_width;
	}

	public Integer getTable_short_msg_width() {
		return table_short_msg_width;
	}
}
