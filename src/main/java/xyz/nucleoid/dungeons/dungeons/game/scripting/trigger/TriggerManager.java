package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.Dungeons;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.DgPlayer;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptingUtil;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions.*;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria.OnceTargetsAll;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria.OncePerPlayer;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria.OnceTargetsTriggerer;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;
import xyz.nucleoid.plasmid.registry.TinyRegistry;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.*;
import java.util.stream.Collectors;

public class TriggerManager {
    public static final TinyRegistry<ActionBuilder> ACTION_BUILDERS = TinyRegistry.create();
    public static final TinyRegistry<TriggerCriterionBuilder> TRIGGER_CRITERION_BUILDERS = TinyRegistry.create();

    public final List<Trigger> triggers = new LinkedList<>();

    static {
        registerAction("gravity", GravityAction::create);
        registerAction("effect", GiveEffectAction::create);
        registerAction("give", GiveItemAction::create);
        registerAction("set_quest", SetNewQuestAction::create);
        registerAction("advance_objective", AdvanceObjectiveAction::create);

        registerCriterion("once_targets_all", OnceTargetsAll::create);
        registerCriterion("once_targets_triggerer", OnceTargetsAll::create);
        registerCriterion("once_per_player", OncePerPlayer::create);
    }

    private static void registerAction(String id, ActionBuilder builder) {
        ACTION_BUILDERS.register(new Identifier(Dungeons.ID, id), builder);
    }

    private static void registerCriterion(String id, TriggerCriterionBuilder builder) {
        TRIGGER_CRITERION_BUILDERS.register(new Identifier(Dungeons.ID, id), builder);
    }

    public void parseAll(MapTemplate template) throws ScriptTemplateInstantiationError {
        for (TemplateRegion region : template.getMetadata().getRegions("trigger").collect(Collectors.toList())) {
            NbtCompound data = region.getData();
                if (data.contains("actions")) {
                this.triggers.add(TriggerManager.parse(template, region));
            }
        }
    }

    public static Trigger parse(MapTemplate template, TemplateRegion region) throws ScriptTemplateInstantiationError {
        NbtCompound data = region.getData();
        List<Action> actions = TriggerManager.parseActions(template, region);

        TriggerCriterion criterion = new OnceTargetsTriggerer();
        Identifier id = null;
        NbtCompound criterionData = null;

        if (data.contains("criterion", NbtType.STRING)) {
            id = ScriptingUtil.parseDungeonsDefaultId(data.getString("criterion"));
        } else if (data.contains("criterion", NbtType.COMPOUND)) {
            criterionData = data.getCompound("criterion");

            if (!criterionData.contains("type")) {
                throw new ScriptTemplateInstantiationError("Key `criterion` with compound tag must have `type`");
            }

            id = ScriptingUtil.parseDungeonsDefaultId(criterionData.getString("type"));
        }

        if (id != null) {
            TriggerCriterionBuilder builder = TRIGGER_CRITERION_BUILDERS.get(id);

            if (builder == null) {
                throw new ScriptTemplateInstantiationError("Unknown criterion type `" + id + "`");
            }

            criterion = builder.create(criterionData);
        }

        return new Trigger(region.getBounds(), criterion, actions);
    }

    public static List<Action> parseActions(MapTemplate template, TemplateRegion region) throws ScriptTemplateInstantiationError {
        NbtCompound data = region.getData();
        NbtList actionList = data.getList("actions", NbtType.COMPOUND);

        List<Action> actions = new ArrayList<>();
        for (int i = 0; i < actionList.size(); i++) {
            NbtCompound tag = actionList.getCompound(i);
            Identifier id = ScriptingUtil.parseDungeonsDefaultId(tag.getString("type"));

            if (!ACTION_BUILDERS.containsKey(id)) {
                throw new ScriptTemplateInstantiationError("Invalid action `" + tag.getString("type") + "`");
            }

            Action action = Objects.requireNonNull(ACTION_BUILDERS.get(id)).create(template, region, tag);
            actions.add(action);
        }

        return actions;
    }

    public void tick(DgActive active) {
        ServerWorld world = active.world;

        this.triggers.removeIf(trigger -> {
            List<OnlineParticipant> inside = new ArrayList<>();

            for (Map.Entry<PlayerRef, DgPlayer> entry : active.participants.entrySet()) {
                ServerPlayerEntity player = entry.getKey().getEntity(world);

                if (player == null || !trigger.region.contains(player.getBlockPos()) || player.interactionManager.getGameMode() != GameMode.ADVENTURE) {
                    continue;
                }

                inside.add(new OnlineParticipant(entry.getValue(), player));
            }

            if (inside.isEmpty()) {
                return false;
            }

            TriggerCriterion.TestResult result = trigger.criterion.testForPlayers(active, inside);

            if (!result.runsFor.isEmpty()) {
                for (Action action : trigger.actions) {
                    action.execute(active, result.runsFor);
                }
            }

            return result.removeTrigger;
        });
    }
}
