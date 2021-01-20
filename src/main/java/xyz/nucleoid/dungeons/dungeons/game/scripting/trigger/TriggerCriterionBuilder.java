package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;

@FunctionalInterface
public interface TriggerCriterionBuilder {
    TriggerCriterion create(@Nullable CompoundTag data) throws ScriptTemplateInstantiationError;
}
