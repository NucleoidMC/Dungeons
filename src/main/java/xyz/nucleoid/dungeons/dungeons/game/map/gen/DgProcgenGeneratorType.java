package xyz.nucleoid.dungeons.dungeons.game.map.gen;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringIdentifiable;

public enum DgProcgenGeneratorType implements StringIdentifiable {
    OVERGROWN("overgrown");

    public final String id;

    static Codec<DgProcgenGeneratorType> CODEC = StringIdentifiable.createCodec(DgProcgenGeneratorType::values, DgProcgenGeneratorType::fromString);

    DgProcgenGeneratorType(String id) {
        this.id = id;
    }

    public static DgProcgenGeneratorType fromString(String id) {
        switch (id) {
            case "overgrown":
                return OVERGROWN;
            default:
                return null;
        }
    }

    @Override
    public String asString() {
        return this.id;
    }
}
