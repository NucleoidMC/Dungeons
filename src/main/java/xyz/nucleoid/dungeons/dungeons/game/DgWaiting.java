package xyz.nucleoid.dungeons.dungeons.game;

import net.minecraft.util.ActionResult;
import xyz.nucleoid.plasmid.game.GameOpenContext;
import xyz.nucleoid.plasmid.game.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.GameWorld;
import xyz.nucleoid.plasmid.game.StartResult;
import xyz.nucleoid.plasmid.game.event.PlayerAddListener;
import xyz.nucleoid.plasmid.game.event.PlayerDeathListener;
import xyz.nucleoid.plasmid.game.event.RequestStartListener;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMapGenerator;
import xyz.nucleoid.plasmid.world.bubble.BubbleWorldConfig;

import java.util.concurrent.CompletableFuture;

public class DgWaiting {
    private final GameWorld gameWorld;
    private final DgMap map;
    private final DgConfig config;
    private final DgSpawnLogic spawnLogic;

    private DgWaiting(GameWorld gameWorld, DgMap map, DgConfig config) {
        this.gameWorld = gameWorld;
        this.map = map;
        this.config = config;
        this.spawnLogic = new DgSpawnLogic(gameWorld, map);
    }

    public static CompletableFuture<GameWorld> open(GameOpenContext<DgConfig> context) {
        DgMapGenerator generator = new DgMapGenerator(context.getConfig().mapConfig);

        return generator.create().thenCompose(map -> {
            BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                    .setGenerator(map.asGenerator(context.getServer()))
                    .setDefaultGameMode(GameMode.SPECTATOR);

            return context.openWorld(worldConfig).thenApply(gameWorld -> {
                DgWaiting waiting = new DgWaiting(gameWorld, map, context.getConfig());

                GameWaitingLobby.open(gameWorld, context.getConfig().playerConfig, builder -> {
                    builder.on(RequestStartListener.EVENT, waiting::requestStart);
                    builder.on(PlayerAddListener.EVENT, waiting::addPlayer);
                    builder.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
                });

                return gameWorld;
            });
        });
    }

    private StartResult requestStart() {
        DgActive.open(this.gameWorld, this.map, this.config);
        return StartResult.OK;
    }

    private void addPlayer(ServerPlayerEntity player) {
        this.spawnPlayer(player);
    }

    private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        player.setHealth(20.0f);
        this.spawnPlayer(player);
        return ActionResult.FAIL;
    }

    private void spawnPlayer(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);
    }
}
