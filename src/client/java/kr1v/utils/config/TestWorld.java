package kr1v.utils.config;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import kr1v.malilibApi.annotation.Config;
import kr1v.malilibApi.annotation.Label;
import kr1v.malilibApi.annotation.PopupConfig;
import kr1v.malilibApi.config.ArrayBackedCycleConfig;
import kr1v.malilibApi.config.ConfigButton;
import kr1v.malilibApi.config.ConfigCycle;
import kr1v.malilibApi.config.EnumBackedCycleConfig;
import kr1v.malilibApi.config.plus.ConfigBooleanPlus;
import kr1v.malilibApi.config.plus.ConfigIntegerPlus;
import kr1v.malilibApi.config.plus.ConfigStringPlus;
import kr1v.utils.UtilsClient;
import kr1v.utils.mixin.accessor.CreateWorldScreenAccessor;
import kr1v.utils.mixin.accessor.ScreenAccessor;
import kr1v.utils.util.StringUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.*;
import net.minecraft.client.world.GeneratorOptionsFactory;
import net.minecraft.client.world.GeneratorOptionsHolder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.VanillaDataPackProvider;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.server.SaveLoading;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.GeneratorOptions;
import net.minecraft.world.gen.WorldPreset;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.level.WorldGenSettings;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.LockSupport;
import java.util.function.Function;

import static net.minecraft.world.gen.WorldPresets.*;

@SuppressWarnings("unused")
@Config(value = UtilsClient.MOD_ID, name = "Test World")
public class TestWorld {
	public static final ConfigBooleanPlus ADD_TEST_WORLD_BUTTON = new ConfigBooleanPlus("Add test button to main menu", true);
	public static final ConfigBoolean PERSIST = new ConfigBoolean("Persist world", false);
	@Label("Game")
	public static final ConfigCycle<WorldCreator.Mode> GAME_MODE = new EnumBackedCycleConfig.Builder<>("Game Mode", WorldCreator.Mode.class).displayNameProvider(value -> value.name.getString()).build();
	public static final ConfigCycle<Difficulty> DIFFICULTY = new EnumBackedCycleConfig.Builder<>("Difficulty", Difficulty.class).displayNameProvider(value -> StringUtils.splitSnakeCase(value.name())).build();
	public static final ConfigBoolean ALLOW_COMMANDS = new ConfigBoolean("Allow Commands", true);
	@Label("World")
	public static final ConfigStringPlus SEED = new ConfigStringPlus("Seed");
	public static final ConfigBoolean GENERATE_STRUCTURES = new ConfigBoolean("Generate Structures", true);
	public static final ConfigCycle<RegistryKey<WorldPreset>> WORLD_PRESET = new ArrayBackedCycleConfig.Builder<>("World Preset", DEFAULT, FLAT, LARGE_BIOMES, AMPLIFIED, SINGLE_BIOME_SURFACE, DEBUG_ALL_BLOCK_STATES)
			.displayNameProvider(worldPresetRegistryKey -> {
				if (worldPresetRegistryKey == DEFAULT) return "Normal";
				else if (worldPresetRegistryKey == FLAT) return "Flat";
				else if (worldPresetRegistryKey == LARGE_BIOMES) return "Large biomes";
				else if (worldPresetRegistryKey == AMPLIFIED) return "Amplified";
				else if (worldPresetRegistryKey == SINGLE_BIOME_SURFACE) return "Single biome";
				else if (worldPresetRegistryKey == DEBUG_ALL_BLOCK_STATES) return "Debug world";
				else return "Unknown";
			}).build();

	@Label("Init")
	public static final ConfigBoolean TELEPORT_TO_POS = new ConfigBoolean("Teleport to a specific position", false);

	@PopupConfig
	public static class Position {
		public static final ConfigIntegerPlus x = new ConfigIntegerPlus("x");
		public static final ConfigIntegerPlus y = new ConfigIntegerPlus("y");
		public static final ConfigIntegerPlus z = new ConfigIntegerPlus("z");
	}

	public static GameRules defaultGameRules;

	public static final ConfigButton EDIT_GAME_RULES = new ConfigButton("", "Edit Game Rules", () -> {
		MinecraftClient mc = MinecraftClient.getInstance();
		Screen parent = mc.currentScreen;
		mc.setScreen(new EditGameRulesScreen(defaultGameRules, gameRulesOptional -> {
			mc.setScreen(parent);
			gameRulesOptional.ifPresent(gameRules -> defaultGameRules = gameRules);
		}));
	});

	public static final MinecraftClient client = MinecraftClient.getInstance();
	public static final String TEMP_WORLD_NAME = "Temporary Test World";

