package kr1v.utils.mixin.fullbright;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import kr1v.utils.config.Misc;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
	@WrapOperation(method = "update", at = @At(value = "INVOKE", target = "Ljava/lang/Double;floatValue()F", ordinal = 1))
	private float wrap(Double instance, Operation<Float> original) {
		if (Misc.FULL_BRIGHT.getBooleanValue()) {
			return (15.f);
		} else {
			return original.call(instance);
		}
	}
}
