package xyz.nucleoid.dungeons.dungeons.item.ranged;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import xyz.nucleoid.dungeons.dungeons.item.base.DgMaterialItem;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgRangedWeaponMaterial;

public class DgMaterialBowItem<M extends Enum<M> & DgRangedWeaponMaterial> extends DgBowItem implements DgMaterialItem<M> {
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
        return DgItemUtil.initRangedMaterialWeapon(DgItemUtil.weaponBuilder(this).build(), material, quality, baseRangedDamage, baseDrawTime);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        DgItemUtil.appendMaterialStacks(group, stacks, materialComponent, this::createStack);
    }

    @Override
    public double getRangedDamage(ItemStack stack) {
        return DgItemUtil.rangedDamageOf(stack);
    }

    @Override
    public int getDrawTime(ItemStack stack) {
        return DgItemUtil.drawTimeOf(stack);
    }
}
