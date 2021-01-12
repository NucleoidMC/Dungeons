package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgModelRegistry;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.Random;

public class DgBow {
    public DgBowType type;
    public DgLootGrade grade;
    public DgBowMaterial material;
    public double attackDamage;
    public int drawTicks;

    public DgBow(DgBowType type, DgBowMaterial material, DgLootGrade grade, double attackDamage, int drawTicks) {
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
    public static DgBow generate(Random random, double meanLevel) {
        DgBowType type = DgBowType.choose(random);
        DgBowMaterial material = DgBowMaterial.choose(random, meanLevel);
        DgLootGrade grade = DgLootGrade.chooseInRange(random, material.minGrade, material.maxGrade);
        double attackDamage = (type.baseDamage + (random.nextDouble() / 2)) * material.damageModifier * grade.damageModifier;

        return new DgBow(type, material, grade, attackDamage, type.baseDrawTicks);
    }

    public ItemStack toItemStack() {
        ItemStackBuilder builder = ItemStackBuilder.of(this.type.asVanillaItem())
                .setUnbreakable()
                .setName(new TranslatableText("item.dungeons." + this.type.id, new TranslatableText("grade." + this.grade.id), new TranslatableText("material.bow." + this.material.id)));
        // TODO handle damage and draw ticks better
        DgWeaponGenerator.addWeaponInfoWrapped(builder, String.format("A %s %s made of %s.", this.grade.id, this.type.id, this.material.id));
        ItemStack stack = DgWeaponGenerator.fakeWeaponStats(builder, this.attackDamage, this.drawTicks);
        DgWeaponGenerator.addCustomModel(stack, material.id, type.id);
        DgWeaponGenerator.formatName(stack, grade);
        return stack;

    }
}
