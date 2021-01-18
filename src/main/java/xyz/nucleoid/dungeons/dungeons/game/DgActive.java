package xyz.nucleoid.dungeons.dungeons.game;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn.SpawnerManager;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerManager;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.BlockBounds;
import xyz.nucleoid.plasmid.util.PlayerRef;
import xyz.nucleoid.plasmid.util.Scheduler;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DgActive {
    private final DgConfig config;
    private final DgMap map;
    public final GameSpace gameSpace;
    public final Object2ObjectMap<PlayerRef, DgPlayer> participants;
    private final DgSpawnLogic spawnLogic;
    private final TriggerManager triggerManager;

    private DgActive(
            GameSpace gameSpace,
            DgMap map,
            TriggerManager triggerManager,
            SpawnerManager spawnerManager,
            DgConfig config,
            Set<PlayerRef> participants
    ) {
        this.gameSpace = gameSpace;
        this.config = config;
        this.map = map;
        this.spawnLogic = new DgSpawnLogic(gameSpace, map);
        this.participants = new Object2ObjectOpenHashMap<>();
        this.triggerManager = triggerManager;
        for (PlayerRef player : participants) {
            this.participants.put(player, new DgPlayer());
        }
        spawnerManager.spawnAll(gameSpace.getWorld());
    }

    public static void open(
            GameSpace gameWorld,
            DgMap map,
            TriggerManager triggerManager,
            SpawnerManager spawnerManager,
            DgConfig config
    ) throws GameOpenException {
        Set<PlayerRef> participants = gameWorld.getPlayers().stream()
                .map(PlayerRef::of)
                .collect(Collectors.toSet());
        DgActive active = new DgActive(gameWorld, map, triggerManager, spawnerManager, config, participants);

        gameWorld.openGame(builder -> {
            builder.setRule(GameRule.CRAFTING, RuleResult.DENY);
            builder.setRule(GameRule.PORTALS, RuleResult.DENY);
            builder.setRule(GameRule.PVP, RuleResult.DENY);
            builder.setRule(GameRule.HUNGER, RuleResult.DENY);
            builder.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
            builder.setRule(GameRule.INTERACTION, RuleResult.DENY);
            builder.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
            builder.setRule(GameRule.THROW_ITEMS, RuleResult.DENY);
            builder.setRule(GameRule.UNSTABLE_TNT, RuleResult.ALLOW);

            builder.on(GameOpenListener.EVENT, active::onOpen);
            builder.on(GameCloseListener.EVENT, active::onClose);

            builder.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
            builder.on(PlayerAddListener.EVENT, active::addPlayer);
            builder.on(PlayerRemoveListener.EVENT, active::removePlayer);

            builder.on(GameTickListener.EVENT, active::tick);

            builder.on(PlayerDamageListener.EVENT, active::onPlayerDamage);
            builder.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
            builder.on(ExplosionListener.EVENT, active::onExplosion);
        });
    }

    private void onOpen() {
        ServerWorld world = this.gameSpace.getWorld();
        for (PlayerRef ref : this.participants.keySet()) {
            ref.ifOnline(world, this::spawnParticipant);
        }
    }

    private void onClose() {}

    private void addPlayer(ServerPlayerEntity player) {
        if (!this.participants.containsKey(PlayerRef.of(player))) {
            this.spawnSpectator(player);
        }
    }

    private void removePlayer(ServerPlayerEntity player) {
        this.participants.remove(PlayerRef.of(player));
    }

    private boolean reduceFallDamage(ServerPlayerEntity player) {
        for (BlockBounds region : this.map.fallDamageReduceRegions) {
            if (region.contains(player.getBlockPos())) {
                return true;
            }
        }

        return false;
    }

    private ActionResult onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        return ActionResult.PASS;
    }

    private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        if (source == DamageSource.FALL && this.reduceFallDamage(player)) {
            // TODO(plasmid): plasmid should only set to 20hp iff health == 0
            Scheduler.INSTANCE.submit((MinecraftServer server) -> player.setHealth(1.0f));
            player.setHealth(1.0f);
            // TODO(antibody): when antibody/scoped events exist this can be made less jank
            // Before then I cba because any solution I do will be temporary and bad anyway - Restioson.
        } else {
            this.spawnParticipant(player);
        }

        return ActionResult.FAIL;
    }

    // TODO(antibody)
    private void onExplosion(List<BlockPos> blockPos) {
        ServerWorld world = this.gameSpace.getWorld();
        blockPos.removeIf(pos -> {
            for (BlockBounds region : this.map.explosionAllowRegions) {
                if (region.contains(pos) && world.getBlockState(pos).getBlock() == Blocks.INFESTED_COBBLESTONE) {
                    return false;
                }
            }

            return true;
        });
    }

    private void spawnParticipant(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
        this.spawnLogic.spawnPlayer(player);
    }

    private void spawnSpectator(ServerPlayerEntity player) {
        this.spawnLogic.resetPlayer(player, GameMode.SPECTATOR);
        this.spawnLogic.spawnPlayer(player);
    }

    private void tick() {
        this.triggerManager.tick(this);
    }
}
