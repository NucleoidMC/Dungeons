package xyz.nucleoid.dungeons.dungeons.game.map.gen;

import java.util.Random;

import kdotjpg.opensimplex.OpenSimplexNoise;
import xyz.nucleoid.plasmid.game.world.generator.GameChunkGenerator;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.StructuresConfig;

public class DgChunkGenerator extends GameChunkGenerator {
	private final OpenSimplexNoise horizontalThreshold;
	private final OpenSimplexNoise verticalThreshold;

	public DgChunkGenerator(MinecraftServer server) {
		super(createBiomeSource(server, BuiltinBiomes.THE_VOID), new StructuresConfig(false));
		Random random = new Random();
		horizontalThreshold = new OpenSimplexNoise(random.nextLong());
		verticalThreshold = new OpenSimplexNoise(random.nextLong());
	}

	@Override
	public void populateNoise(WorldAccess world, StructureAccessor structures, Chunk chunk) {
		int chunkX = chunk.getPos().x * 16;
		int chunkZ = chunk.getPos().z * 16;

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Random random = new Random();

		for (int x = chunkX; x < chunkX + 16; x++) {
			for (int z = chunkZ; z < chunkZ + 16; z++) {

				double threshold = horizontalThresholdAt(horizontalThreshold.eval(x / 35.0, z / 35.0));

				for (int y = 0; y < 40; y++) {
					mutable.set(x, y, z);

					BlockState state = Blocks.STONE.getDefaultState();

					if (y == 0 || y == 39) {
						state = Blocks.BEDROCK.getDefaultState();
						world.setBlockState(mutable, state, 3);
					} else {
						if ((noiseFalloffAt(y) + (verticalThreshold.eval(x / 40.0, y / 20.0, z / 40.0) * 2)) > threshold) {
							world.setBlockState(mutable, state, 3);
						}
					}
				}
		    }
		}
	}

	private double horizontalThresholdAt(double noise) {
		return (7.5 * noise * noise);
	}

	private double noiseFalloffAt(int y) {
		return (16.0 / y) - (16.0 / (y - 40));
	}

	@Override
	public void generateFeatures(ChunkRegion world, StructureAccessor structures) {
		int chunkX = world.getCenterChunkX() * 16;
		int chunkZ = world.getCenterChunkZ() * 16;

		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Random random = new Random();

		for (int x = chunkX; x < chunkX + 16; x++) {
			for (int z = chunkZ; z < chunkZ + 16; z++) {
				if (random.nextInt(60) == 0) {
					// Place glowstone
					for (int y = 0; y < 39; y++) {
						mutable.set(x, y, z);

						BlockState atPos = world.getBlockState(mutable);

						if (atPos.isOpaque() && world.getBlockState(mutable.up()).isAir()) {
							if (random.nextBoolean()) {
								world.setBlockState(mutable, Blocks.GLOWSTONE.getDefaultState(), 3);
							}
						}

						if (atPos.isOpaque() && world.getBlockState(mutable.down()).isAir()) {
							if (random.nextBoolean()) {
								world.setBlockState(mutable, Blocks.GLOWSTONE.getDefaultState(), 3);
							}
						}
					}
				}
			}
		}
	}
}
