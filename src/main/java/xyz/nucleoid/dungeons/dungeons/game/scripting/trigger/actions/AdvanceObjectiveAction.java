package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptingUtil;
import xyz.nucleoid.dungeons.dungeons.game.scripting.quest.*;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.List;

public record AdvanceObjectiveAction(QuestObjective objective,
                                     String objectiveId) implements Action {

    public static AdvanceObjectiveAction create(MapTemplate template, TemplateRegion trigger, NbtCompound data) throws ScriptTemplateInstantiationError {
        if (!data.contains("objective")) {
            throw new ScriptTemplateInstantiationError("Advance objective action requires `objective`");
        }

        NbtCompound objectiveData = data.getCompound("objective");

        String typeStr = "simple";
        if (objectiveData.contains("type")) {
            typeStr = objectiveData.getString("type");
        }

        if (!objectiveData.contains("id")) {
            throw new ScriptTemplateInstantiationError("Advance objective action objective requires `id`");
        }

        Identifier type = ScriptingUtil.parseDungeonsDefaultId(typeStr);
        QuestObjectiveBuilder builder = QuestManager.OBJECTIVE_BUILDERS.get(type);

        if (builder == null) {
            throw new ScriptTemplateInstantiationError("Invalid objective type `" + type + "`");
        }

        return new AdvanceObjectiveAction(builder.create(template, objectiveData), objectiveData.getString("id"));
    }

    @Override
    public void execute(DgActive active, List<OnlineParticipant> targets) {
        Quest current = active.questManager.getCurrentQuest();
        if (current == null) {
            throw new IllegalStateException("Attempted to advance to a new objective while no quest is active!");
        }

        try {
            current.advanceTo(this.objectiveId, this.objective);
        } catch (QuestException e) {
            throw new RuntimeException(e.reason);
        }

        for (OnlineParticipant participant : targets) {
            participant.entity().playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }
}
