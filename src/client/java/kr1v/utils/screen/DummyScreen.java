package kr1v.utils.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Style;

public class DummyScreen extends ChatScreen {
	private final MinecraftClient client;

	public DummyScreen() {
		super("");
		this.client = MinecraftClient.getInstance();
		if (this.client.player == null) this.client.setScreen(null);
	}

	@Override
	public void init() {
		if (this.client.player == null) return;
		super.init();
	}

	@Override
	protected void addScreenNarrations(NarrationMessageBuilder messageBuilder) {
		if (this.client.player == null) return;
		super.addScreenNarrations(messageBuilder);
	}

	@Override
	protected void setInitialFocus() {
		if (this.client.player == null) return;
		super.setInitialFocus();
	}

	int scaledWindowWidth = -1;
	int scaledWindowHeight = -1;

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
		this.client.inGameHud.getChatHud().render(context, this.client.inGameHud.getTicks(), mouseX, mouseY, true);
		if (this.client.player == null) return;
		scaledWindowWidth = context.getScaledWindowWidth();
		scaledWindowHeight = context.getScaledWindowHeight();

		int half = scaledWindowWidth / 2;
		for (int hotbarItem = 0; hotbarItem < 9; hotbarItem++) {
			int x = half - 90 + hotbarItem * 20 + 2;
			int y = scaledWindowHeight - 16 - 3;
			if (isPosInsideItem(mouseX, mouseY+1, x, y)) {
				ItemStack stack = this.client.player.getInventory().getStack(hotbarItem);
				if (stack.getItem() != Items.AIR) {
					context.drawTooltip(this.textRenderer, getTooltipFromItem(this.client, stack), stack.getTooltipData(), mouseX, mouseY, stack.get(DataComponentTypes.TOOLTIP_STYLE));
				}
			}
		}
	}

	private boolean isPosInsideItem(int mouseX, int mouseY, int itemX, int itemY) {
		return mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			ChatHud chatHud = this.client.inGameHud.getChatHud();
			if (chatHud.mouseClicked(mouseX, mouseY)) {
				return true;
			}

			Style style = this.client.inGameHud.getChatHud().getTextStyleAt(mouseX, mouseY);
			if (style != null && this.handleTextClick(style)) {
				return true;
			}

			if (this.client.player != null) {
				int half = scaledWindowWidth / 2;
				for (int hotbarItem = 0; hotbarItem < 9; hotbarItem++) {
					int x = half - 90 + hotbarItem * 20 + 2;
					int y = scaledWindowHeight - 16 - 3;
					if (isPosInsideItem((int) mouseX, (int) (mouseY+1), x, y)) {
							this.client.player.getInventory().setSelectedSlot(hotbarItem);
							return true;
						}
				}
			}
		}
		this.client.setScreen(null);
		return true;
	}
}
