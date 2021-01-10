package xyz.nucleoid.dungeons.dungeons.game.map.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;

public class RoomManager {
	private final List<Room> rooms;

	public RoomManager() {
		Random random = new Random();
		List<Room> rooms = new ArrayList<>();
		rooms.add(new SpheroidRoom(random, new BlockPos(0, 20, 0), 10, 5, 10));
		rooms.add(new SpheroidRoom(random, new BlockPos( 15, 18, 0), 12, 3, 4));

		this.rooms = rooms;
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
