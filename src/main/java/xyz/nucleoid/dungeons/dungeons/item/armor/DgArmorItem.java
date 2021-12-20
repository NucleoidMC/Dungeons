package xyz.nucleoid.dungeons.dungeons.item.armor;

import com.google.common.collect.ImmutableSet;
import eu.pb4.polymer.api.item.PolymerItem;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import xyz.nucleoid.dungeons.dungeons.item.DgFlavorTextProvider;
import xyz.nucleoid.dungeons.dungeons.item.DgFlavorText;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.material.DgArmorMaterial;

import java.util.*;

public class DgArmorItem extends Item implements PolymerItem, DgArmor, DgFlavorTextProvider {
    private final EquipmentSlot slot;
    private final double baseToughness;
    private final DgArmorMaterial material;

    public DgArmorItem(DgArmorMaterial material, EquipmentSlot slot, double baseToughness, Settings settings) {
        super(settings);
        this.slot = slot;
        this.baseToughness = baseToughness;
        this.material = material;
    }

    @Override
    public double getBaseToughness() {
        return this.baseToughness;
    }

    @Override
    public double getKnockBackResistance() {
        return 0;
    }

    public DgArmorMaterial getMaterial() {
        return this.material;
    }

    public ItemStack createStack(DgItemQuality quality) {
        return DgArmorUtil.initArmor(DgArmorUtil.armorBuilder(this).build(), quality);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        for (DgItemQuality quality : DgItemQuality.values()) {
            if (group == quality.getItemGroup()) {
                if (quality.ordinal() >= this.material.getMinQuality().ordinal() && quality.ordinal() <= this.material.getMaxQuality().ordinal()) {
                    stacks.add(this.createStack(quality));
                }

            }
        }
    }

    @Override
    public Set<EquipmentSlot> getValidSlots(ItemStack stack) {
        return ImmutableSet.of(this.slot);
    }

    private DgFlavorText generateFlavourText(Random random, DgItemQuality quality, DgArmorMaterial material) {
        if (quality == null || material == null) {
            return null;
        }
        List<DgFlavorText> choices = new ArrayList<>();

        if (quality.ordinal() <= DgItemQuality.DUSTY.ordinal()) {
            Collections.addAll(
                    choices,
                    this.flavorTextOf("unimpressive", 1),
                    this.flavorTextOf("time", 2),
                    this.flavorTextOf("soldier", 3),
                    this.flavorTextOf("disease", 2),
                    this.flavorTextOf("cookfire", 3)
            );
        } else if (quality.ordinal() <= DgItemQuality.FINE.ordinal()) {
            Collections.addAll(
                    choices,
                    this.flavorTextOf("will_do", 1),
                    this.flavorTextOf("unknowledgeable", 3),
                    this.flavorTextOf("standard_issue", 3)
            );
        } else if (quality.ordinal() <= DgItemQuality.SUPERB.ordinal()) {
            Collections.addAll(
                    choices,
                    this.flavorTextOf("glow", 2),
                    this.flavorTextOf("whispers", 3),
                    this.flavorTextOf("craftsmanship", 2),
                    this.flavorTextOf("skeleton", 3)
            );
        } else {
            Collections.addAll(
                    choices,
                    this.flavorTextOf("carvings", 2),
                    this.flavorTextOf("legendary", 1),
                    this.flavorTextOf("reflection", 3),
                    this.flavorTextOf("ransom", 2)
            );
        }
        Collections.addAll(
                choices,
                this.flavorTextOf("prized_material", 2),
                this.flavorTextOf("expense", 2),
                this.flavorTextOf("gram", 3)
        );

        int idx = random.nextInt(choices.size());
        return choices.get(idx);
    }

    @Override
    public DgFlavorText getFlavorText(Random random, ItemStack stack) {
        return this.generateFlavourText(random, DgItemUtil.qualityOf(stack), this.material);
    }

    @Override
    public String getFlavorTextType() {
        return "armor";
    }

    @Override
    public Text getName(ItemStack stack) {
        DgItemQuality quality = DgItemUtil.qualityOf(stack);
        Text materialText = this.material == null ? new LiteralText("!! Null Material !!").formatted(Formatting.RED) : new TranslatableText(this.material.getTranslationKey());
        return DgItemUtil.formatName(new TranslatableText(stack.getItem().getTranslationKey(), materialText), quality);
    }

    @Override
    public Item getPolymerItem(ItemStack stack, ServerPlayerEntity player) {
        return this.material.getPlaceholderItem(this.slot);
    }
}
