package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerInstantiationError;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;

import java.util.List;

public class GiveItemAction implements Action {
    private final Item item;
    private final CompoundTag customData;

    public GiveItemAction(Item item, CompoundTag customData) {
        this.item = item;
        this.customData = customData;
    }

    public static GiveItemAction create(MapTemplate template, TemplateRegion trigger, CompoundTag data) throws TriggerInstantiationError {
        if (!data.contains("item")) {
            throw new TriggerInstantiationError("Item is a required argument for dungeons:give");
        }

        Identifier id = Identifier.tryParse(data.getString("item"));

        if (id == null || !Registry.ITEM.getOrEmpty(id).isPresent()) {
            throw new TriggerInstantiationError("Invalid item `" + data.getString("item") + "`");
        }

        System.out.println(id);

        Item item = Registry.ITEM.get(id);

        CompoundTag customData = new CompoundTag();
        if (data.contains("data")) {
            customData = data.getCompound("data");
        }

        return new GiveItemAction(item, customData);
    }

    @Override
    public void execute(DgActive active, List<OnlineParticipant> targets) {
        for (OnlineParticipant participant : targets) {
            ItemStack stack = this.item.getDefaultStack();
            stack.setTag(this.customData);
            participant.entity.inventory.offerOrDrop(active.gameSpace.getWorld(), stack);
        }
    }
}
