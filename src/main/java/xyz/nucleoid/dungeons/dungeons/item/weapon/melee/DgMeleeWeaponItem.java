package xyz.nucleoid.dungeons.dungeons.item.weapon.melee;

import eu.pb4.polymer.api.item.PolymerItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.item.DgModelProvider;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.weapon.DgWeaponItemUtil;

public abstract class DgMeleeWeaponItem extends Item implements PolymerItem, DgMeleeWeapon, DgModelProvider {
    final Item proxy;

    public DgMeleeWeaponItem(Item proxy, Settings settings) {
        super(settings.maxCount(1));
        this.proxy = proxy;
    }

    @Override
    public abstract void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks);

    @Override
    public Text getName(ItemStack stack) {
        DgItemQuality quality = DgItemUtil.qualityOf(stack);
        return new TranslatableText(this.getTranslationKey(), quality.getId());
    }

    @Override
    public Item getPolymerItem(ItemStack stack, @Nullable ServerPlayerEntity player) {
        return this.proxy;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }
}
