package xyz.nucleoid.dungeons.dungeons.game.loot.weapon;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.TranslatableText;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;
import xyz.nucleoid.dungeons.dungeons.game.loot.DgModelRegistry;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgWeaponGenerator.ATTACK_DAMAGE_MODIFIER_ID;

public class DgMetalMeleeWeapon {
    public DgMetalMeleeWeaponType type;
    public DgLootGrade grade;
    public DgWeaponMetal metal; // TODO(restioson): how to do other weapons?
    public String flavourText;
    public double attackDamage;

    public DgMetalMeleeWeapon(DgMetalMeleeWeaponType type, DgLootGrade grade, DgWeaponMetal metal, String flavourText, double attackDamage) {
        this.type = type;
        this.grade = grade;
        this.metal = metal;
        this.flavourText = flavourText;
        this.attackDamage = attackDamage;
    }

    public static void registerModels() {
        for (DgMetalMeleeWeaponType type : DgMetalMeleeWeaponType.values()) {
            for (DgWeaponMetal material : DgWeaponMetal.values()) {
                DgModelRegistry.register(type.asVanillaItem(), material.id, type.id);
            }
        }
    }

    public static DgMetalMeleeWeapon generate(Random random) {
        return create(null, null, null, random);
    }

    public static DgMetalMeleeWeapon create(DgMetalMeleeWeaponType type, DgWeaponMetal metal, DgLootGrade grade, Random random) {
        if (type == null) {
            type = DgMetalMeleeWeaponType.choose(random);
        }

        if (metal == null) {
            DgWeaponMetal skip = null;
            if (type == DgMetalMeleeWeaponType.MACE) {
                skip = DgWeaponMetal.DAMASCUS;
            }
            metal = DgWeaponMetal.choose(random, skip);
        }

        if (grade == null) {
            grade = DgLootGrade.chooseInRange(random, metal.minGrade, metal.maxGrade);
        }
        double attackDamage = (type.baseDamage + (random.nextDouble() / 2)) * metal.damageModifier * grade.damageModifier;
        String flavourText = DgMetalMeleeWeapon.generateFlavourText(random, grade, metal);

        return new DgMetalMeleeWeapon(type, grade, metal, flavourText, attackDamage);
    }

    private static String generateFlavourText(Random random, DgLootGrade grade, DgWeaponMetal metal) {
        List<String> choices = new ArrayList<>();

        if (grade.ordinal() <= DgLootGrade.DUSTY.ordinal()) {
            Collections.addAll(
                    choices,
                    "It is unimpressive.",
                    "Time has taken its toll on this weapon.",
                    "It was most likely used by some long-forgotten army foot soldier.",
                    "Maybe it will give your enemies some sort of disease?",
                    "It would be a bad idea to bring this anywhere near to the cookfire."
            );
        } else if (grade.ordinal() <= DgLootGrade.FINE.ordinal()) {
            Collections.addAll(
                    choices,
                    "It will do, for now.",
                    "Impressive to anyone who isn't knowledgeable about weapons.",
                    "It seems to be standard issue from an old kingdom army."
            );
        } else if (grade.ordinal() <= DgLootGrade.SUPERB.ordinal()) {
            Collections.addAll(
                    choices,
                    "It almost seems to glow softly in the dim light.",
                    "If you put your ear to it to listen to its inaudible whispers, you'd cut yourself badly.",
                    "It is clear that the craftsmanship is of quality.",
                    "Enough to make any self-respecting skeleton think twice about engaging in combat with you."
            );
        } else {
            Collections.addAll(
                    choices,
                    "The carvings are immaculate, but indecipherable.",
                    "The work of a legendary smith.",
                    "You can see your reflection with perfect clarity in its blade.",
                    "In its day, it was probably worth more than a king's ransom."
            );
        }

        if (metal.ordinal() <= DgWeaponMetal.BRONZE.ordinal()) {
            Collections.addAll(
                    choices,
                    "It looks as if it might shatter if it were strained just a bit too much.",
                    "An easy-to-work metal, but the tradeoff is clear.",
                    "Frankly, it'd be worth more melted down and turned into a pot."
            );
        } else if (metal.ordinal() <= DgWeaponMetal.DAMASCUS.ordinal()) {
            Collections.addAll(
                    choices,
                    "A sturdy blade with a formidable cutting edge.",
                    "Rather heavy, but balanced and sharp nonetheless.",
                    "It is impressively durable and flexible."
            );
        } else {
            Collections.addAll(
                    choices,
                    "A prized material, worthy of a blade of this calibre.",
                    "No expense was spared in the creation of this weapon.",
                    "Many will never see a gram of this metal in their lives, let alone an entire blade."
            );
        }

        int idx = random.nextInt(choices.size());
        return choices.get(idx);
    }

    public ItemStack toItemStack() {
        ItemStackBuilder builder = ItemStackBuilder.of(this.type.asVanillaItem())
                .setName(new TranslatableText("item.dungeons." + this.type.id, new TranslatableText("grade." + this.grade.id), new TranslatableText("material.metal." + this.metal.id)))
                .addModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);

        DgWeaponGenerator.addLoreWrapped(builder, String.format("A %s %s made of %s.", this.grade.id.toLowerCase(), this.type.id.toLowerCase(), this.metal.id.toLowerCase()));
        DgWeaponGenerator.addLoreWrapped(builder, this.flavourText);
        ItemStack stack = builder.build();
        DgWeaponGenerator.addCustomModel(stack, metal.id, type.id);
        DgWeaponGenerator.formatName(stack, grade);
        return stack;
    }
}
