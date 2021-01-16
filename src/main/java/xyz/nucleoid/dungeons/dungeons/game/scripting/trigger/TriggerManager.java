package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

import com.mojang.serialization.Lifecycle;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.Identifier;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions.GiveEffectAction;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions.GiveItemAction;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions.GravityAction;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria.Once;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria.OncePerPlayer;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;
import xyz.nucleoid.plasmid.registry.TinyRegistry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class TriggerManager {
    public static final TinyRegistry<ActionBuilder> ACTION_BUILDERS = new TinyRegistry<>(Lifecycle.stable());

    public final List<Trigger> triggers = new LinkedList<>();

    static {
        ACTION_BUILDERS.register(new Identifier("dungeons", "gravity"), GravityAction::create);
        ACTION_BUILDERS.register(new Identifier("dungeons", "effect"), GiveEffectAction::create);
        ACTION_BUILDERS.register(new Identifier("dungeons", "give"), GiveItemAction::create);
    }

    private static Identifier tryParseIdentifier(String str) {
        String[] split = str.split(":");

        if (split.length == 2) {
            return new Identifier(split[0], split[1]);
        } else {
            return new Identifier("dungeons", split[0]);
        }
    }

    public void parseAll(MapTemplate template) throws TriggerInstantiationError {
        for (TemplateRegion region : template.getMetadata().getRegions("trigger").collect(Collectors.toList())) {
            CompoundTag data = region.getData();
            if (data.contains("actions")) {
                ListTag actionList = data.getList("actions", NbtType.COMPOUND);

                List<Action> actions = new ArrayList<>();
                for (int i = 0; i < actionList.size(); i++) {
                    CompoundTag tag = actionList.getCompound(i);
                    Identifier id = TriggerManager.tryParseIdentifier(tag.getString("type"));

                    if (!ACTION_BUILDERS.containsKey(id)) {
                        throw new TriggerInstantiationError("Invalid action `" + tag.getString("type") + "`");
                    }

                    Action action = Objects.requireNonNull(ACTION_BUILDERS.get(id)).create(template, region, tag);
                    actions.add(action);
                }

                TriggerCriterion criterion = new Once();
                if (data.contains("criterion")) {
                    String criterionName = data.getString("criterion");

                    switch (criterionName) {
                        case "once":
                            break;
                        case "once_per_player":
                            criterion = new OncePerPlayer();
                            break;
                        default:
                            throw new TriggerInstantiationError("Invalid run criterion `" + criterionName + "`");
                    }
                }

                this.triggers.add(new Trigger(region.getBounds(), criterion, actions));
            }
        }
    }
}
