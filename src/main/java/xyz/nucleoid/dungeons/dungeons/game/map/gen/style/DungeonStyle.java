package xyz.nucleoid.dungeons.dungeons.game.map.gen.style;

import java.util.Random;

import xyz.nucleoid.substrate.biome.BaseBiomeGen;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ChunkRegion;

public interface DungeonStyle extends BaseBiomeGen {
	void placeCeiling(ChunkRegion world, BlockPos pos, Random random);
	void placeWall(ChunkRegion world, BlockPos pos, Random random, Direction direction);
	void placeFloor(ChunkRegion world, BlockPos pos, Random random);
}
