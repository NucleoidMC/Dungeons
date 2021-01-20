package xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.actions;

import cal.codes.larynx.Larynx;
import cal.codes.larynx.resources.TrackRegistry;
import cal.codes.larynx.tracks.DialogueTrack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import xyz.nucleoid.dungeons.dungeons.Dungeons;
import xyz.nucleoid.dungeons.dungeons.game.DgActive;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptTemplateInstantiationError;
import xyz.nucleoid.dungeons.dungeons.game.scripting.ScriptingUtil;
import xyz.nucleoid.dungeons.dungeons.game.scripting.trigger.Action;
import xyz.nucleoid.dungeons.dungeons.util.OnlineParticipant;
import xyz.nucleoid.plasmid.map.template.MapTemplate;
import xyz.nucleoid.plasmid.map.template.TemplateRegion;

import java.util.List;

public class DialogTrackAction implements Action {

    private final Identifier identifier;
    private final DialogueTrack dialog;

    public DialogTrackAction(Identifier identifier) {
        this.identifier = identifier;
        this.dialog = TrackRegistry.INSTANCE.get(identifier);
    }

    public static DialogTrackAction create(MapTemplate template, TemplateRegion trigger, CompoundTag data) throws ScriptTemplateInstantiationError {
        if(!data.contains("id")) throw new ScriptTemplateInstantiationError("id is required for dungeons:dialogtrack");
        Identifier id = ScriptingUtil.parseDungeonsDefaultId(data.getString("id"));
        if(id == null) throw new ScriptTemplateInstantiationError("id must be a valid Identifier for dungeons:dialogtrack");
        return new DialogTrackAction(id);
    }

    @Override
    public void execute(DgActive active, List<OnlineParticipant> targets) {
        targets.forEach(onlineParticipant -> dialog.play(onlineParticipant.entity));
    }
}
