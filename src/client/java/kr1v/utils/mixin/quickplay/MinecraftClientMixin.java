package kr1v.utils.mixin.quickplay;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import kr1v.utils.config.QuickPlay;
import kr1v.utils.config.TestWorld;
import kr1v.utils.mixin.accessor.QuickPlayAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @WrapOperation(method = "method_53528", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
    private void wrap(MinecraftClient instance, Screen screen, Operation<Void> original) {
        if (!QuickPlay.ENABLE_QUICKPLAY.getBooleanValue()) {
            original.call(instance, screen);
            return;
        }
        QuickPlay.QuickPlayType type = TestWorld.getValue(QuickPlay.QUICK_PLAY_TYPE);
        if (QuickPlay.NAME.getStringValue().isEmpty() && type != QuickPlay.QuickPlayType.TEMPORARY) {
            original.call(instance, screen);
            return;
        }

        MinecraftClient that = (MinecraftClient) (Object) this;
        switch (type) {
            case QuickPlay.QuickPlayType.TEMPORARY -> TestWorld.doWorld();
            case QuickPlay.QuickPlayType.SINGLEPLAYER -> QuickPlayAccessor.startSingleplayer(that, QuickPlay.NAME.getStringValue());
            case QuickPlay.QuickPlayType.MULTIPLAYER -> QuickPlayAccessor.startMultiplayer(that, QuickPlay.NAME.getStringValue());
        }
    }
}
