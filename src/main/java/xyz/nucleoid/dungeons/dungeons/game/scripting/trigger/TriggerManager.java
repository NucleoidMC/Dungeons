package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.DgPlayer;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptingUtil;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions.*;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria.Once;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria.OncePerPlayer;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;
import xyz.nucleoid.plasmid.registry.TinyRegistry;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.*;
import java.util.stream.Collectors;

public class TriggerManager {
    public static final TinyRegistry<ActionBuilder> ACTION_BUILDERS = new TinyRegistry<>(Lifecycle.stable());

    public final List<Trigger> triggers = new LinkedList<>();

    static {
        register("gravity", GravityAction::create);
        register("effect", GiveEffectAction::create);
        register("give", GiveItemAction::create);
        register("set_quest", SetNewQuestAction::create);
        register("advance_objective", AdvanceObjectiveAction::create);
    }

    private static void register(String id, ActionBuilder builder) {
        ACTION_BUILDERS.register(new Identifier("dungeons", id), builder);
    }

    public void parseAll(MapTemplate template) throws ScriptTemplateInstantiationError {
        for (TemplateRegion region : template.getMetadata().getRegions("trigger").collect(Collectors.toList())) {
            CompoundTag data = region.getData();
            if (data.contains("actions")) {
                ListTag actionList = data.getList("actions", NbtType.COMPOUND);

                List<Action> actions = new ArrayList<>();
                for (int i = 0; i < actionList.size(); i++) {
                    CompoundTag tag = actionList.getCompound(i);
                    Identifier id = ScriptingUtil.parseDungeonsDefaultId(tag.getString("type"));

                    if (!ACTION_BUILDERS.containsKey(id)) {
                        throw new ScriptTemplateInstantiationError("Invalid action `" + tag.getString("type") + "`");
                    }

                    Action action = Objects.requireNonNull(ACTION_BUILDERS.get(id)).create(template, region, tag);
                    actions.add(action);
                }

                TriggerCriterion criterion = new Once();
                if (data.contains("criterion")) {
                    String criterionName = data.getString("criterion");

                    // TODO(criteria): custom criteria
                    switch (criterionName) {
                        case "once":
                            break;
                        case "once_per_player":
                            criterion = new OncePerPlayer();
                            break;
                        default:
                            throw new ScriptTemplateInstantiationError("Invalid run criterion `" + criterionName + "`");
                    }
                }

                this.triggers.add(new Trigger(region.getBounds(), criterion, actions));
            }
        }
    }

    public void tick(DgActive active) {
        ServerWorld world = active.gameSpace.getWorld();

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

            TriggerCriterion.TestResult result = trigger.criterion.testForPlayers(inside);

            if (!result.runsFor.isEmpty()) {
                for (Action action : trigger.actions) {
                    action.execute(active, result.runsFor);
                }
            }

            return result.removeTrigger;
        });
    }
}
