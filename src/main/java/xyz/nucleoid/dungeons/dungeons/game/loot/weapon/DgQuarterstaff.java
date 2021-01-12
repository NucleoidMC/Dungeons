package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.Random;

import static xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgWeaponGenerator.ATTACK_DAMAGE_MODIFIER_ID;
import static xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgWeaponGenerator.ATTACK_SPEED_MODIFIER_ID;

public class DgQuarterstaff {
    public DgLootGrade grade;
    public DgQuarterstaffWood wood;
    public double attackDamage;
    public double attackSpeed;

    public static double BASE_DAMAGE = 4.7;
    public static double BASE_ATTACK_SPEED = 1.8;

    public DgQuarterstaff(DgLootGrade grade, DgQuarterstaffWood wood, double attackDamage, double attackSpeed) {
        this.grade = grade;
        this.wood = wood;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
    }

    // TODO(weapons): flavour text
    public static DgQuarterstaff generate(Random random) {
        DgQuarterstaffWood wood = DgQuarterstaffWood.choose(random);
        DgLootGrade grade = DgLootGrade.chooseInRange(random, wood.minGrade, wood.maxGrade);
        double attackDamage = (BASE_DAMAGE + (random.nextDouble() / 2)) * wood.damageModifier * grade.damageModifier;

        return new DgQuarterstaff(grade, wood, attackDamage, BASE_ATTACK_SPEED);
    }

    public ItemStack toItemStack() {
        ItemStackBuilder builder = ItemStackBuilder.of(Items.STICK)
                .setName(new LiteralText(String.format("%s %s Quarterstaff", this.grade.name, this.wood.name)))
                .setUnbreakable()
                // modifier is based on empty hand, so some subtraction must be done
                .addModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage - 0.5, EntityAttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND)
                .addModifier(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Attack speed modifier", this.attackSpeed - 4.0, EntityAttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);

        DgWeaponGenerator.addLoreWrapped(builder, String.format("A %s quarterstaff made of %s.", this.grade.name.toLowerCase(), this.wood.name.toLowerCase()));
        ItemStack stack = builder.build();
        stack.getOrCreateTag().putByte("HideFlags", (byte) 2);
        return stack;
    }
}
