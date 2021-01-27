package xyz.nucleoid.dungeons.dungeons.item.armor;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import xyz.nucleoid.dungeons.dungeons.item.base.DgArmor;
import xyz.nucleoid.dungeons.dungeons.item.base.DgFlavorTextProvider;
import xyz.nucleoid.dungeons.dungeons.util.item.DgArmorUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.DgFlavorText;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgArmorMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterialComponent;
import xyz.nucleoid.plasmid.fake.FakeItem;

import java.util.*;

public class DgArmorItem extends Item implements FakeItem, DgArmor, DgFlavorTextProvider {
    private final EquipmentSlot slot;
    private final DgMaterialComponent<DgArmorMaterial> materialComponent = new DgMaterialComponent<>(DgArmorMaterial.class, DgArmorMaterial::choose);

    public DgArmorItem(EquipmentSlot slot, Settings settings) {
        super(settings);
        this.slot = slot;
    }

    @Override
    public double getArmorToughness() {
        return material.baseToughness;
    }

    @Override
    public double getKnockBackResistance() {
        return 0;
    }

    public ItemStack createStack(DgArmorMaterial material, DgItemQuality quality) {
        return DgArmorUtil.
    }

    @Override
    public Set<EquipmentSlot> getValidSlots(ItemStack stack) {
        return ImmutableSet.of(slot);
    }

    private DgFlavorText generateFlavourText(Random random, DgItemQuality quality, DgArmorMaterial material) {
        if (quality == null || material == null) {
            return null;
        }
        List<DgFlavorText> choices = new ArrayList<>();

        if (quality.ordinal() <= DgItemQuality.DUSTY.ordinal()) {
            Collections.addAll(
                    choices,
                    flavorTextOf("unimpressive", 1),
                    flavorTextOf("time", 2),
                    flavorTextOf("soldier", 3),
                    flavorTextOf("disease", 2),
                    flavorTextOf("cookfire", 3)
            );
        } else if (quality.ordinal() <= DgItemQuality.FINE.ordinal()) {
            Collections.addAll(
                    choices,
                    flavorTextOf("will_do", 1),
                    flavorTextOf("unknowledgeable", 3),
                    flavorTextOf("standard_issue", 3)
            );
        } else if (quality.ordinal() <= DgItemQuality.SUPERB.ordinal()) {
            Collections.addAll(
                    choices,
                    flavorTextOf("glow", 2),
                    flavorTextOf("whispers", 3),
                    flavorTextOf("craftsmanship", 2),
                    flavorTextOf("skeleton", 3)
            );
        } else {
            Collections.addAll(
                    choices,
                    flavorTextOf("carvings", 2),
                    flavorTextOf("legendary", 1),
                    flavorTextOf("reflection", 3),
                    flavorTextOf("ransom", 2)
            );
        }
        Collections.addAll(
                choices,
                flavorTextOf("prized_material", 2),
                flavorTextOf("expense", 2),
                flavorTextOf("gram", 3)
        );

        int idx = random.nextInt(choices.size());
        return choices.get(idx);
    }

    @Override
    public DgFlavorText getFlavorText(Random random, ItemStack stack) {
        return generateFlavourText(random, DgItemUtil.qualityOf(stack), DgItemUtil.materialOf(stack, materialComponent));
    }

    @Override
    public String getFlavorTextType() {
        return "armor";
    }
    @Override
    public Text getName(ItemStack stack) {
        return DgItemUtil.nameOf(stack, materialComponent);
    }

}
