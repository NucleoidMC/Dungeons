package xyz.nucleoid.dungeons.dungeons.entity.attribute;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.Dungeons;
import xyz.nucleoid.dungeons.dungeons.util.DgTranslationUtil;

public class DgEntityAttributes {
    public static final EntityAttribute GENERIC_RANGED_DAMAGE = register("generic.ranged_damage", (new ClampedEntityAttribute(translationKeyOf("generic.ranged_damage"), 0.0D, 0.0D, 2048.0D)));
    public static final EntityAttribute GENERIC_DRAW_TIME = register("generic.draw_time", (new ClampedEntityAttribute(translationKeyOf("generic.draw_time"), 20.0D, 0.0D, 2048.0D)));

    public static void register() {
        FabricDefaultAttributeRegistry.register(EntityType.PLAYER, PlayerEntity.createPlayerAttributes().add(GENERIC_RANGED_DAMAGE).add(GENERIC_DRAW_TIME));
    }

    private static <T extends EntityAttribute> T register(String id, T attribute) {
        Registry.register(Registry.ATTRIBUTE, new Identifier(Dungeons.ID, id), attribute);
        return attribute;
    }

    public static String translationKeyOf(String name) {
        return DgTranslationUtil.translationKeyOf("attribute", name);
    }
}
