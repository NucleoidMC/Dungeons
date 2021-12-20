package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;

@FunctionalInterface
public interface TriggerCriterionBuilder {
    TriggerCriterion create(@Nullable NbtCompound data) throws ScriptTemplateInstantiationError;
}
