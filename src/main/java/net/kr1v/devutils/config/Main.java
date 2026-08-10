package net.kr1v.devutils.config;

import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.hotkeys.KeybindSettings;
import kr1v.malilibApi.MalilibApi;
import kr1v.malilibApi.annotation.Config;
import kr1v.malilibApi.config.plus.ConfigBooleanHotkeyedPlus;
import kr1v.malilibApi.config.plus.ConfigHotkeyPlus;
import net.kr1v.devutils.DevUtils;

@Config(DevUtils.MOD_ID)
public class Main {
	public static final ConfigHotkeyPlus OPEN_SCREEN = new ConfigHotkeyPlus("Open screen", "R,C").setPressCallback((keyAction, iKeybind) -> {
		MalilibApi.openScreenFor(DevUtils.MOD_ID);
		return true;
	});

	public static final ConfigBooleanHotkeyedPlus HIDE_SCREENS = new ConfigBooleanHotkeyedPlus("Hide screen rendering", false, "RIGHT_SHIFT")
			.setAllowExtraKeys(true).setActivateOn(KeyAction.BOTH).setContext(KeybindSettings.Context.ANY);
	static {
		HIDE_SCREENS.setPressCallback((action, key) -> {
			HIDE_SCREENS.set(!HIDE_SCREENS.get());
			return true;
		});
	}

}
