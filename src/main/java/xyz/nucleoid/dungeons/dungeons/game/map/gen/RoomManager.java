package xyz.nucleoid.dungeons.dungeons.game.map.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;

public final class RoomManager {
	private final List<Room> rooms;

	public RoomManager() {
		Random random = new Random();
		RoomGenerator generator = new RoomGenerator(random);

		this.rooms = generator.generate();
	}

	public boolean shouldCarve(BlockPos pos) {
		for (Room room : this.rooms) {
			if (room.shouldCarve(pos)) {
				return true;
			}
		}

		return false;
	}
}
