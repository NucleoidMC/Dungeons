package xyz.nucleoid.dungeons.dungeons.loot;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class DgEnemyDropGenerator {
    public static @Nullable ItemStack generate(Random random, double dungeonLevel) {
        double rand = random.nextDouble();

        if (rand < 0.4) { // 40% of mobs will drop things...

            rand /= 0.5; // Normalise for ease of understanding

            if (rand < 0.2) { // 20% of all drops are weapons...
                return DgWeaponLoot.generate(random, dungeonLevel);
            } else if (rand < 0.4) {
                return DgArmorLoot.generate(random, dungeonLevel);
            } else { // The rest (60%) are consumables
                return DgConsumable.choose(random).asItemStack();
            }
        } else {
            // No such luck :(
            return null;
        }
    }
}
