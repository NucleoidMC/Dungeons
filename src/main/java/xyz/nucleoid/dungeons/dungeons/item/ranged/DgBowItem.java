package xyz.nucleoid.dungeons.dungeons.item.ranged;

import eu.pb4.polymer.api.item.PolymerItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.dungeons.dungeons.entity.attribute.DgEntityAttributes;
import xyz.nucleoid.dungeons.dungeons.item.base.DgRangedWeapon;

public abstract class DgBowItem extends BowItem implements PolymerItem, DgRangedWeapon {
    public DgBowItem(Settings settings) {
        super(settings.maxCount(1));
    }

    @Override
    public void onStoppedUsing(ItemStack bowStack, World world, LivingEntity user, int remainingUseTicks) {
        boolean bl2;
        int i;
        float f;

        if (!(user instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity player = (PlayerEntity)user;
        boolean bl = player.getAbilities().creativeMode || EnchantmentHelper.getLevel(Enchantments.INFINITY, bowStack) > 0;
        ItemStack arrowStack = player.getArrowType(bowStack);

        // BEGIN MODIFICATIONS
        boolean isPotionArrow = arrowStack.getItem() == Items.TIPPED_ARROW ||
                !PotionUtil.getCustomPotionEffects(arrowStack).isEmpty() ||
                !PotionUtil.getPotionEffects(arrowStack).isEmpty();
        // END MODIFICATIONS

        if (arrowStack.isEmpty() && !bl) {
            return;
        }

        if (arrowStack.isEmpty()) {
            arrowStack = new ItemStack(Items.ARROW);
        }

        if ((double)(f = BowItem.getPullProgress(i = this.getMaxUseTime(bowStack) - remainingUseTicks)) < 0.1) {
            return;
        }

        boolean bl3 = bl2 = bl && arrowStack.isOf(Items.ARROW);

        if (!world.isClient) {
            int k;
            int j;
            ArrowItem arrowItem = (ArrowItem)(arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
            PersistentProjectileEntity arrowEntity = arrowItem.createArrow(world, arrowStack, player);
            arrowEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0f, f * 3.0f, 1.0f);
            if (f == 1.0f) {
                arrowEntity.setCritical(true);
            }

            // BEGIN MODIFICATIONS
            double damage = arrowEntity.getDamage();

            // Vanilla is 6.0 for full draw without critical hit by using a 2.0 damage value combined with
            // everything else. Here we just multiply that 2.0 damage value by (target / vanilla).
            // Hopefully, it will work. - Restioson

            double vanillaDamage = 6.0;
            double targetDamage = user.getAttributeValue(DgEntityAttributes.GENERIC_RANGED_DAMAGE);
            arrowEntity.setDamage(damage * (targetDamage / vanillaDamage));
            // END MODIFICATIONS

            if ((j = EnchantmentHelper.getLevel(Enchantments.POWER, bowStack)) > 0) {
                arrowEntity.setDamage(arrowEntity.getDamage() + (double)j * 0.5 + 0.5);
            }
            if ((k = EnchantmentHelper.getLevel(Enchantments.PUNCH, bowStack)) > 0) {
                arrowEntity.setPunch(k);
            }
            if (EnchantmentHelper.getLevel(Enchantments.FLAME, bowStack) > 0) {
                arrowEntity.setOnFireFor(100);
            }
            bowStack.damage(1, player, p -> p.sendToolBreakStatus(player.getActiveHand()));

            // BEGIN MODIFICATIONS
            if (isPotionArrow) {
                arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
            } else {
                arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
            }
            // END MODIFICATIONS

            if (bl2 || player.getAbilities().creativeMode && (arrowStack.isOf(Items.SPECTRAL_ARROW) || arrowStack.isOf(Items.TIPPED_ARROW))) {
                arrowEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }
            world.spawnEntity(arrowEntity);
        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.4f + 1.2f) + f * 0.5f);

        // BEGIN MODIFICATIONS
        if (!bl2 && !player.getAbilities().creativeMode) {
            if (isPotionArrow) {
                arrowStack.decrement(1);
                if (arrowStack.isEmpty()) {
                    player.getInventory().removeOne(arrowStack);
                }
            } else if (player instanceof ServerPlayerEntity) {
                int slot = player.getInventory().getSlotWithStack(arrowStack);
                ((ServerPlayerEntity) player).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, 0, slot, arrowStack));
            }
        }
        // END MODIFICATIONS

        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        Entity holder = stack.getHolder();

        if (holder instanceof LivingEntity) {
            return (int) ((LivingEntity) holder).getAttributeValue(DgEntityAttributes.GENERIC_DRAW_TIME);
        } else {
            return super.getMaxUseTime(stack);
        }
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return Items.BOW;
    }
}
