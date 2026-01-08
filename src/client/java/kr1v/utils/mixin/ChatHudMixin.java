package kr1v.utils.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import kr1v.utils.config.Chat;
import kr1v.utils.interfaces.IMouseReleased;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.util.ChatMessages;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static kr1v.utils.ChatHudManager.selectedText;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin implements IMouseReleased {
	@Shadow
	public abstract int getWidth();

	@Shadow
	protected abstract double toChatLineX(double mouseX);

	@Shadow
	protected abstract double toChatLineY(double mouseY);

	@Shadow
	protected abstract int getMessageLineIndex(double x, double y);

	@Shadow
	@Final
	private MinecraftClient client;


	@Shadow
	@Final
	private List<ChatHudLine.Visible> visibleMessages;

	@Shadow
	public abstract double getChatScale();

	@Unique
	boolean selecting = false;

	@Unique
	int anchorLine = -1;
	@Unique
	int anchorChar = 0;

	@Unique
	int activeLine = -1;
	@Unique
	int activeChar = 0;

	@Unique
	private int normFirstSelectedLine = -1;
	@Unique
	private int normLastSelectedLine = -1;
	@Unique
	private int normSelectCharacterStart = 0;
	@Unique
	private int normSelectCharacterEnd = 0;

	@Unique
	private int mouseClickX = 0;
	@Unique
	private int mouseClickY = 0;


	@WrapMethod(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V")
	private void addTimeStamp(Text message, MessageSignatureData signatureData, MessageIndicator indicator, Operation<Void> original) {
		MutableText mutableMessage = message.copy();
		if (Chat.ADD_HOVER_TIMESTAMP.getBooleanValue()) {
			Instant instant = Instant.now();
			LocalTime time = instant.atZone(ZoneId.systemDefault()).toLocalTime();
			String timeStr;
			try {
				timeStr = time.format(DateTimeFormatter.ofPattern(Chat.TIMESTAMP_FORMAT.getStringValue()));
			} catch (Exception e) {
				mutableMessage.append("\nTimestamp format is incorrect!");
				timeStr = time.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
			}
			mutableMessage.setStyle(mutableMessage.getStyle().withHoverEvent(new HoverEvent.ShowText(Text.literal("Timestamp: \n" + timeStr))));
		}

		original.call(mutableMessage, signatureData, indicator);
	}


	@Inject(method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"))
	private void addMessage(Text message, MessageSignatureData signatureData, MessageIndicator indicator, CallbackInfo ci) {
		ChatHudLine chatHudLine = new ChatHudLine(this.client.inGameHud.getTicks(), message, signatureData, indicator);

		int i = MathHelper.floor((double) this.getWidth() / this.getChatScale());
		MessageIndicator.Icon icon = chatHudLine.getIcon();
		if (icon != null) {
			i -= icon.width + 4 + 2;
		}
		if (normFirstSelectedLine != -1 && normLastSelectedLine != -1) {
			int size = ChatMessages.breakRenderedChatMessageLines(chatHudLine.content(), i, this.client.textRenderer).size();
			normFirstSelectedLine += size;
			normLastSelectedLine += size;
		}
	}

	@Inject(method = "mouseClicked", at = @At("HEAD"))
	private void mouseClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
		mouseClickX = (int) mouseX;
		mouseClickY = (int) mouseY;
		int messageIndex = this.getMessageLineIndex(this.toChatLineX(mouseX), this.toChatLineY(mouseY));
		if (messageIndex < 0) {
			clearSelection();
			return;
		}

		int chatX = (int) this.toChatLineX(mouseX);
		StringBuilder sb = new StringBuilder();
		this.visibleMessages.get(messageIndex).content().accept((index, style, codePoint) -> {
			sb.append((char) codePoint);
			return true;
		});
		String msgStr = sb.toString();
		int charIndex = charIndexAtChatX(msgStr, chatX);

		selecting = true;
		anchorLine = messageIndex;
		anchorChar = charIndex;

		activeLine = anchorLine;
		activeChar = anchorChar;

		normalizeSelection();
	}

	@Unique
	private StringBuilder selectedTextBuilder;

	@Inject(method = "render", at = @At("HEAD"))
	private void onRenderHead(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
		// start fresh per-render
		this.selectedTextBuilder = new StringBuilder();

		// if we lost focus, clear selection (matches the original method)
		if (!focused) {
			clearSelection();
		}
	}


	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0))
	private void wrapLineBackground(DrawContext ctx,
									int x1,
									int y1,
									int x2,
									int y2,
									int color,
									Operation<Void> operation,
									@Local(name = "s") int messageIndex,
									@Local(name = "visible") ChatHudLine.Visible visible,
									@Local(name = "x") int lineTopY,
									@Local(name = "o") int lineHeight,
									@Local(name = "k") int chatWidthScaled,
									@Local(name = "v") int backgroundOpacity
	) {
		try {
			if (this.indexInsideOf(messageIndex)) {
				int startX = -4;
				int normalEndSelectedStart = -4;
				int selectedEndNormalStart;
				int end = chatWidthScaled + 8;

				StringBuilder sb = new StringBuilder();
				visible.content().accept((index, style, codePoint) -> {
					sb.append((char) codePoint);
					return true;
				});
				String full = sb.toString();
				int len = full.length();
				int sStart = Math.clamp(normSelectCharacterStart, 0, len);
				int sEnd = Math.clamp(normSelectCharacterEnd, 0, len);

				String sens = full.substring(0, Math.min(sStart, len));
				String ness = full.substring(0, Math.min(sEnd, len));

				if (isFirst(messageIndex) && isLast(messageIndex)) {
					if (sStart < sEnd) {
						normalEndSelectedStart = this.client.textRenderer.getWidth(sens);
						selectedEndNormalStart = this.client.textRenderer.getWidth(ness);
						this.selectedTextBuilder.append(full, sStart, sEnd).append("\n");
					} else {
						normalEndSelectedStart = this.client.textRenderer.getWidth(ness);
						selectedEndNormalStart = this.client.textRenderer.getWidth(sens);
						this.selectedTextBuilder.append(full, sEnd, sStart).append("\n");
					}
				} else if (isFirst(messageIndex)) {
					selectedEndNormalStart = this.client.textRenderer.getWidth(sens);
					this.selectedTextBuilder.append(full, 0, sStart).append("\n");
				} else if (isLast(messageIndex)) {
					normalEndSelectedStart = this.client.textRenderer.getWidth(ness);
					selectedEndNormalStart = end;
					this.selectedTextBuilder.append(full, sEnd, len).append("\n");
				} else {
					this.selectedTextBuilder.append(full).append("\n");
					selectedEndNormalStart = end;
				}

				int backgroundColourForNormalText = backgroundOpacity << 24;
				int backgroundColourForSelectedText = Chat.CHAT_SELECTED_TEXT_BACKGROUND_COLOUR.getIntegerValue();

				ctx.fill(startX, lineTopY - lineHeight, normalEndSelectedStart, lineTopY, backgroundColourForNormalText);
				ctx.fill(normalEndSelectedStart, lineTopY - lineHeight, selectedEndNormalStart, lineTopY, backgroundColourForSelectedText);
				ctx.fill(selectedEndNormalStart, lineTopY - lineHeight, end, lineTopY, backgroundColourForNormalText);
			} else {
				operation.call(ctx, x1, y1, x2, y2, color);
			}
		} catch (Throwable t) {
			operation.call(ctx, x1, y1, x2, y2, color);
		}
	}

	@WrapOperation(
			method = "render",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/OrderedText;III)I",
					ordinal = 0
			)
	)
	private int wrapDrawText(DrawContext instance, TextRenderer textRenderer, OrderedText text, int x, int y, int color, Operation<Integer> original,
							 @Local(name = "s") int messageIndex,
							 @Local(name = "visible") ChatHudLine.Visible visible,
							 @Local(name = "mouseX") int mouseX,
							 @Local(name = "mouseY") int mouseY
	) {
		try {
			if (this.selecting && messageIndex == getMessageLineIndex(this.toChatLineX(mouseX), this.toChatLineY(mouseY))) {
				StringBuilder sb = new StringBuilder();
				visible.content().accept((index, style, codePoint) -> {
					sb.append((char) codePoint);
					return true;
				});
				String messageString = sb.toString();
				int chatMouseX = (int) this.toChatLineX(mouseX);
				int currentIndex = charIndexAtChatX(messageString, chatMouseX);
				this.activeLine = messageIndex;
				this.activeChar = currentIndex;
				this.normalizeSelection();
			}

			original.call(instance, textRenderer, text, x, y, color);
		} catch (Throwable t) {
			original.call(instance, textRenderer, text, x, y, color);
		}
//        return original.call(instance, textRenderer, text, x, y, color);
		return x;
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void onRenderTail(DrawContext context, int currentTick, int mouseX, int mouseY, boolean focused, CallbackInfo ci) {
		if (this.selectedTextBuilder == null) return;
		if (this.selectedTextBuilder.isEmpty()) return;

		String[] lines = this.selectedTextBuilder.toString().split("\n");
		Collections.reverse(Arrays.asList(lines));
		selectedText = String.join("\n", lines);
	}

	@Unique
	public void utils$mouseReleased(double mouseX, double mouseY) {
		if (mouseClickX == (int) mouseX && mouseClickY == (int) mouseY) {
			clearSelection();
			return;
		}
		int messageIndex = this.getMessageLineIndex(this.toChatLineX(mouseX), this.toChatLineY(mouseY));
		if (messageIndex >= 0) {
			int chatX = (int) this.toChatLineX(mouseX);
			StringBuilder sb = new StringBuilder();
			this.visibleMessages.get(messageIndex).content().accept((index, style, codePoint) -> {
				sb.append((char) codePoint);
				return true;
			});
			String msgStr = sb.toString();
			int charIndex = charIndexAtChatX(msgStr, chatX);

			activeLine = messageIndex;
			activeChar = charIndex;
		}

		selecting = false;
		normalizeSelection();
	}

	@Unique
	private int charIndexAtChatX(String messageString, int chatX) {
		if (chatX <= 0) return 0;
		int curX = 0;
		int i = 0;
		int len = messageString.length();
		while (i < len) {
			curX += client.textRenderer.getWidth(String.valueOf(messageString.charAt(i)));
			if (curX > chatX) return i + 1;
			i++;
		}
		return len;
	}

	@Unique
	private void clearSelection() {
		selecting = false;
		anchorLine = activeLine = -1;
		selectedText = null;
		normalizeSelection();
	}

	@Unique
	private void normalizeSelection() {
		if (anchorLine < 0 || activeLine < 0) {
			normFirstSelectedLine = normLastSelectedLine = -1;
			normSelectCharacterStart = normSelectCharacterEnd = 0;
			return;
		}

		if (anchorLine <= activeLine) {
			normFirstSelectedLine = anchorLine;
			normLastSelectedLine = activeLine;
			normSelectCharacterStart = anchorChar;
			normSelectCharacterEnd = activeChar;
		} else {
			normFirstSelectedLine = activeLine;
			normLastSelectedLine = anchorLine;
			normSelectCharacterStart = activeChar;
			normSelectCharacterEnd = anchorChar;
		}

		StringBuilder sb = new StringBuilder();
		if (normFirstSelectedLine < visibleMessages.size()) {
			visibleMessages.get(normFirstSelectedLine).content().accept((index, style, codePoint) -> {
				sb.append((char) codePoint);
				return true;
			});
			String firstLineText = sb.toString();
			normSelectCharacterStart = Math.clamp(normSelectCharacterStart, 0, firstLineText.length());
		}
		if (normLastSelectedLine >= 0 && normLastSelectedLine < visibleMessages.size()) {
			visibleMessages.get(normLastSelectedLine).content().accept((index, style, codePoint) -> {
				sb.append((char) codePoint);
				return true;
			});
			String lastLineText = sb.toString();
			normSelectCharacterEnd = Math.clamp(normSelectCharacterEnd, 0, lastLineText.length());
		}
	}

	@Unique
	private boolean indexInsideOf(int index) {
		if (normFirstSelectedLine < 0 || normLastSelectedLine < 0) return false;
		return normFirstSelectedLine <= index && index <= normLastSelectedLine;
	}

	@Unique
	private boolean isFirst(int index) {
		return index == normFirstSelectedLine;
	}

	@Unique
	private boolean isLast(int index) {
		return index == normLastSelectedLine;
	}
}
