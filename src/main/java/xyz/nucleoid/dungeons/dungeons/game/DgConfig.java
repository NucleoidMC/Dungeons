package xyz.nucleoid.dungeons.dungeons.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMapConfig;

public class DgConfig {
    public static final Codec<DgConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
            Codec.INT.fieldOf("time_limit_secs").forGetter(config -> config.timeLimitSecs)
    ).apply(instance, DgConfig::new));

    public final PlayerConfig playerConfig;
    public final int timeLimitSecs;

    public DgConfig(PlayerConfig players, int timeLimitSecs) {
        this.playerConfig = players;
        this.timeLimitSecs = timeLimitSecs;
    }
}
