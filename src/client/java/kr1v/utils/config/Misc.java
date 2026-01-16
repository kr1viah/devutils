package kr1v.utils.config;

import com.google.common.collect.ImmutableList;
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
	public static final ConfigStringListPlus FLY_PRESETS = new ConfigStringListPlus("Fly presets", ImmutableList.of("0.05"));
	public static final ConfigIntegerPlus ACTIVE_FLY_PRESET = new ConfigIntegerPlus("Active fly preset", 0);
	public static final ConfigDoublePlus MULTIPLIER = new ConfigDoublePlus("Fly speed increase/decrease multiplier", 1.5, 0.0, Double.MAX_VALUE, false);

	public static final ConfigHotkeyPlus NEXT_PRESET = new ConfigHotkeyPlus("Next fly preset", "BUTTON_5", (action, key) -> {
		int active = ACTIVE_FLY_PRESET.getIntegerValue();
		active++;
		if (active >= FLY_PRESETS.getStrings().size()) active = 0;
		ACTIVE_FLY_PRESET.setIntegerValue(active);
		return true;
	});

	public static final ConfigHotkeyPlus PREV_PRESET = new ConfigHotkeyPlus("Previous fly preset", "BUTTON_4", (action, key) -> {
		int active = ACTIVE_FLY_PRESET.getIntegerValue();
		active--;
		if (active < 0) active = FLY_PRESETS.getStrings().size() - 1;
		ACTIVE_FLY_PRESET.setIntegerValue(active);
		return true;
	});

	// TODO: add an overload for the constructors that automatically returns true
	public static final ConfigHotkeyPlus INCREASE_FLY_SPEED = new ConfigHotkeyPlus("Increase active presets' fly speed", (action, key) -> {
		int active = ACTIVE_FLY_PRESET.getIntegerValue();
		double activeFlySpeed = Double.parseDouble(FLY_PRESETS.getStrings().get(active));
		activeFlySpeed *= MULTIPLIER.getDoubleValue();
		FLY_PRESETS.getStrings().set(active, "" + activeFlySpeed);
		return true;
	});
	public static final ConfigHotkeyPlus DECREASE_FLY_SPEED = new ConfigHotkeyPlus("Decrease active presets' fly speed", (action, key) -> {
		int active = ACTIVE_FLY_PRESET.getIntegerValue();
		double activeFlySpeed = Double.parseDouble(FLY_PRESETS.getStrings().get(active));
		activeFlySpeed /= MULTIPLIER.getDoubleValue();
		FLY_PRESETS.getStrings().set(active, "" + activeFlySpeed);
		return true;
	});
}
