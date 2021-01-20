package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.DgPlayer;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerCriterion;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OnceTargetsAll implements TriggerCriterion {
    public static OnceTargetsAll create(@Nullable CompoundTag data) {
        return new OnceTargetsAll();
    }

    @Override
    public TestResult testForPlayers(DgActive active, List<OnlineParticipant> playersInside) {
        List<OnlineParticipant> all = new ArrayList<>();
        ServerWorld world = active.gameSpace.getWorld();

        for (Map.Entry<PlayerRef, DgPlayer> entry : active.participants.entrySet()) {
            entry.getKey().ifOnline(world, p -> all.add(new OnlineParticipant(entry.getValue(), p)));
        }

        return new TestResult(all, true);
    }
}
