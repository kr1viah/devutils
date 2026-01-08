package kr1v.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import kr1v.malilibApi.ConfigHandler;
import kr1v.malilibApi.InputHandler;
import kr1v.malilibApi.MalilibApi;
import kr1v.utils.config.TestWorld;
import kr1v.utils.interfaces.IMouseReleased;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;

import java.util.Map;

public class UtilsClient implements ClientModInitializer {
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final String MOD_ID = "Utils";

	@Override
	public void onInitializeClient() {
		MalilibApi.registerMod(
				MOD_ID,
				MOD_ID,
				new ConfigHandler(MOD_ID) {
					@Override
					public void loadAdditionalData(JsonObject root) {
						if (root.has("gameRules") && root.get("gameRules").isJsonObject()) {
							GameRules gameRules = new GameRules(FeatureFlags.FEATURE_MANAGER.getFeatureSet());
							Map<String, JsonElement> map = root.getAsJsonObject("gameRules").asMap();
							gameRules.accept(new GameRules.Visitor() {
								@Override
								public void visitInt(GameRules.Key<GameRules.IntRule> key, GameRules.Type<GameRules.IntRule> type) {
									String keyStr = "I/" + key.getCategory() + "/" + key.getTranslationKey();
									if (map.containsKey(keyStr)) {
										var val = map.get(keyStr);
										if (val.isJsonPrimitive() && val.getAsJsonPrimitive().isNumber()) {
											int integer = val.getAsInt();
											gameRules.get(key).set(integer, null);
										}
									}
								}

								@Override
								public void visitBoolean(GameRules.Key<GameRules.BooleanRule> key, GameRules.Type<GameRules.BooleanRule> type) {
									String keyStr = "B/" + key.getCategory() + "/" + key.getTranslationKey();
									if (map.containsKey(keyStr)) {
										var val = map.get(keyStr);
										if (val.isJsonPrimitive() && val.getAsJsonPrimitive().isBoolean()) {
											boolean bool = val.getAsBoolean();
											gameRules.get(key).set(bool, null);
										}
									}
								}
							});
							TestWorld.defaultGameRules = gameRules;
						} else {
							TestWorld.defaultGameRules = new GameRules(FeatureFlags.FEATURE_MANAGER.getFeatureSet());
						}
					}

					@Override
					public void saveAdditionalData(JsonObject root) {
						JsonObject gameRules = new JsonObject();
						GameRules rules = TestWorld.defaultGameRules;
						rules.accept(new GameRules.Visitor() {
							@Override
							public void visitBoolean(GameRules.Key<GameRules.BooleanRule> key, GameRules.Type<GameRules.BooleanRule> type) {
								gameRules.addProperty("B/" + key.getCategory() + "/" + key.getTranslationKey(), rules.getBoolean(key));
							}

							@Override
							public void visitInt(GameRules.Key<GameRules.IntRule> key, GameRules.Type<GameRules.IntRule> type) {
								gameRules.addProperty("I/" + key.getCategory() + "/" + key.getTranslationKey(), rules.getInt(key));
							}
						});
						root.add("gameRules", gameRules);
					}
				},
				new InputHandler(MOD_ID) {
					@Override
					public boolean onMouseClick(int mouseX, int mouseY, int eventButton, boolean eventButtonState) {
						if (eventButton == 0 && !eventButtonState) {
							((IMouseReleased) MinecraftClient.getInstance().inGameHud.getChatHud()).utils$mouseReleased(mouseX, mouseY);
						}
						return super.onMouseClick(mouseX, mouseY, eventButton, eventButtonState);
					}
				}
		);
	}
}
