package kr1v.utils.config;

import kr1v.malilibApi.MalilibApi;
import kr1v.malilibApi.annotation.Config;
import kr1v.malilibApi.config.plus.*;
import kr1v.utils.UtilsClient;
import kr1v.utils.screen.DummyScreen;
import net.minecraft.client.MinecraftClient;

@SuppressWarnings("unused")
@Config(UtilsClient.MOD_ID)
public class Misc {
	static final ConfigHotkeyPlus HOTKEY = new ConfigHotkeyPlus("Open gui", "G, C", (action, key) -> {
		MalilibApi.openScreenFor(UtilsClient.MOD_ID);
		return true;
	});
	static final ConfigHotkeyPlus SHOW_CURSOR = new ConfigHotkeyPlus("Show cursor", (action, key) -> {
		MinecraftClient.getInstance().setScreen(new DummyScreen());
		return true;
	});

	public static final ConfigBooleanPlus FAST_MAIN_MENU = new ConfigBooleanPlus("Fast main menu", true);

	public static final ConfigBooleanHotkeyedPlus MULTIPLE_ACTION_BAR = new ConfigBooleanHotkeyedPlus("Allow multiple action bar", true);
	public static final ConfigBooleanHotkeyedPlus NEW_ON_TOP = new ConfigBooleanHotkeyedPlus("Put new action bar messages on top", true);

	public static final ConfigBooleanHotkeyedPlus FULL_BRIGHT = new ConfigBooleanHotkeyedPlus("Full bright", true);
	public static final ConfigDoublePlus FLY_SPEED = new ConfigDoublePlus("Fly speed", 0, 0, Double.MAX_VALUE, false);
	public static final ConfigDoublePlus MULTIPLIER = new ConfigDoublePlus("Fly speed increase/decrease multiplier", 1.5, 0.0, Double.MAX_VALUE, false) {{
		setValueChangeCallback(config -> {

		});
	}};

	// TODO: add an overload for the constructors that automatically returns true
	public static final ConfigHotkeyPlus INCREASE_FLY_SPEED = new ConfigHotkeyPlus("Increase fly speed", (action, key) -> {
		FLY_SPEED.setDoubleValue(FLY_SPEED.getDoubleValue() * MULTIPLIER.getDoubleValue());
		return true;
	});
	public static final ConfigHotkeyPlus DECREASE_FLY_SPEED = new ConfigHotkeyPlus("Decrease fly speed", (action, key) -> {
		FLY_SPEED.setDoubleValue(FLY_SPEED.getDoubleValue() / MULTIPLIER.getDoubleValue());
		return true;
	});
	public static final ConfigHotkeyPlus RESET_FLY_SPEED = new ConfigHotkeyPlus("Reset fly speed", ((action, key) -> {
		FLY_SPEED.resetToDefault();
		return true;
	}));
}
