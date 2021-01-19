package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;

import java.util.List;

public class GiveItemAction implements Action {
    private final Item item;
    private final int count;
    private final CompoundTag customData;

    private GiveItemAction(Item item, int count, CompoundTag customData) {
        this.item = item;
        this.count = count;
        this.customData = customData;
    }

    public static GiveItemAction create(MapTemplate template, TemplateRegion trigger, CompoundTag data) throws ScriptTemplateInstantiationError {
        if (!data.contains("item")) {
            throw new ScriptTemplateInstantiationError("Item is a required argument for dungeons:give");
        }

        Identifier id = Identifier.tryParse(data.getString("item"));

        if (id == null || !Registry.ITEM.getOrEmpty(id).isPresent()) {
            throw new ScriptTemplateInstantiationError("Invalid item `" + data.getString("item") + "`");
        }

        System.out.println(id);

        Item item = Registry.ITEM.get(id);

        CompoundTag customData = new CompoundTag();
        if (data.contains("data")) {
            customData = data.getCompound("data");
        }

        int count = 1;
        if (data.contains("count")) {
            count = data.getInt("count");
        }

        return new GiveItemAction(item, count, customData);
    }

    @Override
    public void execute(DgActive active, List<OnlineParticipant> targets) {
        for (OnlineParticipant participant : targets) {
            ItemStack stack = this.item.getDefaultStack();
            stack.setTag(this.customData);
            stack.setCount(this.count);
            participant.entity.inventory.offerOrDrop(active.gameSpace.getWorld(), stack);
        }
    }
}
