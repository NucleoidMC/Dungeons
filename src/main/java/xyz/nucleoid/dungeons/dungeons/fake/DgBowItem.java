package xyz.nucleoid.dungeons.dungeons.fake;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgRangedWeapon;
import xyz.nucleoid.plasmid.fake.FakeItem;

import java.util.function.Consumer;

public class DgBowItem extends BowItem implements FakeItem {
    public DgBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        // From BowItem.onStoppedUsing

        if (user instanceof PlayerEntity) {
            PlayerEntity playerEntity = (PlayerEntity)user;
            boolean bl = playerEntity.abilities.creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
            ItemStack itemStack = playerEntity.getArrowType(stack);
            if (!itemStack.isEmpty() || bl) {
                if (itemStack.isEmpty()) {
                    itemStack = new ItemStack(Items.ARROW);
                }

                int i = this.getMaxUseTime(stack) - remainingUseTicks;

                // BEGIN MODIFICATIONS

                int fullDrawTicks = 20;
                CompoundTag tag = stack.getOrCreateTag();
                if (tag.contains(DgRangedWeapon.DRAW_TICKS_TAG)) {
                    fullDrawTicks = tag.getInt(DgRangedWeapon.DRAW_TICKS_TAG);
                }

                float f = dgGetPullProgress(i, fullDrawTicks);

                // END MODIFICATIONS

                if ((double)f >= 0.1D) {
                    boolean bl2 = bl && itemStack.getItem() == Items.ARROW;
                    if (!world.isClient) {
                        ArrowItem arrowItem = (ArrowItem)((ArrowItem)(itemStack.getItem() instanceof ArrowItem ? itemStack.getItem() : Items.ARROW));
                        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, itemStack, playerEntity);
                        persistentProjectileEntity.setProperties(playerEntity, playerEntity.pitch, playerEntity.yaw, 0.0F, f * 3.0F, 1.0F);

                        // BEGIN MODIFICATIONS

                        double damage = persistentProjectileEntity.getDamage();

                        // Vanilla is 6.0 for full draw without critical hit by using a 2.0 damage value combined with
                        // everything else. Here we just multiply that 2.0 damage value by (target / vanilla).
                        // Hopefully, it will work. - Restioson

                        double vanillaDamage = 6.0;
                        double targetDamage = vanillaDamage;
                        if (tag.contains(DgRangedWeapon.DRAW_TICKS_TAG)) {
                            targetDamage = tag.getInt(DgRangedWeapon.DAMAGE_TAG);
                        }

                        persistentProjectileEntity.setDamage(damage * (targetDamage / vanillaDamage));

                        // END MODIFICATIONS

                        if (f == 1.0F) {
                            persistentProjectileEntity.setCritical(true);
                        }

                        int j = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                        if (j > 0) {
                            persistentProjectileEntity.setDamage(persistentProjectileEntity.getDamage() + (double)j * 0.5D + 0.5D);
                        }

                        int k = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                        if (k > 0) {
                            persistentProjectileEntity.setPunch(k);
                        }

                        if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                            persistentProjectileEntity.setOnFireFor(100);
                        }

                        stack.damage(1, (LivingEntity)playerEntity, (Consumer<LivingEntity>)((p) -> {
                            p.sendToolBreakStatus(playerEntity.getActiveHand());
                        }));
                        if (bl2 || playerEntity.abilities.creativeMode && (itemStack.getItem() == Items.SPECTRAL_ARROW || itemStack.getItem() == Items.TIPPED_ARROW)) {
                            persistentProjectileEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                        }

                        world.spawnEntity(persistentProjectileEntity);
                    }

                    world.playSound((PlayerEntity)null, playerEntity.getX(), playerEntity.getY(), playerEntity.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F / (RANDOM.nextFloat() * 0.4F + 1.2F) + f * 0.5F);
                    if (!bl2 && !playerEntity.abilities.creativeMode) {
                        itemStack.decrement(1);
                        if (itemStack.isEmpty()) {
                            playerEntity.inventory.removeOne(itemStack);
                        }
                    }

                    playerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
                }
            }
        }
    }

    public static float dgGetPullProgress(int useTicks, int fullDrawTicks) {
        float f = (float)useTicks / (float) fullDrawTicks;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public Item asProxy() {
        return Items.BOW;
    }
}
