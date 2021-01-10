package xyz.nucleoid.dungeons.dungeons.game.map.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xyz.nucleoid.substrate.gen.GenHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class RoomGenerator {
	private static final double PI2 = Math.PI / 2.0;
	private final Random random;

	public RoomGenerator(Random random) {
		this.random = random;
	}

	public List<Room> generate() {
		List<Room> rooms = new ArrayList<>();
		BlockPos center = new BlockPos(0, 20, 0);
		rooms.add(new SpheroidRoom(this.random, center, 12, 5, 12));

		rooms.addAll(intersection(this.random, 1.0, 1, center, 12, 12, 8, 4, 3));
		rooms.addAll(intersection(this.random, 1.0, 1, new BlockPos(26, 20, 0), 4, 4, 6, 2, 2));

		return rooms;
	}

	private static List<Room> intersection(Random random, double tunnelChance, int forced, BlockPos center, int xRadius, int zRadius, int tunnelLength, int tunnelWidth, int tunnelHeight) {
		List<Room> rooms = new ArrayList<>();

		for (Direction direction : GenHelper.HORIZONTALS) {
			rooms.add(new SpheroidRoom(random, new BlockPos(
					radNext(center.getX(), direction.getOffsetX() * xRadius),
					center.getY(),
					radNext(center.getZ(), direction.getOffsetZ() * zRadius)
			), Math.abs(direction.getOffsetX()) * tunnelLength + tunnelWidth, tunnelHeight, Math.abs(direction.getOffsetZ()) * tunnelLength + tunnelWidth));
		}

		return rooms;
	}

	private static int radNext(int start, int rad) {
		return (int) (PI2 * rad + start);
	}
}
