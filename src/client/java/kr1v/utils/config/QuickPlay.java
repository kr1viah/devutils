package kr1v.utils.config;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import kr1v.malilibApi.annotation.Config;
import kr1v.malilibApi.config.ConfigCycle;
import kr1v.malilibApi.config.EnumBackedCycleConfig;
import kr1v.malilibApi.config.plus.ConfigStringPlus;

@Config(value = "Utils", name = "Quickplay")
public class QuickPlay {
	public static final ConfigBoolean ENABLE_QUICKPLAY = new ConfigBoolean("Enable quickplay", false, "Note:\nThis decides if this mod should affect quickplay in any way, not if it works at all.");
	public static final ConfigCycle<QuickPlayType> QUICK_PLAY_TYPE = new EnumBackedCycleConfig.Builder<>("Type", QuickPlayType.class).displayNameProvider(QuickPlayType::getName).build();
	public static final ConfigStringPlus NAME = new ConfigStringPlus("World/server to join", "", "If left empty, will do nothing. If Type is set to Temporary, will also do nothing");

	public enum QuickPlayType {
		TEMPORARY("Temporary"),
		SINGLEPLAYER("Singleplayer"),
		MULTIPLAYER("Multiplayer"),
		;

		private final String name;

		QuickPlayType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}
}
