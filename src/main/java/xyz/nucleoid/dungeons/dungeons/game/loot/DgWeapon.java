package xyz.nucleoid.dungeons.dungeons.game.loot;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;

public class DgWeapon {
    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
\
    public DgWeaponType type;
    public DgLootGrade grade;
    public DgWeaponMetal metal; // TODO(restioson): how to do other weapons?
    public double attackDamage;

    public DgWeapon(DgWeaponType type, DgLootGrade grade, DgWeaponMetal metal, double attackDamage) {
        this.type = type;
        this.grade = grade;
        this.metal = metal;
        this.attackDamage = attackDamage;
    }

    public static DgWeapon generate(Random random) {
        DgWeaponType type = DgWeaponType.choose(random);
        DgWeaponMetal metal = DgWeaponMetal.choose(random);
        DgLootGrade grade = DgLootGrade.chooseInRange(random, metal.minGrade, metal.maxGrade);
        double attackDamage = 4 + random.nextDouble() * 4;

        // TODO: Round to 1 decimal point

        return new DgWeapon(type, grade, metal, attackDamage);
    }

    public ItemStack toItemStack() {
        return ItemStackBuilder.of(this.type.asVanillaItem())
                .setName(new LiteralText(String.format("%s %s %s", this.grade.name, this.metal.name, this.type.name)))
                .addModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND)
                .build();
    }
}
