package kr1v.utils.mixin.fly_speed;

import kr1v.utils.config.Misc;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerAbilities.class)
public class PlayerAbilitiesMixin {
	@Inject(method = "getFlySpeed", at = @At("HEAD"), cancellable = true)
	private void getFlySpeed(CallbackInfoReturnable<Float> cir) {
		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		if (player != null && player.getAbilities().allowFlying) {
			int active = Misc.ACTIVE_FLY_PRESET.getIntegerValue();
			try {
				float activeSpeed = Float.parseFloat(Misc.FLY_PRESETS.getStrings().get(active));
				cir.setReturnValue(activeSpeed);
			} catch (NumberFormatException ignored) {
			} catch (IndexOutOfBoundsException ignored) {
				Misc.ACTIVE_FLY_PRESET.setIntegerValue(Misc.FLY_PRESETS.getStrings().size()-1);
			}

		}
	}
}
