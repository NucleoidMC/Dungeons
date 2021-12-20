package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.quest.Quest;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.map_templates.MapTemplate;
import xyz.nucleoid.map_templates.TemplateRegion;

import java.util.List;

public class SetNewQuestAction implements Action {
    private final String id;

    private SetNewQuestAction(String id) {
        this.id = id;
    }

    public static SetNewQuestAction create(MapTemplate template, TemplateRegion trigger, NbtCompound data) throws ScriptTemplateInstantiationError {
        if (!data.contains("id")) {
            throw new ScriptTemplateInstantiationError("Set new quest action requires `id`");
        }

        return new SetNewQuestAction(data.getString("id"));
    }

    @Override
    public void execute(DgActive active, List<OnlineParticipant> targets) {
        // TODO(quests): handle the old quest being removed when it is needed
        active.questManager.setCurrentQuest(new Quest(this.id));

        for (OnlineParticipant participant : targets) {
            participant.entity.playSound(SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }
}
