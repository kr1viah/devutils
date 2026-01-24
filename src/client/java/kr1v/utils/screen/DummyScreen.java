package kr1v.utils.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;

public class DummyScreen extends ChatScreen {
	private final MinecraftClient client;

	public DummyScreen() {
		super(""/*? if =1.21.11 {*/, false/*? }*/);
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
		//? if =1.21.5
		//this.client.inGameHud.getChatHud().render(context, this.client.inGameHud.getTicks(), mouseX, mouseY, true);
		//? if =1.21.11
		this.client.inGameHud.getChatHud().render(context, client.textRenderer, this.client.inGameHud.getTicks(), mouseX, mouseY, true, true);
		if (this.client.player == null) return;
		scaledWindowWidth = context.getScaledWindowWidth();
		scaledWindowHeight = context.getScaledWindowHeight();

		for (int hotbarItem = 0; hotbarItem < 9; hotbarItem++) {
			if (isPosInsideItemRender(mouseX, mouseY+1, hotbarItem)) {
				ItemStack stack = this.client.player.getInventory().getStack(hotbarItem);
				if (stack.getItem() != Items.AIR) {
					context.drawTooltip(this.textRenderer, getTooltipFromItem(this.client, stack), stack.getTooltipData(), mouseX, mouseY, stack.get(DataComponentTypes.TOOLTIP_STYLE));
				}
			}
		}
		int offhandIndex = client.player.getMainArm().getOpposite() == Arm.LEFT ? -1 : 10;
		if (isPosInsideItemRender(mouseX, mouseY+1, offhandIndex)) {
			ItemStack stack = this.client.player.getInventory().getStack(40); // offhand
			if (stack.getItem() != Items.AIR) {
				context.drawTooltip(this.textRenderer, getTooltipFromItem(this.client, stack), stack.getTooltipData(), mouseX, mouseY, stack.get(DataComponentTypes.TOOLTIP_STYLE));
			}
		}
	}

	//? if =1.21.11 {
	@Override
	public boolean mouseClicked(net.minecraft.client.gui.Click click, boolean doubled) {
		return this.mouseClicked(click.x(), click.y(), click.button());
	}
	//? }

	//? if =1.21.5
	//@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (button == 0) {
			ChatHud chatHud = this.client.inGameHud.getChatHud();
			//? if =1.21.5 {
			/*if (chatHud.mouseClicked(mouseX, mouseY)) {
				return true;
			}
			net.minecraft.text.Style style = this.client.inGameHud.getChatHud().getTextStyleAt(mouseX, mouseY);
			if (style != null && this.handleTextClick(style)) {
				return true;
			}
			*///? }

			if (this.client.player != null) {
				for (int hotbarItem = 0; hotbarItem < 9; hotbarItem++) {
					if (isPosInsideItemClick((int) mouseX, (int) (mouseY+1), hotbarItem)) {
						this.client.player.getInventory().setSelectedSlot(hotbarItem);
						return true;
					}
				}
			}
		}
		this.client.setScreen(null);
		return true;
	}

	private boolean isPosInsideItemClick(int mouseX, int mouseY, int hotbarItem) {
		int half = scaledWindowWidth / 2;
		int x = half - 90 + hotbarItem * 20 + 2 - 2; // move to the left by 2;
		int y = scaledWindowHeight - 16 - 3 - 2; // lower by 2
		int width = 20; // + 2 on both sides
		int height = 20; // + 2 on both sides

		return mouseX >= x
				&& mouseX < x + width
				&& mouseY >= y
				&& mouseY < y + height;
	}

	private boolean isPosInsideItemRender(int mouseX, int mouseY, int hotbarItem) { // -1 for left offhand, 10 for right offhand
		int half = scaledWindowWidth / 2;
		int x = half - 90 + hotbarItem * 20 + 2;
		int y = scaledWindowHeight - 16 - 3;
		int width = 16;
		int height = 16;

		if (hotbarItem == -1) {
			x = half - 117;
		} else if (hotbarItem == 10) {
			x = half + 102;
		}

		return mouseX >= x
				&& mouseX < x + width
				&& mouseY >= y
				&& mouseY < y + height;
	}
}
