package xyz.nucleoid.dungeons.dungeons.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.loot.DgArmorLoot;

public class GiveArmorCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("givearmor")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("random").then(CommandManager.argument("dungeonLevel", DoubleArgumentType.doubleArg()).executes(context -> giveRandom(context.getSource(), DoubleArgumentType.getDouble(context, "dungeonLevel")))));
        for (DgItemQuality quality : DgItemQuality.values()) {
            LiteralArgumentBuilder<ServerCommandSource> qualityNode = CommandManager.literal(quality.getId());
            root.then(qualityNode);
        }
        dispatcher.register(root);
    }

    private static int giveRandom(ServerCommandSource source, double dungeonLevel) throws CommandSyntaxException {
        return give(source, DgArmorLoot.generate(source.getWorld().getRandom(), dungeonLevel));
    }

    private static int give(ServerCommandSource source, ItemStack stack) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        player.getInventory().offerOrDrop(stack);
        source.sendFeedback(new TranslatableText("commands.give.success.single", 1, stack.toHoverableText(), player.getDisplayName()), true);
        return 1;
    }
}
