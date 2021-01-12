package xyz.nucleoid.dungeons.dungeons;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.dungeons.dungeons.assets.DgModelGenerator;
import xyz.nucleoid.dungeons.dungeons.game.DgConfig;
import xyz.nucleoid.dungeons.dungeons.game.DgWaiting;
import xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgBow;
import xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgMetalMeleeWeapon;
import xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgQuarterstaff;
import xyz.nucleoid.plasmid.game.GameType;

public class Dungeons implements ModInitializer {

    public static final String ID = "dungeons";
    public static final Logger LOGGER = LogManager.getLogger(ID);
    private static final boolean DOING_MODEL_STUFF = false;

    public static final GameType<DgConfig> TYPE = GameType.register(
            new Identifier(ID, "dungeons"),
            DgWaiting::open,
            DgConfig.CODEC
    );

    @Override
    public void onInitialize() {
        if (DOING_MODEL_STUFF) {
            Dungeons.LOGGER.info("===============================================================================================");
            Dungeons.LOGGER.info("DUNGEONS MODEL IDS:");
            Dungeons.LOGGER.info("===============================================================================================");
            DgBow.registerModels();
            DgMetalMeleeWeapon.registerModels();
            DgQuarterstaff.registerModels();
            Dungeons.LOGGER.info("===============================================================================================");
            DgModelGenerator.generateModelsShittily();
        }
    }
}
