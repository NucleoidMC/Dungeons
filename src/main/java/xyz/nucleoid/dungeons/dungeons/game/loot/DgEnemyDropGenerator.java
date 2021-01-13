package xyz.nucleoid.dungeons.dungeons.game.loot;

import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgWeaponGenerator;

import java.util.Random;

public class DgEnemyDropGenerator {
    public static @Nullable ItemStack generate(Random random, double dungeonLevel) {
        double rand = random.nextDouble();

        if (rand < 0.5) { //50% of mobs will drop things...

            rand /= 0.5; // Normalise for ease of understanding

            if (rand < 0.3) { // 30% of all drops are weapons...
                return DgWeaponGenerator.generate(random, dungeonLevel);
            } else { // The rest (70%) are consumables
                return DgConsumable.choose(random).asItemStack();
            }
        } else {
            // No such luck :(
            return null;
        }
    }
}
