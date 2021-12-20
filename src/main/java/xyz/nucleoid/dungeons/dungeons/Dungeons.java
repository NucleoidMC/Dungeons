package xyz.nucleoid.dungeons.dungeons;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.dungeons.dungeons.assets.DgModelGenerator;
import xyz.nucleoid.dungeons.dungeons.entity.attribute.DgEntityAttributes;
import xyz.nucleoid.dungeons.dungeons.game.DgConfig;
import xyz.nucleoid.dungeons.dungeons.game.DgWaiting;
import xyz.nucleoid.dungeons.dungeons.game.command.GiveWeaponCommand;
import xyz.nucleoid.dungeons.dungeons.item.DgItems;
import xyz.nucleoid.plasmid.game.GameType;

public class Dungeons implements ModInitializer {

    public static final String ID = "dungeons";
    public static final Logger LOGGER = LogManager.getLogger(ID);
    private static final boolean GENERATE_MODELS = true;

    public static final GameType<DgConfig> TYPE = GameType.register(
            new Identifier(ID, "dungeons"),
            DgConfig.CODEC,
            DgWaiting::open
    );

    @Override
    public void onInitialize() {
        DgItems.register();
        DgEntityAttributes.register();
        if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
            CommandRegistrationCallback.EVENT.register((dispatcher, b) -> GiveWeaponCommand.register(dispatcher));
            if (GENERATE_MODELS) {
                DgModelGenerator.generateModels();
            }
        }
    }
}
