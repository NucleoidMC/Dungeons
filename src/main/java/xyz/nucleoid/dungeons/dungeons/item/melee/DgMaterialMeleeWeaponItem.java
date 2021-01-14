package xyz.nucleoid.dungeons.dungeons.item.melee;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.item.base.DgMaterialItem;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemModelRegistry;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.util.item.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMeleeWeaponMaterial;
import xyz.nucleoid.dungeons.dungeons.util.item.material.DgMeleeWeaponMetal;

public class DgMaterialMeleeWeaponItem<M extends Enum<M> & DgMeleeWeaponMaterial> extends DgMeleeWeaponItem implements DgMaterialItem<M> {
    protected DgMaterialComponent<M> materialComponent;
    private final double baseAttackDamage, baseAttackSpeed;

    public DgMaterialMeleeWeaponItem(double baseAttackDamage, double baseAttackSpeed, DgMaterialComponent<M> materialComponent, Item proxy, Settings settings) {
        super(proxy, settings);
        this.baseAttackDamage = baseAttackDamage;
        this.baseAttackSpeed = baseAttackSpeed;
        this.materialComponent = materialComponent;
    }

    @Override
    public Text getName(ItemStack stack) {
        return DgItemUtil.nameOf(stack, materialComponent);
    }

    @Override
    public ItemStack createStack(M material, DgItemQuality quality) {
        return DgItemUtil.initMeleeMaterialWeapon(DgItemUtil.weaponBuilder(this).build(), material, quality, baseAttackDamage, baseAttackSpeed);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        DgItemUtil.appendMaterialStacks(group, stacks, materialComponent, this::createStack);
    }

    @Override
    public DgMaterialComponent<M> getMaterialComponent() {
        return materialComponent;
    }

    @Override
    public void registerModels() {
        for (M material : materialComponent.getMaterials()) {
            DgItemModelRegistry.register(this, asProxy(), material.getId(), Registry.ITEM.getId(this).getPath());
        }
    }

    @Override
    public double getMeleeDamage(ItemStack stack) {
        return DgItemUtil.meleeDamageOf(stack);
    }

    @Override
    public double getSwingSpeed(ItemStack stack) {
        return DgItemUtil.swingSpeedOf(stack);
    }
}
