package xyz.nucleoid.dungeons.dungeons.item.melee;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xyz.nucleoid.dungeons.dungeons.item.base.DgMeleeWeapon;
import xyz.nucleoid.dungeons.dungeons.item.base.DgModelProvider;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemUtil;
import xyz.nucleoid.plasmid.fake.FakeItem;

public abstract class DgMeleeWeaponItem extends Item implements FakeItem, DgMeleeWeapon, DgModelProvider {
    private final Item proxy;

    public DgMeleeWeaponItem(Item proxy, Settings settings) {
        super(settings.maxCount(1));
        this.proxy = proxy;
    }

    public ItemStack createStack(DgItemQuality quality) {
        return DgItemUtil.initWeapon(DgItemUtil.weaponBuilder(this).build(), quality);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        for (DgItemQuality quality : DgItemQuality.values()) {
            if (group == quality.getItemGroup()) {
                stacks.add(createStack(quality));
            }
        }
    }

    @Override
    public Text getName(ItemStack stack) {
        DgItemQuality quality = DgItemUtil.qualityOf(stack);
        return new TranslatableText(getTranslationKey(), quality.getId());
    }

    @Override
    public Item asProxy() {
        return proxy;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }
}
