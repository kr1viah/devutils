package net.kr1v.devutils.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kr1v.devutils.config.Main;
//~ if >=26.1 'Graphics' -> 'GraphicsExtractor'
import net.minecraft.client.gui.GuiGraphicsExtractor;
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
	//? } else {
	/*@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;renderWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
	private void wrap(Screen instance, GuiGraphics guiGraphics, int mouseX, int mouseY, float a, Operation<Void> original) {
		if (Main.HIDE_SCREENS.getBooleanValue()) return;
		original.call(instance, guiGraphics, mouseX, mouseY, a);
	}
	*///? }
}
