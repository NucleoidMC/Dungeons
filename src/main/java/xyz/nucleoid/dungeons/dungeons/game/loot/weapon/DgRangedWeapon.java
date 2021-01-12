package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;
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
        this.grade = grade;
        this.material = material;
        this.attackDamage = attackDamage;
        this.drawTicks = drawTicks;
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
                .setName(new LiteralText(String.format("%s %s %s", this.grade.name, this.material.name, this.type.name)));

        DgWeaponGenerator.addWeaponInfoWrapped(builder, String.format("A %s %s made of %s.", this.grade.name.toLowerCase(), this.type.name.toLowerCase(), this.material.name.toLowerCase()));
        ItemStack stack = DgWeaponGenerator.fakeWeaponStats(builder, this.attackDamage, this.drawTicks);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt(DRAW_TICKS_TAG, this.drawTicks);
        tag.putDouble(DAMAGE_TAG, this.attackDamage);
        return stack;
    }
}
