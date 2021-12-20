package xyz.nucleoid.dungeons.dungeons.game;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn.SpawnerManager;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerManager;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.map_templates.MapTemplate;
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

import java.util.Optional;

public class DgWaiting {
    private final ServerWorld world;
    private final GameSpace gameSpace;
    private final DgMap map;
    private final DgConfig config;
    private final DgSpawnLogic spawnLogic;
    private final TriggerManager triggerManager;
    private final SpawnerManager spawnerManager;

    private DgWaiting(ServerWorld world, GameSpace gameSpace, DgMap map, DgConfig config, TriggerManager triggerManager, SpawnerManager spawnerManager) {
        this.gameSpace = gameSpace;
        this.config = config;
        this.world = world;
        this.triggerManager = triggerManager;
        this.spawnLogic = new DgSpawnLogic(world, map);
        this.map = map;
        this.spawnerManager = spawnerManager;
    }

    public static GameOpenProcedure open(GameOpenContext<DgConfig> context) {
        DgMap map = DgMap.create(context.server(), context.config());

        RuntimeWorldConfig worldConfig = new RuntimeWorldConfig().setGenerator(map.asGenerator(context.server()));

        TriggerManager triggerManager = new TriggerManager();
        SpawnerManager spawnerManager = new SpawnerManager();

        try {
            NbtCompound data = map.template.getMetadata().getData();

            double dungeonLevel = 1.0;
            if (data.contains("level")) {
                dungeonLevel = data.getDouble("level");
            }

            triggerManager.parseAll(map.template);
            spawnerManager.parseAll(map.template, dungeonLevel);
        } catch (ScriptTemplateInstantiationError e) {
            throw new GameOpenException(new LiteralText("Trigger instantiation error: " + e.reason));
        }

        return context.openWithWorld(worldConfig, (activity, world) -> {
            DgWaiting waiting = new DgWaiting(world, activity.getGameSpace(), map, context.config(), triggerManager, spawnerManager);
            GameWaitingLobby.addTo(activity, context.config().playerConfig);
            // TODO: Set resource pack, worldConfig.setResourcePack(  )

            activity.listen(GameActivityEvents.REQUEST_START, waiting::requestStart);
            activity.listen(GamePlayerEvents.OFFER, waiting::offerPlayer);
            activity.listen(PlayerDeathEvent.EVENT, waiting::onPlayerDeath);
        });
    }

    private GameResult requestStart() {
        DgActive.open(this.world, this.gameSpace, this.map, this.triggerManager, this.spawnerManager, this.config);
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
