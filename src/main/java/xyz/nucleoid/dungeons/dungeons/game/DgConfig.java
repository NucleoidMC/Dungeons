package xyz.nucleoid.dungeons.dungeons.game;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.game.common.config.PlayerConfig;

public class DgConfig {
    public static final Codec<DgConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
            Codec.INT.fieldOf("time_limit_secs").forGetter(config -> config.timeLimitSecs),
            Identifier.CODEC.fieldOf("map").forGetter(config -> config.map)
    ).apply(instance, DgConfig::new));

    public final PlayerConfig playerConfig;
    public final int timeLimitSecs;
    public final Identifier map;

    public DgConfig(PlayerConfig players, int timeLimitSecs, Identifier map) {
        this.playerConfig = players;
        this.timeLimitSecs = timeLimitSecs;
        this.map = map;
    }
}
