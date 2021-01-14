package xyz.nucleoid.dungeons.dungeons.util.item.material;

import java.util.Random;

public interface DgMaterialPicker<M extends DgMaterial> {
    M randomlyPick(Random random, double meanLevel);
}
