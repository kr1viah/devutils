package kr1v.utils.config;

import kr1v.malilibApi.annotation.Config;
import kr1v.malilibApi.config.plus.ConfigBooleanPlus;
import kr1v.malilibApi.config.plus.ConfigHotkeyPlus;
import kr1v.utils.screen.DummyScreen;
import net.minecraft.client.MinecraftClient;

@SuppressWarnings("ALL")
@Config("Utils")
public class Misc {
    static final ConfigHotkeyPlus SHOW_CURSOR = new ConfigHotkeyPlus("Show cursor", (action, key) -> {
        MinecraftClient.getInstance().setScreen(new DummyScreen());
        return true;
    });

    public static final ConfigBooleanPlus       FAST_MAIN_MENU = new ConfigBooleanPlus("Fast main menu", true);

    public static final ConfigBooleanPlus       MULTIPLE_ACTION_BAR = new ConfigBooleanPlus("Allow multiple action bar", true);
    public static final ConfigBooleanPlus       NEW_ON_TOP = new ConfigBooleanPlus("Put new action bar messages on top", true);
}
