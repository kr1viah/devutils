package kr1v.utils.config;

import fi.dy.masa.malilib.config.options.ConfigColor;
import fi.dy.masa.malilib.util.data.Color4f;
import kr1v.malilibApi.MalilibApi;
import kr1v.malilibApi.annotation.Config;
import kr1v.malilibApi.config.plus.ConfigBooleanPlus;
import kr1v.malilibApi.config.plus.ConfigHotkeyPlus;
import kr1v.malilibApi.config.plus.ConfigStringPlus;
import kr1v.utils.ChatHudManager;
import net.minecraft.client.MinecraftClient;

@SuppressWarnings("unused")
@Config("Utils")
public class Chat {
	static final ConfigHotkeyPlus HOTKEY = new ConfigHotkeyPlus("Open gui", "G, C", (action, key) -> {
		MalilibApi.openScreenFor("Utils");
		return true;
	});
	public static final ConfigBooleanPlus ADD_HOVER_TIMESTAMP = new ConfigBooleanPlus("Add hover timestamp", true);
	public static final ConfigStringPlus TIMESTAMP_FORMAT = new ConfigStringPlus("Timestamp format", "[HH:mm:ss]");

	public static final ConfigColor CHAT_SELECTED_TEXT_BACKGROUND_COLOUR = new ConfigColor("Selected text background color", Color4f.fromColor(0xFF2222DD));
	static final ConfigHotkeyPlus COPY_HOTKEY = new ConfigHotkeyPlus("Copy selected text", "LEFT_CONTROL, C", ((action, key) -> {
		if (MinecraftClient.getInstance().inGameHud.getChatHud().isChatFocused()) {
			MinecraftClient.getInstance().keyboard.setClipboard(ChatHudManager.selectedText);
		}
		return true;
	}));
}
