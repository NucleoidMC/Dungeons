package xyz.nucleoid.dungeons.dungeons.game;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.entity.enemy.DgEnemy;
import xyz.nucleoid.dungeons.dungeons.game.scripting.behavior.ExplodableRegion;
import xyz.nucleoid.dungeons.dungeons.game.scripting.enemy_spawn.SpawnerManager;
import xyz.nucleoid.dungeons.dungeons.game.scripting.quest.QuestManager;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerManager;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.BlockBounds;
import xyz.nucleoid.plasmid.util.PlayerRef;
import xyz.nucleoid.plasmid.util.Scheduler;
import xyz.nucleoid.plasmid.widget.GlobalWidgets;
import xyz.nucleoid.plasmid.widget.SidebarWidget;

import java.util.ArrayList;
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
    public final GlobalWidgets widgets;
    public final QuestManager questManager;

    private DgActive(
            GameSpace gameSpace,
            DgMap map,
            TriggerManager triggerManager,
            SpawnerManager spawnerManager,
            DgConfig config,
            Set<PlayerRef> participants,
            GlobalWidgets widgets
    ) {
        this.gameSpace = gameSpace;
        this.config = config;
        this.map = map;
        this.spawnLogic = new DgSpawnLogic(gameSpace, map);
        this.participants = new Object2ObjectOpenHashMap<>();
        this.questManager = new QuestManager();
        this.triggerManager = triggerManager;
        this.widgets = widgets;

        List<OnlineParticipant> online = new ArrayList<>();
        for (PlayerRef player : participants) {
            this.participants.put(player, new DgPlayer());
            player.ifOnline(this.gameSpace.getWorld(), p -> online.add(new OnlineParticipant(this.participant(p), p)));
        }

        spawnerManager.spawnAll(gameSpace.getWorld());

        for (Action action : this.map.spawnActions) {
            action.execute(this, online);
        }
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

        gameWorld.openGame(builder -> {
            GlobalWidgets widgets = new GlobalWidgets(builder);
            DgActive active = new DgActive(gameWorld, map, triggerManager, spawnerManager, config, participants, widgets);
            builder.setRule(GameRule.CRAFTING, RuleResult.DENY);
            builder.setRule(GameRule.PORTALS, RuleResult.DENY);
            builder.setRule(GameRule.PVP, RuleResult.DENY);
            builder.setRule(GameRule.HUNGER, RuleResult.DENY);
            builder.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
            builder.setRule(GameRule.INTERACTION, RuleResult.ALLOW);
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

            builder.on(AttackEntityListener.EVENT, active::onAttackEntity);
            builder.on(EntityHitListener.EVENT, active::onHitEntity);
            builder.on(UseBlockListener.EVENT, active::onUseBlock);
        });
    }

    private @Nullable DgPlayer participant(ServerPlayerEntity player) {
        return this.participants.get(PlayerRef.of(player));
    }

    private @Nullable DgPlayer participant(PlayerRef ref) {
        return this.participants.get(ref);
    }

    private ActionResult onUseBlock(ServerPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        DgPlayer participant = this.participant(player);

        if (participant == null) {
            return ActionResult.FAIL;
        }

        ServerWorld world = this.gameSpace.getWorld();
        Block block = world.getBlockState(hitResult.getBlockPos()).getBlock();

        if (block == Blocks.LEVER) {
            return ActionResult.PASS;
        } else {
            return ActionResult.FAIL;
        }
    }

    private ActionResult onHitEntity(ProjectileEntity projectileEntity, EntityHitResult hitResult) {
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
    private void onExplosion(List<BlockPos> blockPos) {
        ServerWorld world = this.gameSpace.getWorld();
        blockPos.removeIf(pos -> {
            for (ExplodableRegion region: this.map.explodableRegions) {
                if (region.region.contains(pos) && region.isExplodable(world.getBlockState(pos).getBlock())) {
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
        // arbitrary interval to avoid ticking too often for performance
        if (this.gameSpace.getWorld().getTime() % 5 == 0) {
            this.triggerManager.tick(this);
            this.questManager.tick(this);
        }
    }
}
