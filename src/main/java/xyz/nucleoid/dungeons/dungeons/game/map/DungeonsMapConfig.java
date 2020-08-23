package xyz.nucleoid.dungeons.dungeons.game.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.game.map.template.MapTemplate;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;

public class DungeonsMapConfig {
    public static final Codec<DungeonsMapConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockState.CODEC.fieldOf("spawn_block").forGetter(map -> map.spawnBlock)
    ).apply(instance, DungeonsMapConfig::new));

    public final BlockState spawnBlock;

    public DungeonsMapConfig(BlockState spawnBlock) {
        this.spawnBlock = spawnBlock;
    }
}
