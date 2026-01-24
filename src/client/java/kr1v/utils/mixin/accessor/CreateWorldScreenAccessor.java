package kr1v.utils.mixin.accessor;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.CreateWorldCallback;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.CombinedDynamicRegistries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.ServerDynamicRegistryType;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.gen.WorldPreset;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;
import java.util.OptionalLong;

@Mixin(CreateWorldScreen.class)
public interface CreateWorldScreenAccessor {
	@Invoker
	void invokeCreateLevel();

	@Invoker
	boolean invokeStartServer(CombinedDynamicRegistries<ServerDynamicRegistryType> combinedDynamicRegistries, SaveProperties saveProperties);

	@Invoker("<init>")
	static CreateWorldScreen newCreateWorldScreen(
			MinecraftClient client,
			//? if =1.21.5 {
			/*@org.jetbrains.annotations.Nullable Screen parent,
			*///? } else {
			Runnable onClosed,
			//? }
			GeneratorOptionsHolder generatorOptionsHolder,
			Optional<RegistryKey<WorldPreset>> defaultWorldType,
			OptionalLong seed,
			CreateWorldCallback callback) {
		throw new AssertionError();
	}
}
