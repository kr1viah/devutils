package kr1v.utils.config;

import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.util.data.Color4f;
import kr1v.malilibApi.annotation.Config;
import kr1v.malilibApi.config.plus.ConfigBooleanHotkeyedPlus;
import kr1v.malilibApi.config.plus.ConfigHotkeyPlus;
import kr1v.malilibApi.config.plus.ConfigStringPlus;
import kr1v.utils.ChatHudManager;
import kr1v.utils.UtilsClient;
import net.minecraft.client.MinecraftClient;

@SuppressWarnings("unused")
@Config(UtilsClient.MOD_ID)
public class Chat {
	public static final ConfigBooleanHotkeyedPlus ADD_HOVER_TIMESTAMP = new ConfigBooleanHotkeyedPlus("Add hover timestamp", true);
	public static final ConfigStringPlus TIMESTAMP_FORMAT = new ConfigStringPlus("Timestamp format", "[HH:mm:ss]");

	public static final ConfigColor CHAT_SELECTED_TEXT_BACKGROUND_COLOUR = new ConfigColor("Selected text background color", Color4f.fromColor(0xFF2222DD));
	static final ConfigHotkeyPlus COPY_HOTKEY = new ConfigHotkeyPlus("Copy selected text", "LEFT_CONTROL, C", ((action, key) -> {
		if (MinecraftClient.getInstance().inGameHud.getChatHud().isChatFocused()) {
			MinecraftClient.getInstance().keyboard.setClipboard(ChatHudManager.selectedText);
		}
		return true;
	}));
}
