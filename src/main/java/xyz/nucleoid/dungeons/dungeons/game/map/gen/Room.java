package xyz.nucleoid.dungeons.dungeons.game.map.gen;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

public abstract class Room {
	public Room(Random random) {

	}

	public abstract boolean shouldCarve(BlockPos pos);
}
