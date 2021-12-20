package xyz.nucleoid.dungeons.dungeons.game.scripting.quest.objectives;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import xyz.nucleoid.dungeons.dungeons.Dungeons;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.quest.Quest;
import xyz.nucleoid.dungeons.dungeons.game.scripting.quest.QuestObjective;
import xyz.nucleoid.map_templates.MapTemplate;

public record SimpleObjective(String id) implements QuestObjective {

    public static SimpleObjective create(MapTemplate template, NbtCompound data) throws ScriptTemplateInstantiationError {
        if (!data.contains("id")) {
            throw new ScriptTemplateInstantiationError("simple objective requires `id` field");
        }

        String id = data.getString("id");
        return new SimpleObjective(id);
    }

    @Override
    public Text format(Quest quest) {
        String key = "quest." + Dungeons.ID + "." + quest.id + ".objective." + this.id;
        return new TranslatableText(key).formatted(Formatting.AQUA);
    }
}
