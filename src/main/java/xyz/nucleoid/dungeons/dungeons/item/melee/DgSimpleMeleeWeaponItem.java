package xyz.nucleoid.dungeons.dungeons.item.melee;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xyz.nucleoid.dungeons.dungeons.util.item.model.DgItemModelRegistry;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemUtil;

public class DgSimpleMeleeWeaponItem extends DgMeleeWeaponItem {
    protected double attackDamage, attackSpeed;

    public DgSimpleMeleeWeaponItem(double attackDamage, double attackSpeed, Item proxy, Settings settings) {
        super(proxy, settings);
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
    }

    @Override
    public void registerModels() {
        DgItemModelRegistry.register(this, this.proxy, DgItemUtil.idPathOf(this));
    }

    @Override
    public double getMeleeDamage(ItemStack stack) {
        return this.attackDamage;
    }

    @Override
    public double getSwingSpeed(ItemStack stack) {
        return this.attackSpeed;
    }
}
