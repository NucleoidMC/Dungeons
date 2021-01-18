package xyz.nucleoid.dungeons.dungeons.game;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import xyz.nucleoid.dungeons.dungeons.game.map.gen.DgProcgenMapConfig;
import xyz.nucleoid.plasmid.game.config.PlayerConfig;

public class DgConfig {
    public static final Codec<DgConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PlayerConfig.CODEC.fieldOf("players").forGetter(config -> config.playerConfig),
            Codec.INT.fieldOf("time_limit_secs").forGetter(config -> config.timeLimitSecs),
            Codec.either(Identifier.CODEC, DgProcgenMapConfig.CODEC).fieldOf("map").forGetter(config -> config.map)
    ).apply(instance, DgConfig::new));

    public final PlayerConfig playerConfig;
    public final int timeLimitSecs;
    public final Either<Identifier, DgProcgenMapConfig> map;

    public DgConfig(PlayerConfig players, int timeLimitSecs, Either<Identifier, DgProcgenMapConfig> map) {
        this.playerConfig = players;
        this.timeLimitSecs = timeLimitSecs;
        this.map = map;
    }
}
