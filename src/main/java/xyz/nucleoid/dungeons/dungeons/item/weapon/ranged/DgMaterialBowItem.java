package xyz.nucleoid.dungeons.dungeons.item.weapon.ranged;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import xyz.nucleoid.dungeons.dungeons.item.DgMaterialItem;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.weapon.DgWeaponItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.item.material.DgWeaponMaterial;

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
        return DgItemUtil.nameOf(stack, this.materialComponent);
    }

    @Override
    public DgMaterialComponent<M> getMaterialComponent() {
        return this.materialComponent;
    }

    public ItemStack createStack(M material, DgItemQuality quality) {
        return DgWeaponItemUtil.initMaterialWeapon(DgWeaponItemUtil.weaponBuilder(this).build(), material, quality);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        DgItemUtil.appendMaterialStacks(group, stacks, this.materialComponent, this::createStack);
    }

    @Override
    public double getRangedDamage(ItemStack stack) {
        return DgWeaponItemUtil.getDamage(stack, this.baseRangedDamage, this.materialComponent);
    }

    @Override
    public int getDrawTime(ItemStack stack) {
        return this.baseDrawTime;
    }
}
