package net.kr1v.devutils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kr1v.devutils.config.Main;
//? if >=1.20 {
//~ if >=26.1 'Graphics' -> 'GraphicsExtractor'
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? } else if >=1.16
//import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(
		//? if >=26.2 {
		net.minecraft.client.gui.Gui.class
		//? } else {
		/*net.minecraft.client.renderer.GameRenderer.class
		*///? }
)
public class ToggleScreenMixin {
	//? if >=26.1 {
	@WrapOperation(method =
			/*? if >=26.2 {*/
			"extractRenderState"
			//? } else if >=26.1 {
			/*"extractGui"
			*///?} else {
			/*"render"
			*///? }
			, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V"))
	private void wrap(Screen instance, GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, Operation<Void> original) {
		if (Main.HIDE_SCREENS.getBooleanValue()) return;
		original.call(instance, graphics, mouseX, mouseY, a);
	}
	//? } else if >=1.20 {
	/*//~ if >=1.21.9 'renderWithTooltip' -> 'renderWithTooltipAndSubtitles'
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltip(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
	private void wrap(Screen instance, GuiGraphics guiGraphics, int mouseX, int mouseY, float a, Operation<Void> original) {
		if (Main.HIDE_SCREENS.getBooleanValue()) return;
		original.call(instance, guiGraphics, mouseX, mouseY, a);
	}
	*///? } else if >=1.16 {
	/*//~ if >=1.19.3 'Screen;render' -> 'Screen;renderWithTooltip'
	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V"))
	private void wrap(Screen instance, PoseStack poseStack, int mouseX, int mouseY, float a, Operation<Void> original) {
		if (Main.HIDE_SCREENS.getBooleanValue()) return;
		original.call(instance, poseStack, mouseX, mouseY, a);
	}
	*///? } else {
	/*@WrapOperation(method = "render*", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(IIF)V"))
	private void wrap(Screen instance, int mouseX, int mouseY, float a, Operation<Void> original) {
		if (Main.HIDE_SCREENS.getBooleanValue()) return;
		original.call(instance, mouseX, mouseY, a);
	}
	*///? }
}
