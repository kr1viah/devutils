package kr1v.utils.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(QuickPlay.class)
public interface QuickPlayAccessor {
    @Invoker("startSingleplayer") static void startSingleplayer(MinecraftClient client, String levelName) {
        throw new AssertionError();
    }
    @Invoker("startMultiplayer") static void startMultiplayer(MinecraftClient client, String levelName) {
        throw new AssertionError();
    }
}
