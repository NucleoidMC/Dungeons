package xyz.nucleoid.dungeons.dungeons.game;

import net.minecraft.util.math.Vec3d;

import xyz.nucleoid.plasmid.game.GameSpace;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;

public class DgSpawnLogic {
    private final GameSpace gameWorld;
    private final DgMap map;

    public DgSpawnLogic(GameSpace gameWorld, DgMap map) {
        this.gameWorld = gameWorld;
        this.map = map;
    }

    public void resetPlayer(ServerPlayerEntity player, GameMode gameMode) {
        player.setGameMode(gameMode);
        player.setVelocity(Vec3d.ZERO);
        player.fallDistance = 0.0f;

    }

    public void spawnPlayer(ServerPlayerEntity player) {
        ServerWorld world = this.gameWorld.getWorld();

        player.teleport(world, 0, 40, 0, 0.0F, 0.0F);
    }
}
