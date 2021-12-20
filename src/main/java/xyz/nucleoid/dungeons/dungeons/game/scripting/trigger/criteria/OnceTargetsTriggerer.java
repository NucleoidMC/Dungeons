package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.criteria;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.TriggerCriterion;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;

import java.util.List;

public class OnceTargetsTriggerer implements TriggerCriterion {
    public static OnceTargetsTriggerer create(@Nullable NbtCompound data) {
        return new OnceTargetsTriggerer();
    }

    @Override
    public TestResult testForPlayers(DgActive active, List<OnlineParticipant> playersInside) {
        return new TestResult(playersInside, true);
    }
}
