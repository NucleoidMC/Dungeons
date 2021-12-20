package xyz.nucleoid.dungeons.dungeons.game;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameSpace;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;

import java.util.Random;

public class DgSpawnLogic {
    private final ServerWorld world;
    private final DgMap map;

    public DgSpawnLogic(ServerWorld world, DgMap map) {
        this.world = world;
        this.map = map;
    }

    public void resetPlayer(ServerPlayerEntity player, GameMode gameMode) {
        player.changeGameMode(gameMode);
        player.setVelocity(Vec3d.ZERO);
        player.fallDistance = 0.0f;
        player.setFireTicks(0);
    }

    public PlayerOfferResult acceptPlayer(PlayerOffer offer, GameMode gameMode) {
        var player = offer.player();
        return offer.accept(this.world, this.chooseSpawn(player.getRandom()))
                .and(() -> this.resetPlayer(player, gameMode));
    }


    public void spawnPlayer(ServerPlayerEntity player) {
        Vec3d pos = this.chooseSpawn(player.getRandom());
        player.teleport(this.world, pos.x, pos.y, pos.z, this.map.spawnAngle, 0.0F);
    }

    private Vec3d chooseSpawn(Random random) {
        BlockPos min = this.map.spawn.min();
        BlockPos max = this.map.spawn.max();

        double x = MathHelper.nextDouble(random, min.getX(), max.getX());
        double z = MathHelper.nextDouble(random, min.getZ(), max.getZ());
        double y = min.getY();

        return new Vec3d(x, y, z);
    }
}
