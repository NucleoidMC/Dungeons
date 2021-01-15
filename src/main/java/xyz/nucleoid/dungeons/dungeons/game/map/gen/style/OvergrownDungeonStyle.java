package xyz.nucleoid.dungeons.dungeons.game.map.gen.style;

import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

public final class OvergrownDungeonStyle implements DungeonStyle {
	public static final OvergrownDungeonStyle INSTANCE = new OvergrownDungeonStyle();

	@Override
	public void placeCeiling(ChunkRegion world, BlockPos pos, Random random) {
		if (random.nextInt(32) > 0 && world.getBlockState(pos).isOf(Blocks.STONE)) {
			world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
		}

		if (random.nextInt(64) == 0) {
			world.setBlockState(pos, Blocks.SHROOMLIGHT.getDefaultState(), 3);
		}
	}

	@Override
	public void placeWall(ChunkRegion world, BlockPos pos, Random random, Direction direction) {
		if (random.nextInt(32) > 0 && world.getBlockState(pos).isOf(Blocks.STONE)) {
			world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
		}

		// Place mushroom
		if (random.nextInt(48) == 0) {
			BlockState state = random.nextBoolean() ? Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState() : Blocks.RED_MUSHROOM_BLOCK.getDefaultState();
			for(int x = -2; x <= 2; x++) {
				for(int z = -2; z <= 2; z++) {
					if (Math.abs(x) == 2 && Math.abs(z) == 2) {
						continue;
					}

					BlockPos local = pos.add(x, 0, z);
					if (world.getBlockState(local).getMaterial().isReplaceable()) {
						world.setBlockState(local, state, 3);
					}
				}
			}
		}

		if (random.nextInt(8) == 0) {
			BlockPos start = pos.offset(direction.getOpposite());
			if (world.getBlockState(start).isAir()) {

				for (int y = 0; y < 4 + random.nextInt(6); y++) {
					BlockPos local = start.down(y);
					if (!world.getBlockState(local).isAir()) {
						break;
					}

					world.setBlockState(local, Blocks.VINE.getDefaultState().with(VineBlock.getFacingProperty(direction), true), 3);
				}
			}
		}
	}

	@Override
	public void placeFloor(ChunkRegion world, BlockPos pos, Random random) {
		if (random.nextInt(32) > 0 && world.getBlockState(pos).isOf(Blocks.STONE)) {
			world.setBlockState(pos, Blocks.DIRT.getDefaultState(), 3);
			if (random.nextInt(2) == 0) {
				if (world.getBlockState(pos.up()).isAir()) {
					world.setBlockState(pos.up(), Blocks.GRASS.getDefaultState(), 3);
				}
			}
		}
	}

	@Override
	public RegistryKey<Biome> getFakingBiome() {
		return BiomeKeys.JUNGLE;
	}
}
