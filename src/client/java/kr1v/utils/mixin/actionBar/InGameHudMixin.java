package kr1v.utils.mixin.actionBar;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import kr1v.utils.ActionBarMessage;
import kr1v.utils.config.Misc;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.profiler.Profilers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Unique
    List<ActionBarMessage> actionBarMessages = new ArrayList<>();

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void decrementRemaining(CallbackInfo ci) {
        ActionBarMessage toRemove = null;
        for (ActionBarMessage actionBarMessage : actionBarMessages) {
            actionBarMessage.timeRemaining--;
            if (actionBarMessage.timeRemaining < 0) toRemove = actionBarMessage;
        }
        actionBarMessages.remove(toRemove);
    }

    @WrapMethod(method = "setOverlayMessage")
    private void redirectOverlayMessage(Text text, boolean tinted, Operation<Void> original) {
        if (!Misc.MULTIPLE_ACTION_BAR.getBooleanValue()) {
            original.call(text, tinted);
        } else {
            ActionBarMessage newMessage = new ActionBarMessage(text, tinted, 60);
            if (!actionBarMessages.contains(newMessage)) {
                if (Misc.NEW_ON_TOP.getBooleanValue()) {
                    actionBarMessages.addFirst(newMessage);
                } else {
                    actionBarMessages.add(newMessage);
                }
            }
        }
    }

    @Inject(method = "renderOverlayMessage", at = @At("HEAD"))
    private void renderClientOverlay(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (actionBarMessages.isEmpty()) {
           return;
        }
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        Profilers.get().push("multipleOverlayMessage");
        int count = 0;
        for (ActionBarMessage actionBarMessage : actionBarMessages.reversed()) {
            if (actionBarMessage.timeRemaining > 0) {
                float f = actionBarMessage.timeRemaining - tickCounter.getTickProgress(false);
                int alpha = (int) ((f * 255.0F) / 20.0F);
                if (alpha > 255) {
                    alpha = 255;
                }

                if (alpha > 8) {
                    context.getMatrices().push();
                    context.getMatrices().translate((float)(context.getScaledWindowWidth() / 2), (float)(context.getScaledWindowHeight() - 68) - 9 * count, 0.0F);
                    int j;
                    if (actionBarMessage.isTinted()) {
                        j = MathHelper.hsvToArgb(f / 50.0F, 0.7F, 0.6F, alpha);
                    } else {
                        j = ColorHelper.withAlpha(alpha, Colors.WHITE);
                    }

                    int width = textRenderer.getWidth(actionBarMessage.getText());
                    context.drawTextWithBackground(textRenderer, actionBarMessage.getText(), -width / 2, 12, width, j);
                    context.getMatrices().pop();
                    count++;
                }
            }
        }
        Profilers.get().pop();
    }
}
