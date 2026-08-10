package net.kr1v.devutils.mixin;
/*
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kr1v.devutils.config.Main;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {
	@WrapOperation(method = "extractRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
	private void wrap(Screen instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
		if (Main.HIDE_SCREENS.getBooleanValue()) return;
		original.call(instance, graphics, mouseX, mouseY, a);
	}
}
*/