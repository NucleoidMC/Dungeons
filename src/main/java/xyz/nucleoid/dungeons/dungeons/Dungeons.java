package xyz.nucleoid.dungeons.dungeons;

import net.fabricmc.api.ModInitializer;
import xyz.nucleoid.plasmid.game.GameType;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.dungeons.dungeons.game.DungeonsConfig;
import xyz.nucleoid.dungeons.dungeons.game.DungeonsWaiting;

public class Dungeons implements ModInitializer {

    public static final String ID = "dungeons";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final GameType<DungeonsConfig> TYPE = GameType.register(
            new Identifier(ID, "dungeons"),
            DungeonsWaiting::open,
            DungeonsConfig.CODEC
    );

    @Override
    public void onInitialize() {}
}
