package kr1v.utils.mixin.fastMainMenu;

import kr1v.utils.config.Misc;
import net.minecraft.client.gui.LogoDrawer;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Shadow
    private boolean doBackgroundFade;

    @Inject(method = "<init>(ZLnet/minecraft/client/gui/LogoDrawer;)V", at = @At("RETURN"))
    private void doNotBackgroundFade(boolean doBackgroundFade, LogoDrawer logoDrawer, CallbackInfo ci) {
        if (Misc.FAST_MAIN_MENU.getBooleanValue()) {
            this.doBackgroundFade = false;
        }
    }
}
