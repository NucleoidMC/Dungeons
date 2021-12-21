package xyz.nucleoid.dungeons.dungeons.game;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.GameMode;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.entity.enemy.DgEnemy;
import xyz.nucleoid.dungeons.dungeons.game.scripting.behavior.ExplodableRegion;
import xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn.SpawnerManager;
import xyz.nucleoid.dungeons.dungeons.game.scripting.quest.QuestManager;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerManager;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.map_templates.BlockBounds;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.common.GlobalWidgets;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.PlayerOffer;
import xyz.nucleoid.plasmid.game.player.PlayerOfferResult;
import xyz.nucleoid.plasmid.game.rule.GameRuleType;
import xyz.nucleoid.plasmid.util.PlayerRef;
import xyz.nucleoid.stimuli.event.player.PlayerAttackEntityEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDamageEvent;
import xyz.nucleoid.stimuli.event.player.PlayerDeathEvent;
import xyz.nucleoid.plasmid.util.Scheduler;
import xyz.nucleoid.stimuli.event.projectile.ProjectileHitEvent;
import xyz.nucleoid.stimuli.event.world.ExplosionDetonatedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DgActive {
    private final DgConfig config;
    private final DgMap map;
    public final GameSpace gameSpace;
    public final ServerWorld world;

    // TODO replace with ServerPlayerEntity if players are removed upon leaving
    public final Object2ObjectMap<PlayerRef, DgPlayer> participants;
    private final DgSpawnLogic spawnLogic;
    private final TriggerManager triggerManager;
    public final GlobalWidgets widgets;
    public final QuestManager questManager;

    private DgActive(
            ServerWorld world,
            GameSpace gameSpace,
            DgMap map,
            TriggerManager triggerManager,
            SpawnerManager spawnerManager,d
            DgConfig config,
            Set<PlayerRef> participants,
            GlobalWidgets widgets
    ) {
        this.gameSpace = gameSpace;
        this.config = config;
        this.map = map;
        this.spawnLogic = new DgSpawnLogic(world, map);
        this.world = world;
        this.participants = new Object2ObjectOpenHashMap<>();
        this.questManager = new QuestManager();
        this.triggerManager = triggerManager;
        this.widgets = widgets;

        List<OnlineParticipant> online = new ArrayList<>();
        for (PlayerRef player : participants) {
            this.participants.put(player, new DgPlayer());
            player.ifOnline(this.world, p -> online.add(new OnlineParticipant(this.participant(p), p)));
        }

        spawnerManager.spawnAll(this.world);

        for (Action action : this.map.spawnActions) {
            action.execute(this, online);
        }
    }

    public static void open(
            ServerWorld world,
            GameSpace gameSpace,
            DgMap map,
            TriggerManager triggerManager,
            SpawnerManager spawnerManager,
            DgConfig config
    ) throws GameOpenException {
        Set<PlayerRef> participants = gameSpace.getPlayers().stream()
                .map(PlayerRef::of)
                .collect(Collectors.toSet());

        gameSpace.setActivity(activity -> {
            GlobalWidgets widgets = GlobalWidgets.addTo(activity);
            DgActive active = new DgActive(world, gameSpace, map, triggerManager, spawnerManager, config, participants, widgets);

            activity.deny(GameRuleType.CRAFTING);
            activity.deny(GameRuleType.PORTALS);
            activity.deny(GameRuleType.PVP);
            activity.deny(GameRuleType.HUNGER);
            activity.allow(GameRuleType.FALL_DAMAGE);
            activity.allow(GameRuleType.INTERACTION);
            activity.deny(GameRuleType.BLOCK_DROPS);
            activity.deny(GameRuleType.THROW_ITEMS);
            activity.deny(GameRuleType.UNSTABLE_TNT);

            activity.listen(GameActivityEvents.ENABLE, active::onOpen);
            activity.listen(GameActivityEvents.DISABLE, active::onClose);

            activity.listen(GamePlayerEvents.OFFER, active::acceptPlayer);
            activity.listen(GamePlayerEvents.REMOVE, active::removePlayer);

            activity.listen(GameActivityEvents.TICK, active::tick);

            activity.listen(PlayerDamageEvent.EVENT, active::onPlayerDamage);
            activity.listen(PlayerDeathEvent.EVENT, active::onPlayerDeath);
            activity.listen(ExplosionDetonatedEvent.EVENT, active::onExplosion);
            activity.listen(PlayerAttackEntityEvent.EVENT, active::onAttackEntity);
            activity.listen(ProjectileHitEvent.ENTITY, active::onProjectileHitEntity); // TODO
        });
    }

    private @Nullable DgPlayer participant(ServerPlayerEntity player) {
        return this.participants.get(PlayerRef.of(player));
    }

    private @Nullable DgPlayer participant(PlayerRef ref) {
        return this.participants.get(ref);
    }

    private ActionResult onProjectileHitEntity(ProjectileEntity projectileEntity, EntityHitResult hitResult) {
        if (hitResult.getEntity() instanceof DgEnemy) {
            return ActionResult.PASS;
        } else {
            return ActionResult.FAIL;
        }
    }

    private ActionResult onAttackEntity(ServerPlayerEntity playerEntity, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (entity instanceof DgEnemy) {
            return ActionResult.PASS;
        } else {
            return ActionResult.FAIL;
        }
    }


    private void onOpen() {
        this.participants.keySet().removeIf(ref -> !ref.isOnline(this.world));
        for (PlayerRef ref : this.participants.keySet()) {
            ref.ifOnline(this.world, player -> {
                this.spawnLogic.resetPlayer(player, GameMode.ADVENTURE);
                this.spawnLogic.spawnPlayer(player);
            });
        }
    }

    private void onClose() {}

    private PlayerOfferResult acceptPlayer(PlayerOffer offer) {
        return this.spawnLogic.acceptPlayer(offer, GameMode.SPECTATOR);
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
            // TODO(plasmid): plasmid should set to 20hp iff health == 0
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
    private void onExplosion(Explosion explosion, boolean particles) {
        explosion.getAffectedBlocks().removeIf(pos -> {
            for (ExplodableRegion region: this.map.explodableRegions) {
                if (region.region().contains(pos) && region.isExplodable(this.world.getBlockState(pos).getBlock())) {
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

    private void tick() {
        // arbitrary interval to avoid ticking too often for performance
        if (this.world.getTime() % 5 == 0) {
            this.triggerManager.tick(this);
            this.questManager.tick(this);
        }
    }
}
