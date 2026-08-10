package net.kr1v.devutils.config;

import kr1v.malilibApi.MalilibApi;
import kr1v.malilibApi.annotation.Config;
import kr1v.malilibApi.config.plus.ConfigHotkeyPlus;
import net.kr1v.devutils.DevUtils;

@Config(DevUtils.MOD_ID)
public class Main {
	public static final ConfigHotkeyPlus OPEN_SCREEN = new ConfigHotkeyPlus("Open screen", "R,C").setPressCallback((keyAction, iKeybind) -> {
		MalilibApi.openScreenFor(DevUtils.MOD_ID);
		return true;
	});
}
