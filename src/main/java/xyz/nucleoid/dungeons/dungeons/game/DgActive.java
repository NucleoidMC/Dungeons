package xyz.nucleoid.dungeons.dungeons.game;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import org.graalvm.compiler.core.phases.EconomyHighTier;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Trigger;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerCriterion;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerManager;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.dungeons.dungeons.util.item.loot.DgEnemyDropGenerator;
import xyz.nucleoid.dungeons.dungeons.game.map.DgMap;
import xyz.nucleoid.plasmid.game.GameOpenException;
import xyz.nucleoid.plasmid.game.GameSpace;
import xyz.nucleoid.plasmid.game.event.*;
import xyz.nucleoid.plasmid.game.player.JoinResult;
import xyz.nucleoid.plasmid.game.rule.GameRule;
import xyz.nucleoid.plasmid.game.rule.RuleResult;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DgActive {
    private final DgConfig config;
    private final DgMap gameMap;
    public final GameSpace gameSpace;
    private final Object2ObjectMap<PlayerRef, DgPlayer> participants;
    private final DgSpawnLogic spawnLogic;
    private final TriggerManager triggerManager;

    private DgActive(GameSpace gameSpace, DgMap map, TriggerManager manager, DgConfig config, Set<PlayerRef> participants) {
        this.gameSpace = gameSpace;
        this.config = config;
        this.gameMap = map;
        this.spawnLogic = new DgSpawnLogic(gameSpace, map);
        this.participants = new Object2ObjectOpenHashMap<>();
        this.triggerManager = manager;

        for (PlayerRef player : participants) {
            this.participants.put(player, new DgPlayer());
        }
    }

    public static void open(GameSpace gameWorld, DgMap map, TriggerManager triggerManager, DgConfig config) throws GameOpenException {
        Set<PlayerRef> participants = gameWorld.getPlayers().stream()
                .map(PlayerRef::of)
                .collect(Collectors.toSet());
        DgActive active = new DgActive(gameWorld, map, triggerManager, config, participants);

        gameWorld.openGame(builder -> {
            builder.setRule(GameRule.CRAFTING, RuleResult.DENY);
            builder.setRule(GameRule.PORTALS, RuleResult.DENY);
            builder.setRule(GameRule.PVP, RuleResult.DENY);
            builder.setRule(GameRule.HUNGER, RuleResult.DENY);
            builder.setRule(GameRule.FALL_DAMAGE, RuleResult.ALLOW);
            builder.setRule(GameRule.INTERACTION, RuleResult.DENY);
            builder.setRule(GameRule.BLOCK_DROPS, RuleResult.DENY);
            builder.setRule(GameRule.THROW_ITEMS, RuleResult.DENY);
            builder.setRule(GameRule.UNSTABLE_TNT, RuleResult.DENY);

            builder.on(GameOpenListener.EVENT, active::onOpen);
            builder.on(GameCloseListener.EVENT, active::onClose);

            builder.on(OfferPlayerListener.EVENT, player -> JoinResult.ok());
            builder.on(PlayerAddListener.EVENT, active::addPlayer);
            builder.on(PlayerRemoveListener.EVENT, active::removePlayer);

            builder.on(GameTickListener.EVENT, active::tick);

            builder.on(PlayerDamageListener.EVENT, active::onPlayerDamage);
            builder.on(PlayerDeathListener.EVENT, active::onPlayerDeath);
        });
    }

    private void onOpen() {
        ServerWorld world = this.gameSpace.getWorld();
        for (PlayerRef ref : this.participants.keySet()) {
            ref.ifOnline(world, this::spawnParticipant);
        }
    }

    private void onClose() { }

    private void addPlayer(ServerPlayerEntity player) {
        if (!this.participants.containsKey(PlayerRef.of(player))) {
            this.spawnSpectator(player);
        }
    }

    private void removePlayer(ServerPlayerEntity player) {
        this.participants.remove(PlayerRef.of(player));
    }

    private ActionResult onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        return ActionResult.PASS;
    }

    private ActionResult onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        // TODO handle death
        this.spawnParticipant(player);
        return ActionResult.FAIL;
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
        ServerWorld world = this.gameSpace.getWorld();

        this.triggerManager.triggers.removeIf(trigger -> {
            List<OnlineParticipant> inside = new ArrayList<>();

            for (Map.Entry<PlayerRef, DgPlayer> entry : this.participants.entrySet()) {
                ServerPlayerEntity player = entry.getKey().getEntity(world);

                if (
                        player == null ||
                        !trigger.region.contains(player.getBlockPos()) ||
                        player.interactionManager.getGameMode() != GameMode.ADVENTURE
                ) {
                    continue;
                }

                inside.add(new OnlineParticipant(entry.getValue(), player));
            }

            if (inside.isEmpty()) {
                return false;
            }

            TriggerCriterion.TestResult result = trigger.criterion.testForPlayers(inside);

            if (!result.runsFor.isEmpty()) {
                for (Action action : trigger.actions) {
                    action.execute(this, result.runsFor);
                }
            }

            return result.removeTrigger;
        });
    }
}
