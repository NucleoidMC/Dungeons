package xyz.nucleoid.dungeons.dungeons;

import net.fabricmc.api.ModInitializer;
import xyz.nucleoid.plasmid.game.GameType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.dungeons.dungeons.game.DgConfig;
import xyz.nucleoid.dungeons.dungeons.game.DgWaiting;

public class Dungeons implements ModInitializer {

    public static final String ID = "dungeons";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GameType<DgConfig> TYPE = GameType.register(
            new Identifier(ID, "dungeons"),
            DgWaiting::open,
            DgConfig.CODEC
    );

    @Override
    public void onInitialize() {}
}
