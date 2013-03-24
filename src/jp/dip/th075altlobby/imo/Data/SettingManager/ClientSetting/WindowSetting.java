package jp.dip.th075altlobby.imo.Data.SettingManager.ClientSetting;

public class WindowSetting extends Setting {
	// Window Setting
	private final Integer 	horizontal_split_devider_location,
							left_vertical_split_devider_location,
							right_vertical_split_devider_location,
							right_vertical_split_instantMessage_to_casterTerminalPane_devider_location,
							main_win_pos_x,
							main_win_pos_y,
							main_win_width,
							main_win_height;
	// IM Controls
	private final Integer	IM_Color,
							IM_Filter;
	private final Boolean 	autoScrollCheck;

	public WindowSetting(
			int horizontal_split_devider_location,
			int left_vertical_split_devider_location,
			int right_vertical_split_devider_location,
			int right_vertical_split_instantMessage_to_casterTerminalPane_devider_location,
			int main_win_pos_x,
			int main_win_pos_y,
			int main_win_width,
			int main_win_height,
			int IM_Color,
			int IM_Filter,
			boolean autoScrollCheck) {
		this.horizontal_split_devider_location = horizontal_split_devider_location;
		this.left_vertical_split_devider_location = left_vertical_split_devider_location;
		this.right_vertical_split_devider_location = right_vertical_split_devider_location;
		this.right_vertical_split_instantMessage_to_casterTerminalPane_devider_location = right_vertical_split_instantMessage_to_casterTerminalPane_devider_location;
		this.main_win_pos_x = main_win_pos_x;
		this.main_win_pos_y = main_win_pos_y;
		this.main_win_width = main_win_width;
		this.main_win_height = main_win_height;
		this.IM_Color = IM_Color;
		this.IM_Filter = IM_Filter;
		this.autoScrollCheck = autoScrollCheck;
	}


	public WindowSetting(
			String horizontal_split_devider_location,
			String left_vertical_split_devider_location,
			String right_vertical_split_devider_location,
			String right_vertical_split_instantMessage_to_casterTerminalPane_devider_location,
			String main_win_pos_x,
			String main_win_pos_y,
			String main_win_width,
			String main_win_height,
			String IM_Color,
			String IM_Filter,
			String console_auto_scroll) {
		this.horizontal_split_devider_location = toInt(horizontal_split_devider_location, 400);
		this.left_vertical_split_devider_location = toInt(left_vertical_split_devider_location, 400);
		this.right_vertical_split_devider_location = toInt(right_vertical_split_devider_location, 200);
		this.right_vertical_split_instantMessage_to_casterTerminalPane_devider_location = toInt(right_vertical_split_instantMessage_to_casterTerminalPane_devider_location, 360);
		this.main_win_pos_x = toInt(main_win_pos_x, 100);
		this.main_win_pos_y = toInt(main_win_pos_y, 100);
		this.main_win_width = toInt(main_win_width, 640);
		this.main_win_height = toInt(main_win_height, 480);
		this.IM_Color = toInt(IM_Color, 0);
		this.IM_Filter = toInt(IM_Filter, 0);
		this.autoScrollCheck = Boolean.valueOf(console_auto_scroll);
	}

	public Integer getHorizontalSplitDeviderLocation() {
		return horizontal_split_devider_location;
	}

	public Integer getLeftVerticalSplitDeviderLocation() {
		return left_vertical_split_devider_location;
	}

	public Integer getRightVerticalSplitDeviderLocation() {
		return right_vertical_split_devider_location;
	}

	public Integer getRightVerticalSplitInstantMessageToCasterTerminalPaneDeviderLocation() {
		return right_vertical_split_instantMessage_to_casterTerminalPane_devider_location;
	}

	public Integer getMainWindowXpos() {
		return main_win_pos_x;
	}

	public Integer getMainWindowYPos() {
		return main_win_pos_y;
	}

	public Integer getMainWindowWidth() {
		return main_win_width;
	}

	public Integer getMainWindowHeight() {
		return main_win_height;
	}

	public Integer getIM_Color() {
		return IM_Color;
	}

	public Integer getIM_Filter() {
		return IM_Filter;
	}

	public Boolean getAutoScroll() {
		return autoScrollCheck;
	}
}
