package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria;


import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerCriterion;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.plasmid.util.PlayerRef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class OncePerPlayer implements TriggerCriterion {
    private final HashSet<PlayerRef> hasRun = new HashSet<>();

    public static OncePerPlayer create(@Nullable NbtCompound data) {
        return new OncePerPlayer();
    }

    @Override
    public TestResult testForPlayers(DgActive active, List<OnlineParticipant> playersInside) {
        List<OnlineParticipant> runsFor = new ArrayList<>();

        for (OnlineParticipant participant : playersInside) {
            PlayerRef ref = PlayerRef.of(participant.entity());
            if (!this.hasRun.contains(ref)) {
                this.hasRun.add(ref);
                runsFor.add(participant);
            }
        }

        return new TestResult(runsFor, false);
    }
}
