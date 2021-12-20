package xyz.nucleoid.dungeons.dungeons.item.weapon.melee;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.item.DgFlavorTextProvider;
import xyz.nucleoid.dungeons.dungeons.item.DgFlavorText;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMeleeWeaponMetal;

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

        if (metal.ordinal() <= DgMeleeWeaponMetal.BRONZE.ordinal()) {
            Collections.addAll(
                    choices,
                    this.flavorTextOf("strained", 3),
                    this.flavorTextOf("easy_to_work", 2),
                    this.flavorTextOf("pot", 2)
            );
        } else if (metal.ordinal() <= DgMeleeWeaponMetal.DAMASCUS_STEEL.ordinal()) {
            Collections.addAll(
                    choices,
                    this.flavorTextOf("formidable_edge", 2),
                    this.flavorTextOf("balanced", 2),
                    this.flavorTextOf("durable", 2)
            );
        } else {
            Collections.addAll(
                    choices,
                    this.flavorTextOf("prized_material", 2),
                    this.flavorTextOf("expense", 2),
                    this.flavorTextOf("gram", 3)
            );
        }
        int idx = random.nextInt(choices.size());
        return choices.get(idx);
    }

    @Override
    public DgFlavorText getFlavorText(Random random, ItemStack stack) {
        return this.generateFlavourText(random, DgItemUtil.qualityOf(stack), DgItemUtil.materialOf(stack, this.getMaterialComponent()));
    }

    @Override
    public String getFlavorTextType() {
        return "metal_melee_weapon";
    }
}
