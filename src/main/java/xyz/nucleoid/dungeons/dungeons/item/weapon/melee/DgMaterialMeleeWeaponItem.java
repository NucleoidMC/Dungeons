package xyz.nucleoid.dungeons.dungeons.item.weapon.melee;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import xyz.nucleoid.dungeons.dungeons.item.DgMaterialItem;
import xyz.nucleoid.dungeons.dungeons.item.DgItemQuality;
import xyz.nucleoid.dungeons.dungeons.item.DgItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.weapon.DgWeaponItemUtil;
import xyz.nucleoid.dungeons.dungeons.item.material.DgMaterialComponent;
import xyz.nucleoid.dungeons.dungeons.item.material.DgWeaponMaterial;
import xyz.nucleoid.dungeons.dungeons.item.model.DgItemModelRegistry;

public class DgMaterialMeleeWeaponItem<M extends Enum<M> & DgWeaponMaterial> extends DgMeleeWeaponItem implements DgMaterialItem<M> {
    protected DgMaterialComponent<M> materialComponent;
    private final double baseMeleeDamage, baseSwingSpeed;

    public DgMaterialMeleeWeaponItem(double baseMeleeDamage, double baseSwingSpeed, DgMaterialComponent<M> materialComponent, Item proxy, Settings settings) {
        super(proxy, settings);
        this.baseMeleeDamage = baseMeleeDamage;
        this.baseSwingSpeed = baseSwingSpeed;
        this.materialComponent = materialComponent;
    }

    @Override
    public Text getName(ItemStack stack) {
        return DgItemUtil.nameOf(stack, this.materialComponent);
    }

    @Override
    public ItemStack createStack(M material, DgItemQuality quality) {
        return DgWeaponItemUtil.initMaterialWeapon(DgWeaponItemUtil.weaponBuilder(this).build(), material, quality);
    }

    @Override
    public void appendStacks(ItemGroup group, DefaultedList<ItemStack> stacks) {
        DgItemUtil.appendMaterialStacks(group, stacks, this.materialComponent, this::createStack);
    }

    @Override
    public DgMaterialComponent<M> getMaterialComponent() {
        return this.materialComponent;
    }

    @Override
    public void registerModels() {
        for (M material : this.materialComponent.getMaterials()) {
            DgItemModelRegistry.register(this, this.proxy, material.getId(), Registry.ITEM.getId(this).getPath());
        }
    }

    @Override
    public double getMeleeDamage(ItemStack stack) {
        return DgWeaponItemUtil.getDamage(stack, this.baseMeleeDamage, this.materialComponent);
    }

    @Override
    public double getSwingSpeed(ItemStack stack) {
        return this.baseSwingSpeed;
    }
}
