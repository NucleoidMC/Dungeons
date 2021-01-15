package xyz.nucleoid.dungeons.dungeons.game.map.gen;

import java.util.Random;

import xyz.nucleoid.dungeons.dungeons.game.map.gen.room.RoomManager;
import xyz.nucleoid.dungeons.dungeons.game.map.gen.style.DungeonStyle;
import xyz.nucleoid.dungeons.dungeons.game.map.gen.style.OvergrownDungeonStyle;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;
import xyz.nucleoid.substrate.gen.GenHelper;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.StructuresConfig;

public class DgChunkGenerator extends GameChunkGenerator {
	private final RoomManager roomManager;

	public DgChunkGenerator(MinecraftServer server) {
		super(createBiomeSource(server, OvergrownDungeonStyle.INSTANCE.getFakingBiome()), new StructuresConfig(false));
		Random random = new Random();
		this.roomManager = new RoomManager();
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor structures, Chunk chunk) {
		int chunkX = chunk.getPos().x * 16;
		int chunkZ = chunk.getPos().z * 16;

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Random random = new Random();

		for (int x = chunkX; x < chunkX + 16; x++) {
			for (int z = chunkZ; z < chunkZ + 16; z++) {
				for (int y = 0; y < 80; y++) {
					mutable.set(x, y, z);

					BlockState state = Blocks.STONE.getDefaultState();

					if (y == 0 || y == 79) {
						state = Blocks.BEDROCK.getDefaultState();
					}

					if (this.roomManager.shouldCarve(mutable)) {
						state = Blocks.AIR.getDefaultState();
					}

					world.setBlockState(mutable, state, 3);
				}
		    }
		}
	}

	@Override
	public void generateFeatures(ChunkRegion world, StructureAccessor structures) {
		int chunkX = world.getCenterChunkX() * 16;
		int chunkZ = world.getCenterChunkZ() * 16;

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Random random = new Random();

		DungeonStyle style = OvergrownDungeonStyle.INSTANCE;

		for (int x = chunkX; x < chunkX + 16; x++) {
			for (int z = chunkZ; z < chunkZ + 16; z++) {
				for (int y = 0; y < 80; y++) {
					mutable.set(x, y, z);

					if (world.getBlockState(mutable).isAir()) {
						BlockPos up = mutable.up();
						if (world.getBlockState(up).isOpaque()) {
							style.placeCeiling(world, up, random);
						}

						BlockPos down = mutable.down();
						if (world.getBlockState(down).isOpaque()) {
							style.placeFloor(world, down, random);
						}

						for (Direction direction : GenHelper.HORIZONTALS) {
							BlockPos wall = mutable.offset(direction);
							if (world.getBlockState(wall).isOpaque()) {
								style.placeWall(world, wall, random, direction);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void carve(long seed, BiomeAccess access, Chunk chunk, GenerationStep.Carver carver) {

	}
}