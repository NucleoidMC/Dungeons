package xyz.nucleoid.dungeons.dungeons.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.TranslatableText;
import xyz.nucleoid.dungeons.dungeons.item.items.DgItems;
import xyz.nucleoid.dungeons.dungeons.item.DgMaterialItem;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.loot.DgWeaponLoot;

public class GiveWeaponCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("giveweapon")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("random").then(CommandManager.argument("dungeonLevel", DoubleArgumentType.doubleArg()).executes(context -> giveRandom(context.getSource(), DoubleArgumentType.getDouble(context, "dungeonLevel")))));
        for (DgItemQuality quality : DgItemQuality.values()) {
            LiteralArgumentBuilder<ServerCommandSource> qualityNode = CommandManager.literal(quality.getId());
            addRegistryItems(quality, qualityNode);
            root.then(qualityNode);
        }
        dispatcher.register(root);
    }

    private static void addRegistryItems(DgItemQuality quality, LiteralArgumentBuilder<ServerCommandSource> builder) {
        for (Item item : DgItems.getItems()) {
            if (item instanceof DgMaterialItem) {
                LiteralArgumentBuilder<ServerCommandSource> node = CommandManager.literal(DgItemUtil.idPathOf(item));
                DgMaterialComponent<?> materialComponent = ((DgMaterialItem<?>) item).getMaterialComponent();
                materialComponent.streamMaterials().forEach(material -> {
                    if (quality.ordinal() >= material.getMinQuality().ordinal() && quality.ordinal() <= material.getMaxQuality().ordinal()) {
                        node.then(CommandManager.literal(material.getId()).executes(context -> give(context.getSource(),
                                ((DgMaterialItem<?>) item).createStackInternal(material, quality))));
                    }
                });
                builder.then(node);
            }
        }
    }

    private static int giveRandom(ServerCommandSource source, double dungeonLevel) throws CommandSyntaxException {
        return give(source, DgWeaponLoot.generate(source.getWorld().getRandom(), dungeonLevel));
    }

    private static int give(ServerCommandSource source, ItemStack stack) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();
        player.getInventory().offerOrDrop(stack);
        source.sendFeedback(new TranslatableText("commands.give.success.single", 1, stack.toHoverableText(), player.getDisplayName()), true);
        return 1;
    }
}
