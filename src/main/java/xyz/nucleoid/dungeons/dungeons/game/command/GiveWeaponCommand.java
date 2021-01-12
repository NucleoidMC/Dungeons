package xyz.nucleoid.dungeons.dungeons.game.command;

import com.mojang.brigadier.CommandDispatcher;
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
import xyz.nucleoid.dungeons.dungeons.game.loot.DgLootGrade;
import xyz.nucleoid.dungeons.dungeons.game.loot.weapon.*;

public class GiveWeaponCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> root = CommandManager.literal("giveweapon")
                .requires(source -> source.hasPermissionLevel(2))
                .then(CommandManager.literal("random").executes(context -> giveRandom(context.getSource())));
        for (DgLootGrade grade : DgLootGrade.values()) {
            LiteralArgumentBuilder<ServerCommandSource> gradeNode = CommandManager.literal(grade.id);
            addBows(grade, gradeNode);
            addMetalMeleeWeapons(grade, gradeNode);
            addQuarterstaves(grade, gradeNode);
            root.then(gradeNode);
        }
        dispatcher.register(root);
    }

    private static void addBows(DgLootGrade grade, LiteralArgumentBuilder<ServerCommandSource> builder) {
        for (DgRangedWeaponType type : DgRangedWeaponType.values()) {
            LiteralArgumentBuilder<ServerCommandSource> node = CommandManager.literal(type.id);
            for (DgRangedWeaponMaterial material : DgRangedWeaponMaterial.values()) {
                node.then(CommandManager.literal(material.id).executes(context -> give(context.getSource(),
                        new DgRangedWeapon(type, grade, material,1.0D, 1).toItemStack())));
            }
            builder.then(node);
        }
    }

    private static void addMetalMeleeWeapons(DgLootGrade grade, LiteralArgumentBuilder<ServerCommandSource> builder) {
        for (DgMetalMeleeWeaponType type : DgMetalMeleeWeaponType.values()) {
            LiteralArgumentBuilder<ServerCommandSource> node = CommandManager.literal(type.id);
            for (DgWeaponMetal material : DgWeaponMetal.values()) {
                node.then(CommandManager.literal(material.id).executes(context -> give(context.getSource(),
                        new DgMetalMeleeWeapon(type, material, grade, "Forged with /giveweapon", 1.0D, 1.0D).toItemStack())));
            }
            builder.then(node);
        }
    }

    private static void addQuarterstaves(DgLootGrade grade, LiteralArgumentBuilder<ServerCommandSource> builder) {
        LiteralArgumentBuilder<ServerCommandSource> node = CommandManager.literal("quarterstaff");
        for (DgQuarterstaffWood material : DgQuarterstaffWood.values()) {
            node.then(CommandManager.literal(material.id).executes(context -> give(context.getSource(),
                    new DgQuarterstaff(material, grade, 1.0D, 1.0D).toItemStack())));
        }
        builder.then(node);
    }

    private static int giveRandom(ServerCommandSource source) throws CommandSyntaxException {
        return give(source, DgWeaponGenerator.generate(source.getWorld().random, 1));
    }

    private static int give(ServerCommandSource source, ItemStack stack) throws CommandSyntaxException {
        ServerPlayerEntity player = source.getPlayer();

        boolean bl = player.inventory.insertStack(stack);
        ItemEntity itemEntity;
        if (bl && stack.isEmpty()) {
            itemEntity = player.dropItem(stack, false);
            if (itemEntity != null) {
                itemEntity.setDespawnImmediately();
            }

            player.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.playerScreenHandler.sendContentUpdates();
        } else {
            itemEntity = player.dropItem(stack, false);
            if (itemEntity != null) {
                itemEntity.resetPickupDelay();
                itemEntity.setOwner(player.getUuid());
            }
        }

        source.sendFeedback(new TranslatableText("commands.give.success.single", 1, stack.toHoverableText(), player.getDisplayName()), true);

        return 1;
    }
}
