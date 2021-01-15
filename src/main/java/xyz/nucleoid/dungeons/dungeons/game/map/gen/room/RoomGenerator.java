package xyz.nucleoid.dungeons.dungeons.game.map.gen.room;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import xyz.nucleoid.substrate.gen.GenHelper;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class RoomGenerator {
	private static final double PI2 = Math.PI / 2.0;
	private final Random random;

	public RoomGenerator(Random random) {
		this.random = random;
	}

	public List<Room> generate() {
		BlockPos center = new BlockPos(0, 40, 0);

		return generate(center, Direction.NORTH, this.random, 0, 6);
	}

	private static List<Room> generate(BlockPos center, Direction direction, Random random, int currentDepth, int maxDepth) {
		List<Room> rooms = new ArrayList<>();

		ConnectionType type = random.nextBoolean() ? ConnectionType.END_ROOM : ConnectionType.TUNNEL;
		if (random.nextInt(3) == 0) {
			type = ConnectionType.INTERSECTION;
		}

		type = currentDepth == 0 ? ConnectionType.START_ROOM : type;
		type = currentDepth == 1 ? ConnectionType.TUNNEL : type;

		if (currentDepth == maxDepth) {
			return rooms;
		}

		if (type == ConnectionType.START_ROOM) {
			rooms.add(new SpheroidRoom(random, center, 12, 5, 12));

			for (Direction dir : GenHelper.HORIZONTALS) {
				rooms.addAll(generate(getCenterForRadius(center, dir, 12, 12), dir, random, currentDepth + 1, maxDepth));
			}
		} else if (type == ConnectionType.END_ROOM) {
			rooms.add(new SpheroidRoom(random, center, 8 + random.nextInt(4), 5 + random.nextInt(3), 8 + random.nextInt(4)));
		} else if (type == ConnectionType.TUNNEL) {
			int tunnelLength = random.nextInt(8) + 8;
			rooms.add(tunnel(random, direction, center, tunnelLength, 4, 4));

			rooms.addAll(generate(getCenterForRadius(center, direction, targetDirection(direction, Direction.Axis.X, tunnelLength, 4), targetDirection(direction, Direction.Axis.Z, tunnelLength, 4)), direction, random, currentDepth + 1, maxDepth));
		} else if (type == ConnectionType.INTERSECTION) {
			for (Direction dir : GenHelper.HORIZONTALS) {
				// Skip going in the direction we just came from
				if (dir == direction.getOpposite()) {
					continue;
				}

				int tunnelLength = random.nextInt(8) + 8;
				rooms.add(tunnel(random, dir, center, tunnelLength, 4, 4));

				rooms.addAll(generate(getCenterForRadius(center, dir, targetDirection(dir, Direction.Axis.X, tunnelLength, 4), targetDirection(dir, Direction.Axis.Z, tunnelLength, 4)), dir, random, currentDepth + 1, maxDepth));
			}
		} else {
			throw new RuntimeException("How did we get here?");
		}

		return rooms;
	}

	private static List<Room> intersection(Random random, double tunnelChance, int forced, Direction skip, BlockPos center, int tunnelLength, int tunnelWidth, int tunnelHeight) {
		List<Room> rooms = new ArrayList<>();

		for (Direction direction : GenHelper.HORIZONTALS) {
			if (skip != null && direction == skip) {
				continue;
			}

			rooms.add(tunnel(random, direction, center, tunnelLength, tunnelWidth, tunnelHeight));
		}

		return rooms;
	}

	private static Room tunnel(Random random, Direction direction, BlockPos center, int tunnelLength, int tunnelWidth, int tunnelHeight) {
		return new SpheroidRoom(random, center, Math.abs(direction.getOffsetX()) * tunnelLength + tunnelWidth, tunnelHeight, Math.abs(direction.getOffsetZ()) * tunnelLength + tunnelWidth);
	}

	/**
	 * Offsets the center position in a direction to get the start position for a tunnel.
	 */
	private static BlockPos getCenterForRadius(BlockPos center, Direction direction, int xRadius, int zRadius) {
		return new BlockPos(
				radNext(center.getX(), direction.getOffsetX() * xRadius),
				center.getY(),
				radNext(center.getZ(), direction.getOffsetZ() * zRadius)
		);
	}

	private static int targetDirection(Direction direction, Direction.Axis axis, int target, int fallback) {
		if (axis == Direction.Axis.X) {
			return Math.abs(direction.getOffsetX()) == 1 ? target : fallback;
		} else {
			return Math.abs(direction.getOffsetZ()) == 1 ? target : fallback;
		}
	}

	private static int radNext(int start, int rad) {
		return (int) (PI2 * rad + start);
	}

	private enum ConnectionType {
		START_ROOM,
		END_ROOM,
		TUNNEL,
		INTERSECTION
	}
}
