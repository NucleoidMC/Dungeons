package xyz.nucleoid.dungeons.dungeons.game.scripting.quest.objectives;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Formatting;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.quest.Quest;
import xyz.nucleoid.dungeons.dungeons.game.scripting.quest.QuestObjective;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.widget.SidebarWidget;

public class SimpleObjective implements QuestObjective {
    private final String id;

    private SimpleObjective(String id) {
        this.id = id;
    }

    public static SimpleObjective create(MapTemplate template, CompoundTag data) throws ScriptTemplateInstantiationError {
        if (!data.contains("id")) {
            throw new ScriptTemplateInstantiationError("simple objective requires `id` field");
        }

        String id = data.getString("id");
        return new SimpleObjective(id);
    }

    @Override
    public void format(Quest quest, SidebarWidget.Content content) {
        content.writeFormattedTranslated(Formatting.AQUA, "quest.dungeons." + quest.id + ".objective." + this.id);
    }
}
