package xyz.nucleoid.dungeons.dungeons.game;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;

import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn.SpawnerManager;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerManager;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.plasmid.game.*;
import xyz.nucleoid.plasmid.game.event.*;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;
import xyz.nucleoid.plasmid.map.template.MapTemplate;

import java.util.Optional;

public class DgWaiting {
    private final GameSpace gameWorld;
    private final DgConfig config;
    private final DgSpawnLogic spawnLogic;
    private final DgMap map;
    private final TriggerManager triggerManager;
    private final SpawnerManager spawnerManager;

    private DgWaiting(GameSpace gameWorld, DgMap map, DgConfig config, TriggerManager triggerManager, SpawnerManager spawnerManager) {
        this.gameWorld = gameWorld;
        this.config = config;
        this.triggerManager = triggerManager;
        this.spawnLogic = new DgSpawnLogic(gameWorld, map);
        this.map = map;
        this.spawnerManager = spawnerManager;
    }

    public static GameOpenProcedure open(GameOpenContext<DgConfig> context) throws GameOpenException {
        DgMap map = DgMap.create(context.getConfig());

        BubbleWorldConfig worldConfig = new BubbleWorldConfig()
                .setGenerator(map.asGenerator(context.getServer()))
                .setDefaultGameMode(GameMode.SPECTATOR);

        TriggerManager triggerManager = new TriggerManager();
        SpawnerManager spawnerManager = new SpawnerManager();
        Optional<MapTemplate> templateOpt = map.templateOrGenerator.left();
        if (templateOpt.isPresent()) {
            try {
                MapTemplate template = templateOpt.get();
                CompoundTag data = template.getMetadata().getData();

                double dungeonLevel = 1.0;
                if (data.contains("level")) {
                    dungeonLevel = data.getDouble("level");
                }

                triggerManager.parseAll(template);
                spawnerManager.parseAll(template, dungeonLevel);
            } catch (ScriptTemplateInstantiationError e) {
                throw new GameOpenException(new LiteralText("Trigger instantiation error: " + e.reason));
            }
        }

        return context.createOpenProcedure(worldConfig, (game) -> {
            DgWaiting waiting = new DgWaiting(game.getSpace(), map, context.getConfig(), triggerManager, spawnerManager);
            GameWaitingLobby.applyTo(game, context.getConfig().playerConfig);
            // TODO: Set resource pack, worldConfig.setResourcePack(  )

            game.on(RequestStartListener.EVENT, waiting::requestStart);
            game.on(PlayerAddListener.EVENT, waiting::addPlayer);
            game.on(PlayerDeathListener.EVENT, waiting::onPlayerDeath);
        });
    }

    private StartResult requestStart() {
        DgActive.open(this.gameWorld, this.map, this.triggerManager, this.spawnerManager, this.config);
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
