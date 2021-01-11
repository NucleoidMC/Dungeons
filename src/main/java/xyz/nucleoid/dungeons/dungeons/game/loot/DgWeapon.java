package xyz.nucleoid.dungeons.dungeons.game.loot;

import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.Random;

public class DgWeapon {
    public DgWeaponType type;
    public DgLootGrade grade;
    public DgWeaponMetal metal; // TODO(restioson): how to do other weapons?

    public DgWeapon(DgWeaponType type, DgLootGrade grade, DgWeaponMetal metal) {
        this.type = type;
        this.grade = grade;
        this.metal = metal;
    }

    public static DgWeapon generate(Random random) {
        DgWeaponType type = DgWeaponType.choose(random);
        DgWeaponMetal metal = DgWeaponMetal.choose(random);
        DgLootGrade grade = DgLootGrade.chooseInRange(random, metal.minGrade, metal.maxGrade);

        return new DgWeapon(type, grade, metal);
    }

    public ItemStack toItemStack() {
        return ItemStackBuilder.of(this.type.asVanillaItem())
                .setName(new LiteralText(String.format("%s %s %s", this.grade.name, this.metal.name, this.type.name)))
                .build();
    }
}
