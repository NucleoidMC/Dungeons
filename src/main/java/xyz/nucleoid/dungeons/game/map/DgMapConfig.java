package xyz.nucleoid.dungeons.dungeons.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;

public class DgMapConfig {
    public static final Codec<DgMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("spawn_block").forGetter(map -> map.spawnBlock)
    ).apply(instance, DgMapConfig::new));

    public final BlockState spawnBlock;

    public DgMapConfig(BlockState spawnBlock) {
        this.spawnBlock = spawnBlock;
    }
}
