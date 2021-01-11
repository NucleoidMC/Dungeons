package xyz.nucleoid.dungeons.dungeons.game.loot;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

import java.util.*;

public class DgWeapon {
    private static final UUID ATTACK_DAMAGE_MODIFIER_ID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    public DgWeaponType type;
    public DgLootGrade grade;
    public DgWeaponMetal metal; // TODO(restioson): how to do other weapons?
    public String flavourText;
    public double attackDamage;

    public DgWeapon(DgWeaponType type, DgLootGrade grade, DgWeaponMetal metal, String flavourText, double attackDamage) {
        this.type = type;
        this.grade = grade;
        this.metal = metal;
        this.flavourText = flavourText;
        this.attackDamage = attackDamage;
    }

    public static DgWeapon generate(Random random) {
        DgWeaponType type = DgWeaponType.choose(random);

        DgWeaponMetal skip = null;
        if (type == DgWeaponType.MACE) {
            skip = DgWeaponMetal.DAMASCUS;
        }
        DgWeaponMetal metal = DgWeaponMetal.choose(random, skip);

        DgLootGrade grade = DgLootGrade.chooseInRange(random, metal.minGrade, metal.maxGrade);
        double attackDamage = (type.baseDamage + (random.nextDouble() / 2)) * metal.damageModifier * grade.damageModifier;
        String flavourText = DgWeapon.generateFlavourText(random, grade, metal);

        return new DgWeapon(type, grade, metal, flavourText, attackDamage);
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
                .setName(new LiteralText(String.format("%s %s %s", this.grade.name, this.metal.name, this.type.name)))
                .addModifier(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", this.attackDamage, EntityAttributeModifier.Operation.ADDITION), EquipmentSlot.MAINHAND);

        DgWeapon.addLoreWrapped(builder, String.format("A %s %s made of %s.", this.grade.name.toLowerCase(), this.type.name.toLowerCase(), this.metal.name.toLowerCase()));
        DgWeapon.addLoreWrapped(builder, this.flavourText);

        return builder.build();
    }

    private static void addLoreWrapped(ItemStackBuilder builder, String text) {
        int maxLoreLength = 25;
        String[] split = text.split(" ");
        StringBuilder currentLine = new StringBuilder(split[0]);

        for (int idx = 1; idx < split.length; idx++) { // skip first word
            String word = split[idx];
            if (currentLine.length() + word.length() <= maxLoreLength) {
                currentLine.append(" ");
            } else {
                builder.addLore(new LiteralText(currentLine.toString()));
                currentLine.setLength(0);
            }
            currentLine.append(word);
        }

        builder.addLore(new LiteralText(currentLine.toString()));
    }
}
