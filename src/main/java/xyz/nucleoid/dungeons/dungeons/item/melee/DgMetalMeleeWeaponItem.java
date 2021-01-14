package xyz.nucleoid.dungeons.dungeons.item.melee;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.base.DgFlavorTextProvider;
import xyz.nucleoid.dungeons.dungeons.util.item.DgFlavorText;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMeleeWeaponMetal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DgMetalMeleeWeaponItem extends DgMaterialMeleeWeaponItem<DgMeleeWeaponMetal> implements DgFlavorTextProvider {

    public DgMetalMeleeWeaponItem(double baseAttackDamage, double baseAttackSpeed, Item proxy, Settings settings) {
        super(baseAttackDamage, baseAttackSpeed, new DgMaterialComponent<>(DgMeleeWeaponMetal.class, (random, meanLevel) -> DgMeleeWeaponMetal.choose(random, meanLevel, false)), proxy, settings);
    }

    private DgFlavorText generateFlavourText(Random random, DgItemQuality quality, DgMeleeWeaponMetal metal) {
        if (quality == null || metal == null) {
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

        if (metal.ordinal() <= DgMeleeWeaponMetal.BRONZE.ordinal()) {
            Collections.addAll(
                    choices,
                    flavorTextOf("strained", 3),
                    flavorTextOf("easy_to_work", 2),
                    flavorTextOf("pot", 2)
            );
        } else if (metal.ordinal() <= DgMeleeWeaponMetal.DAMASCUS_STEEL.ordinal()) {
            Collections.addAll(
                    choices,
                    flavorTextOf("formidable_edge", 2),
                    flavorTextOf("balanced", 2),
                    flavorTextOf("durable", 2)
            );
        } else {
            Collections.addAll(
                    choices,
                    flavorTextOf("prized_material", 2),
                    flavorTextOf("expense", 2),
                    flavorTextOf("gram", 3)
            );
        }
        int idx = random.nextInt(choices.size());
        return choices.get(idx);
    }

    @Override
    public DgFlavorText getFlavorText(Random random, ItemStack stack) {
        return generateFlavourText(random, DgItemUtil.qualityOf(stack), DgItemUtil.materialOf(stack, getMaterialComponent()));
    }

    @Override
    public String getFlavorTextType() {
        return "metal_melee_weapon";
    }
}
