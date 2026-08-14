package net.kr1v.devutils.mixin;

//? if >=26.1
import com.llamalad7.mixinextras.sugar.Local;
//? if >=1.19 {
import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
//? } else
//import net.minecraft.client.Options;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.kr1v.devutils.config.Main;
//~ if >=26.1 'LightTexture' -> 'LightmapRenderStateExtractor' as _
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;


@Mixin(LightmapRenderStateExtractor.class)
public class FullbrightMixin {
	//? if >=1.19 {
	//? if >=26.1 {
	@Definition(id = "brightnessOption", local = @Local(type = float.class, name = "brightnessOption"))
	@Expression("brightnessOption = @(?)")
	@WrapOperation(method = "extract", at = @At("MIXINEXTRAS:EXPRESSION"))
	//? } else {
	/*@Definition(id = "gamma", method = "Lnet/minecraft/client/Options;gamma()Lnet/minecraft/client/OptionInstance;")
	@Definition(id = "get", method = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;")
	@Definition(id = "Double", type = Double.class)
	@Definition(id = "floatValue", method = "Ljava/lang/Double;floatValue()F")
	@Expression("((Double) ?.gamma().get()).floatValue()")
	@WrapOperation(method = "updateLightmapRenderStateExtractor", at = @At("MIXINEXTRAS:EXPRESSION"))
	*///? }
	private float wrap(Double instance, Operation<Float> original) {
	//? } else {
	/*@WrapOperation(method = "updateLightmapRenderStateExtractor", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Options;gamma:D"))
	private double wrap(Options instance, Operation<Double> original) {
	*///? }
		if (Main.FULLBRIGHT.getBooleanValue()) {
			return 1600.0f;
		}
		return original.call(instance);
	}
}
