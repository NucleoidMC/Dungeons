package xyz.nucleoid.dungeons.dungeons.game;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

import xyz.nucleoid.dungeons.dungeons.game.map.DgMapConfig;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMapGenerator;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.plasmid.game.*;
import xyz.nucleoid.plasmid.game.common.GameWaitingLobby;
import xyz.nucleoid.plasmid.game.event.*;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;

public class DgWaiting {
    private final ServerWorld world;
    private final GameSpace gameSpace;
    private final DgMap map;
    private final DgConfig config;
    private final DgSpawnLogic spawnLogic;

    private DgWaiting(ServerWorld world, GameSpace gameSpace, DgMap map, DgConfig config) {
        this.world = world;
        this.gameSpace = gameSpace;
        this.map = map;
        this.config = config;
        this.spawnLogic = new DgSpawnLogic(world, map);
    }

    public static GameOpenProcedure open(GameOpenContext<DgConfig> context) {
        DgConfig config = context.config();
        DgMapGenerator generator = new DgMapGenerator(new DgMapConfig(Blocks.REDSTONE_BLOCK.getDefaultState()));
        DgMap map = generator.build();

        RuntimeWorldConfig worldConfig = new RuntimeWorldConfig().setGenerator(map.asGenerator(context.server()));

        return context.openWithWorld(worldConfig, (activity, world) -> {
            DgWaiting waiting = new DgWaiting(world, activity.getGameSpace(), map, context.config());
            GameWaitingLobby.addTo(activity, context.config().playerConfig);

            activity.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);
            activity.listen(GamePlayerEvents.OFFER, waiting::offerPlayer);
            activity.listen(PlayerDeathEvent.EVENT, waiting::onPlayerDeath);
        });
    }

    private GameResult requestStart() {
        DgActive.open(this.world, this.gameSpace, this.map, this.config);
        return GameResult.ok();
    }

    private PlayerOfferResult offerPlayer(PlayerOffer offer) {
        return this.spawnLogic.acceptPlayer(offer, GameMode.ADVENTURE);
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
