package xyz.nucleoid.dungeons.dungeons.game.map.gen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DgProcgenMapConfig {
    public static final Codec<DgProcgenMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            DgProcgenGeneratorType.CODEC.fieldOf("generator").forGetter(config -> config.generator)
    ).apply(instance, DgProcgenMapConfig::new));

    public final DgProcgenGeneratorType generator;

    public DgProcgenMapConfig(DgProcgenGeneratorType generator) {
        this.generator = generator;
    }
}
