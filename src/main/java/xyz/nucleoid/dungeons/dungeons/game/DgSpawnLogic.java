package xyz.nucleoid.dungeons.dungeons.game;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import xyz.nucleoid.plasmid.game.GameSpace;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;

import java.util.Random;

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
        player.setFireTicks(0);
    }

    public void spawnPlayer(ServerPlayerEntity player) {
        ServerWorld world = this.gameWorld.getWorld();
        Vec3d pos = this.chooseSpawn(player.getRandom());
        player.teleport(world, pos.x, pos.y, pos.z, this.map.spawnAngle, 0.0F);
    }

    private Vec3d chooseSpawn(Random random) {
        BlockPos min = this.map.spawn.getMin();
        BlockPos max = this.map.spawn.getMax();

        double x = MathHelper.nextDouble(random, min.getX(), max.getX());
        double z = MathHelper.nextDouble(random, min.getZ(), max.getZ());
        double y = min.getY();

        return new Vec3d(x, y, z);
    }
}
