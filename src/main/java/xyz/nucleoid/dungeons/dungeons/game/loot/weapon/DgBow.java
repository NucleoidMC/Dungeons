package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.Random;

import static xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgWeaponGenerator.ATTACK_DAMAGE_MODIFIER_ID;

public class DgBow {
    public DgBowType type;
    public DgLootGrade grade;
    public DgBowMaterial material;
    public double attackDamage;

    public DgBow(DgBowType type, DgLootGrade grade, DgBowMaterial material, double attackDamage) {
        this.type = type;
        this.grade = grade;
        this.material = material;
        this.attackDamage = attackDamage;
    }

    // TODO(weapons): flavour text
    public static DgBow generate(Random random) {
        DgBowType type = DgBowType.choose(random);
        DgBowMaterial material = DgBowMaterial.choose(random);
        DgLootGrade grade = DgLootGrade.chooseInRange(random, material.minGrade, material.maxGrade);
        double attackDamage = (type.baseDamage + (random.nextDouble() / 2)) * material.damageModifier * grade.damageModifier;

        return new DgBow(type, grade, material, attackDamage);
    }

    public ItemStack toItemStack() {
        ItemStackBuilder builder = ItemStackBuilder.of(this.type.asVanillaItem())
                .setName(new LiteralText(String.format("%s %s %s", this.grade.name, this.material.name, this.type.name)))
                .addModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);

        DgWeaponGenerator.addLoreWrapped(builder, String.format("A %s %s made of %s.", this.grade.name.toLowerCase(), this.type.name.toLowerCase(), this.material.name.toLowerCase()));
        return builder.build();
    }
}
