package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgModelRegistry;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.Random;

import static xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgWeaponGenerator.ATTACK_DAMAGE_MODIFIER_ID;

public class DgQuarterstaff {
    public DgLootGrade grade;
    public DgQuarterstaffWood wood;
    public double attackDamage;

    public static int BASE_DAMAGE = 5;

    public DgQuarterstaff(DgLootGrade grade, DgQuarterstaffWood wood, double attackDamage) {
        this.grade = grade;
        this.wood = wood;
        this.attackDamage = attackDamage;
    }

    public static void registerModels() {
        for (DgQuarterstaffWood material : DgQuarterstaffWood.values()) {
            DgModelRegistry.register(Items.STICK, material.id, "quarterstaff");
        }
    }

    // TODO(weapons): flavour text
    public static DgQuarterstaff generate(Random random) {
        DgQuarterstaffWood wood = DgQuarterstaffWood.choose(random);
        DgLootGrade grade = DgLootGrade.chooseInRange(random, wood.minGrade, wood.maxGrade);
        double attackDamage = (BASE_DAMAGE + (random.nextDouble() / 2)) * wood.damageModifier * grade.damageModifier;

        return new DgQuarterstaff(grade, wood, attackDamage);
    }

    public ItemStack toItemStack() {
        ItemStackBuilder builder = ItemStackBuilder.of(Items.STICK)
                .setName(new TranslatableText("item.dungeons.quarterstaff", new TranslatableText("grade." + this.grade.id), new TranslatableText("material.quarterstaff." + this.wood.id)))
                .addModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);

        DgWeaponGenerator.addLoreWrapped(builder, String.format("A %s quarterstaff made of %s.", this.grade.id.toLowerCase(), this.wood.id.toLowerCase()));
        ItemStack stack = builder.build();
        DgWeaponGenerator.addCustomModel(stack, wood.id, "quarterstaff");
        return stack;
    }
}
