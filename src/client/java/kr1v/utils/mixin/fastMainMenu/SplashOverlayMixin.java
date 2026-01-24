package kr1v.utils.mixin.fastMainMenu;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import kr1v.utils.config.Misc;
import kr1v.utils.mixin.accessor.Matrix4fStackAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SplashOverlay.class)
public class SplashOverlayMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	//? if =1.21.5 {
	/*@Definition(id = "context", local = @Local(type = DrawContext.class, argsOnly = true))
	@Definition(id = "fill", method = "Lnet/minecraft/client/gui/DrawContext;fill(Lnet/minecraft/client/render/RenderLayer;IIIII)V")
	@Expression("context.fill(?, ?, ?, ?, ?)")
	@WrapWithCondition(method = "render", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean preventFill(DrawContext instance, net.minecraft.client.render.RenderLayer layer, int x1, int y1, int x2, int y2, int color) {
		return !Misc.FAST_MAIN_MENU.getBooleanValue();
	}

	@Definition(id = "context", local = @Local(type = DrawContext.class, argsOnly = true))
	@Definition(id = "drawTexture", method = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V")
	@Expression("context.drawTexture(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
	@WrapWithCondition(method = "render", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean preventFill(DrawContext instance, java.util.function.Function<Identifier, net.minecraft.client.render.RenderLayer> renderLayers, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color) {
		return !Misc.FAST_MAIN_MENU.getBooleanValue();
	}
	*///? } else {
	@Definition(id = "context", local = @Local(type = DrawContext.class, argsOnly = true))
	@Definition(id = "fill", method = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V")
	@Expression("context.fill(?, ?, ?, ?, ?)")
	@WrapWithCondition(method = "render", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean preventFill(DrawContext instance, int x1, int y1, int x2, int y2, int color) {
		return !Misc.FAST_MAIN_MENU.getBooleanValue();
	}

	@Definition(id = "context", local = @Local(type = DrawContext.class, argsOnly = true))
	@Definition(id = "drawTexture", method = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V")
	@Expression("context.drawTexture(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")
	@WrapWithCondition(method = "render", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean preventFill(DrawContext instance, RenderPipeline renderPipeline, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color) {
		return !Misc.FAST_MAIN_MENU.getBooleanValue();
	}
	//? }

	@WrapOperation(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearColorTexture(Lcom/mojang/blaze3d/textures/GpuTexture;I)V"))
	private void prevent(CommandEncoder instance, GpuTexture gpuTexture, int i, Operation<Void> original, @Local(argsOnly = true) DrawContext drawContext, @Local(argsOnly = true, ordinal = 0) int mouseX, @Local(argsOnly = true, ordinal = 1) int mouseY, @Local(argsOnly = true, ordinal = 0) float deltaTicks) {
		if (Misc.FAST_MAIN_MENU.getBooleanValue()) {
			var viewStack = RenderSystem.getModelViewStack();
			var accessor = (Matrix4fStackAccessor) viewStack;
			int curr = accessor.getCurr();
			try {
				assert this.client.currentScreen != null;
				this.client.currentScreen.render(drawContext, mouseX, mouseY, deltaTicks);
			} catch (Throwable ignored) {
				while (accessor.getCurr() > curr) viewStack.popMatrix();
			}
		} else {
			original.call(instance, gpuTexture, i);
		}
	}

	@WrapMethod(method = "renderProgressBar")
	private void preventProgressBarRendering(DrawContext context, int minX, int minY, int maxX, int maxY, float opacity, Operation<Void> original) {
		if (!Misc.FAST_MAIN_MENU.getBooleanValue()) {
			original.call(context, minX, minY, maxX, maxY, opacity);
		}
	}

	@Inject(method = "pausesGame", at = @At("RETURN"), cancellable = true)
	private void doesntPauseGame(CallbackInfoReturnable<Boolean> cir) {
		if (Misc.FAST_MAIN_MENU.getBooleanValue()) {
			cir.setReturnValue(false);
		}
	}
}
