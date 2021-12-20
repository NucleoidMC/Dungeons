package xyz.nucleoid.dungeons.dungeons.game;

import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.plasmid.game.GameSpace;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;

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

    }

    public void spawnPlayer(ServerPlayerEntity player) {
        player.teleport(this.world, 0, 65, 0, 0.0F, 0.0F);
    }

    public PlayerOfferResult acceptPlayer(PlayerOffer offer, GameMode gameMode) {
        var player = offer.player();
        return offer.accept(this.world, this.findSpawnFor(player))
                .and(() -> this.resetPlayer(player, gameMode));
    }

    public Vec3d findSpawnFor(ServerPlayerEntity player) {
        return new Vec3d(0.0, 65.0, 0.0);
    }
}