	public static void doWorld() {
		LevelStorage levelStorage = client.getLevelStorage();

		// if persist and level doesn't exist, create the level
		// if not persist, also create the level
		// if not persist, and it exists, delete existing
		if (PERSIST.getBooleanValue() && levelStorage.levelExists(TEMP_WORLD_NAME)) {
			Screen currentScreen = client.currentScreen;
			client.createIntegratedServerLoader().start(TEMP_WORLD_NAME, () -> client.setScreen(currentScreen));
		} else {
			if (levelStorage.levelExists(TEMP_WORLD_NAME)) {
				Path tempWorldPath = levelStorage.resolve(TEMP_WORLD_NAME);
				LevelStorage.Session session = null;
				try {
					session = levelStorage.createSessionWithoutSymlinkCheck(TEMP_WORLD_NAME);
					session.deleteSessionLock();
				} catch (IOException e) {
					String couldNotDeleteMessage = "Couldn't delete temporary world! Loading it instead.";
					UtilsClient.LOGGER.warn(couldNotDeleteMessage, e);
					client.inGameHud.getChatHud().addMessage(Text.of(couldNotDeleteMessage));

					Screen currentScreen = client.currentScreen;
					client.createIntegratedServerLoader().start(TEMP_WORLD_NAME, () -> client.setScreen(currentScreen));

					return;
				} finally {
					if (session != null) {
						session.tryClose();
					}
				}
			}
			CreateWorldCallback callback = (screen, combinedDynamicRegistries, levelProperties, dataPackTempDir) -> ((CreateWorldScreenAccessor) screen).invokeStartServer(combinedDynamicRegistries, levelProperties);

			GeneratorOptionsFactory generatorOptionsFactory = (dataPackContents, dynamicRegistries, settings) -> new GeneratorOptionsHolder(
					settings.worldGenSettings(), dynamicRegistries, dataPackContents, settings.dataConfiguration()
			);
			Function<SaveLoading.LoadContextSupplierContext, WorldGenSettings> settingsSupplier = context -> new WorldGenSettings(
					GeneratorOptions.createRandom(), WorldPresets.createDemoOptions(context.worldGenRegistryManager())
			);

			SaveLoading.ServerConfig serverConfig = getServerConfig();
			CompletableFuture<GeneratorOptionsHolder> completableFuture = SaveLoading.load(
					serverConfig,
					context -> new SaveLoading.LoadContext<>(
							new WorldCreationSettings(settingsSupplier.apply(context), context.dataConfiguration()), context.dimensionsRegistryManager()
					),
					(resourceManager, dataPackContents, dynamicRegistries, settings) -> {
						resourceManager.close();
						return generatorOptionsFactory.apply(dataPackContents, dynamicRegistries, settings);
					},
					Util.getMainWorkerExecutor(),
					client
			);
			client.runTasks(completableFuture::isDone);

			CreateWorldScreen createWorldScreen = CreateWorldScreenAccessor.newCreateWorldScreen(
					client,
					null,
					completableFuture.join(),
					Optional.of(WORLD_PRESET.getValue()),
					OptionalLong.empty(),
					callback
			);
			CreateWorldScreenAccessor accessor = (CreateWorldScreenAccessor) createWorldScreen;
			ScreenAccessor screenAccessor = (ScreenAccessor) createWorldScreen;

			WorldCreator worldCreator = createWorldScreen.getWorldCreator();
			worldCreator.setWorldName(TEMP_WORLD_NAME);
			worldCreator.setDifficulty(DIFFICULTY.getValue());
			worldCreator.setGameMode(GAME_MODE.getValue());
			worldCreator.setCheatsEnabled(ALLOW_COMMANDS.getBooleanValue());
			worldCreator.setBonusChestEnabled(false);
			worldCreator.setGameRules(defaultGameRules);
			worldCreator.setGenerateStructures(GENERATE_STRUCTURES.getBooleanValue());
			worldCreator.setSeed(SEED.getStringValue());

			screenAccessor.setClient(client);
			accessor.invokeCreateLevel();

			if (TELEPORT_TO_POS.getBooleanValue()) {
				new Thread(() -> {
					while (MinecraftClient.getInstance().player == null) {
						LockSupport.parkNanos(50_000_000);
					}
					LockSupport.parkNanos(50_000_000);
					MinecraftClient.getInstance().getNetworkHandler().sendChatCommand("tp @s " + Position.x.getIntegerValue() + " " + Position.y.getIntegerValue() + " " + Position.z.getIntegerValue());
				}).start();
			}
		}
	}

	private static SaveLoading.@NotNull ServerConfig getServerConfig() {
		ResourcePackManager resourcePackManager = new ResourcePackManager(new VanillaDataPackProvider(client.getSymlinkFinder()));
		DataConfiguration dataConfiguration = new DataConfiguration(new DataPackSettings(List.of("vanilla", "tests"), List.of()), FeatureFlags.FEATURE_MANAGER.getFeatureSet());
		SaveLoading.DataPacks dataPacks = new SaveLoading.DataPacks(resourcePackManager, dataConfiguration, false, true);
		return new SaveLoading.ServerConfig(dataPacks, CommandManager.RegistrationEnvironment.INTEGRATED, 2);
	}
}

