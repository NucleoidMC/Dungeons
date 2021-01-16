package xyz.nucleoid.dungeons.dungeons.item.ranged;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import xyz.nucleoid.dungeons.dungeons.item.base.DgMaterialItem;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.DgWeaponItemUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgWeaponMaterial;

public class DgMaterialBowItem<M extends Enum<M> & DgWeaponMaterial> extends DgBowItem implements DgMaterialItem<M> {
    protected DgMaterialComponent<M> materialComponent;
    private final double baseRangedDamage;
    private final int baseDrawTime;

    public DgMaterialBowItem(double baseRangedDamage, int baseDrawTime, DgMaterialComponent<M> materialComponent, Settings settings) {
        super(settings);
        this.baseRangedDamage = baseRangedDamage;
        this.baseDrawTime = baseDrawTime;
        this.materialComponent = materialComponent;
    }

    @Override
    public Text getName(ItemStack stack) {
        return DgItemUtil.nameOf(stack, materialComponent);
    }

    @Override
    public DgMaterialComponent<M> getMaterialComponent() {
        return materialComponent;
    }

    public ItemStack createStack(M material, DgItemQuality quality) {
        return DgWeaponItemUtil.initMaterialWeapon(DgWeaponItemUtil.weaponBuilder(this).build(), material, quality);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        DgItemUtil.appendMaterialStacks(group, stacks, materialComponent, this::createStack);
    }

    @Override
    public double getRangedDamage(ItemStack stack) {
        return DgWeaponItemUtil.getDamage(stack, baseRangedDamage, materialComponent);
    }

    @Override
    public int getDrawTime(ItemStack stack) {
        return baseDrawTime;
    }
}
