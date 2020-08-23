package xyz.nucleoid.dungeons.dungeons.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.dungeons.dungeons.game.map.DungeonsMapConfig;

public class DungeonsConfig {
    public static final Codec<DungeonsConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
            DungeonsMapConfig.CODEC.fieldOf("map").forGetter(config -> config.mapConfig),
            Codec.INT.fieldOf("time_limit_secs").forGetter(config -> config.timeLimitSecs)
    ).apply(instance, DungeonsConfig::new));

    public final PlayerConfig playerConfig;
    public final DungeonsMapConfig mapConfig;
    public final int timeLimitSecs;

    public DungeonsConfig(PlayerConfig players, DungeonsMapConfig mapConfig, int timeLimitSecs) {
        this.playerConfig = players;
        this.mapConfig = mapConfig;
        this.timeLimitSecs = timeLimitSecs;
    }
}
