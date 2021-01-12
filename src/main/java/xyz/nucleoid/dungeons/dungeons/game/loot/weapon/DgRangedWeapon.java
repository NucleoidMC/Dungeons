package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.nbt.CompoundTag;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgModelRegistry;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.Random;

public class DgRangedWeapon {
    public DgRangedWeaponType type;
    public DgLootGrade grade;
    public DgRangedWeaponMaterial material;

    // This is kind of a "damage target". See DgBowItem for more info on how its used. - Restioson
    public double attackDamage;
    public int drawTicks;

    public static String DRAW_TICKS_TAG = "dungeons_bow_draw_ticks";
    public static String DAMAGE_TAG = "dungeons_bow_damage";

    public DgRangedWeapon(DgRangedWeaponType type, DgLootGrade grade, DgRangedWeaponMaterial material, double attackDamage, int drawTicks) {
        this.type = type;
        this.material = material;
        this.grade = grade;
        this.attackDamage = attackDamage;
        this.drawTicks = drawTicks;
    }

    public static void registerModels() {
        for (DgBowType type : DgBowType.values()) {
            for (DgBowMaterial material : DgBowMaterial.values()) {
                DgModelRegistry.register(type.asVanillaItem(), material.id, type.id);
            }
        }
    }

    // TODO(weapons): flavour text
    public static DgRangedWeapon generate(Random random, double meanLevel) {
        DgRangedWeaponType type = DgRangedWeaponType.choose(random);
        DgRangedWeaponMaterial material = DgRangedWeaponMaterial.choose(random, meanLevel);
        DgLootGrade grade = DgLootGrade.chooseInRange(random, material.minGrade, material.maxGrade);
        double attackDamage = (type.baseDamage + (random.nextDouble() / 2)) * material.damageModifier * grade.damageModifier;

        return new DgRangedWeapon(type, grade, material, attackDamage, type.baseDrawTicks);
    }

    public ItemStack toItemStack() {
        ItemStackBuilder builder = ItemStackBuilder.of(this.type.asItem())
                .setUnbreakable()
                .setName(new TranslatableText("item.dungeons." + this.type.id, new TranslatableText("grade." + this.grade.id), new TranslatableText("material.bow." + this.material.id)));
        // TODO handle damage and draw ticks better
        DgWeaponGenerator.addWeaponInfoWrapped(builder, String.format("A %s %s made of %s.", this.grade.id, this.type.id, this.material.id));
        ItemStack stack = DgWeaponGenerator.fakeWeaponStats(builder, this.attackDamage, this.drawTicks);
        DgWeaponGenerator.addCustomModel(stack, material.id, type.id);
        DgWeaponGenerator.formatName(stack, grade);

        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(DRAW_TICKS_TAG, this.drawTicks);
        tag.putDouble(DAMAGE_TAG, this.attackDamage);

        return stack;
    }
}
