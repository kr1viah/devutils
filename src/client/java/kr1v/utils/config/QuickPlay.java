package kr1v.utils.config;

import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigOptionList;
import kr1v.malilibApi.annotation.Config;
import kr1v.malilibApi.config.plus.ConfigStringPlus;
import kr1v.utils.util.EnumOption;

@Config(value = "Utils", name = "Quickplay")
public class QuickPlay {
    public static final ConfigBoolean       ENABLE_QUICKPLAY = new ConfigBoolean("Enable quickplay", false, "Note:\nThis decides if this mod should affect quickplay in any way, not if it works at all.");
    public static final ConfigOptionList    QUICK_PLAY_TYPE = new ConfigOptionList("Type", new EnumOption<>(QuickPlayType.class, QuickPlayType.TEMPORARY, QuickPlayType::getName));
    public static final ConfigStringPlus    NAME = new ConfigStringPlus("World/server to join", "", "If left empty, will do nothing. If Type is set to Temporary, will also do nothing");

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
