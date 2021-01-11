package xyz.nucleoid.dungeons.dungeons.game.map.gen;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public abstract class Room {
	public Room(Random random) {

	}

	public abstract boolean shouldCarve(BlockPos pos);

	public abstract BlockPos getCenter();

	public abstract Vec3i getSize();
}
