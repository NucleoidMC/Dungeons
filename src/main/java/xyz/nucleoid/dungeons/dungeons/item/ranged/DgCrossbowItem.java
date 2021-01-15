package xyz.nucleoid.dungeons.dungeons.item.ranged;

import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.potion.PotionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.dungeons.dungeons.item.DgItems;
import xyz.nucleoid.dungeons.dungeons.item.base.DgRangedWeapon;
import xyz.nucleoid.dungeons.dungeons.util.item.DgItemUtil;
import xyz.nucleoid.plasmid.fake.FakeItem;

import java.util.List;

public abstract class DgCrossbowItem extends CrossbowItem implements FakeItem, DgRangedWeapon {
    public DgCrossbowItem(Settings settings) {
        super(settings.maxCount(1));
    }
    // arrow removal code removed

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = CrossbowItem.getPullProgress(i, stack);
        if (f >= 1.0F && !isCharged(stack) && DgCrossbowItem.dgLoadProjectiles(user, stack)) {
            setCharged(stack, true);
            SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundCategory, 1.0F, 1.0F / (RANDOM.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    public static boolean dgLoadProjectiles(LivingEntity shooter, ItemStack projectile) {
        int multishot = EnchantmentHelper.getLevel(Enchantments.MULTISHOT, projectile);
        int arrowCount = multishot == 0 ? 1 : 3;
        boolean creativeMode = shooter instanceof PlayerEntity && ((PlayerEntity) shooter).abilities.creativeMode;
        ItemStack arrowStack = shooter.getArrowType(projectile);
        ItemStack arrowStackCopy = arrowStack.copy();

        for (int k = 0; k < arrowCount; ++k) {
            if (k > 0) {
                arrowStack = arrowStackCopy.copy();
            }

            if (arrowStack.isEmpty() && creativeMode) {
                arrowStack = new ItemStack(Items.ARROW);
                arrowStackCopy = arrowStack.copy();
            }

            if (!dgLoadProjectile(shooter, projectile, arrowStack, k > 0, creativeMode)) {
                return false;
            }
        }

        return true;
    }

    private static boolean dgLoadProjectile(LivingEntity shooter, ItemStack crossbow, ItemStack projectile, boolean simulated, boolean creative) {
        if (projectile.isEmpty()) {
            return false;
        } else {
            boolean bl = creative && projectile.getItem() instanceof ArrowItem;

            // BEGIN MODIFICATIONS
            boolean isPotionArrow = projectile.getItem() == Items.TIPPED_ARROW ||
                    !PotionUtil.getCustomPotionEffects(projectile).isEmpty() ||
                    !PotionUtil.getPotionEffects(projectile).isEmpty();
            ItemStack itemStack2;
            if (!bl && !creative && !simulated) {
                if (isPotionArrow) {
                    itemStack2 = projectile.split(1);
                    if (projectile.isEmpty() && shooter instanceof PlayerEntity) {
                        ((PlayerEntity) shooter).inventory.removeOne(projectile);
                    }
                } else if (shooter instanceof ServerPlayerEntity) {
                    itemStack2 = projectile.copy();
                    int slot = ((ServerPlayerEntity) shooter).inventory.getSlotWithStack(projectile);
                    ((ServerPlayerEntity) shooter).networkHandler.sendPacket(new ScreenHandlerSlotUpdateS2CPacket(-2, slot, projectile));
                } else { // should never trigger
                    itemStack2 = projectile.copy();
                }
            } else {
                itemStack2 = projectile.copy();
            }
            // END MODIFICATIONS

            CrossbowItem.putProjectile(crossbow, itemStack2);
            return true;
        }
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (isCharged(itemStack)) {
            dgShootAll(world, user, hand, itemStack, CrossbowItem.getSpeed(itemStack), 1.0F);
            CrossbowItem.setCharged(itemStack, false);
            return TypedActionResult.consume(itemStack);
        } else if (!user.getArrowType(itemStack).isEmpty()) {
            if (!isCharged(itemStack)) {
                this.charged = false;
                this.loaded = false;
                user.setCurrentHand(hand);
            }

            return TypedActionResult.consume(itemStack);
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }

    public static void dgShootAll(World world, LivingEntity entity, Hand hand, ItemStack stack, float speed, float divergence) {
        List<ItemStack> list = CrossbowItem.getProjectiles(stack);
        float[] fs = CrossbowItem.getSoundPitches(entity.getRandom());

        for (int i = 0; i < list.size(); ++i) {
            ItemStack itemStack = (ItemStack) list.get(i);
            boolean bl = entity instanceof PlayerEntity && ((PlayerEntity) entity).abilities.creativeMode;
            if (!itemStack.isEmpty()) {
                if (i == 0) {
                    dgShoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 0.0F);
                } else if (i == 1) {
                    dgShoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, -10.0F);
                } else if (i == 2) {
                    dgShoot(world, entity, hand, stack, itemStack, fs[i], bl, speed, divergence, 10.0F);
                }
            }
        }

        CrossbowItem.postShoot(world, entity, stack);
    }

    private static void dgShoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated) {
        if (!world.isClient) {
            boolean bl = projectile.getItem() == Items.FIREWORK_ROCKET;
            ProjectileEntity projectileEntity2;
            if (bl) {
                projectileEntity2 = new FireworkRocketEntity(world, projectile, shooter, shooter.getX(), shooter.getEyeY() - 0.15000000596046448D, shooter.getZ(), true);
            } else {
                projectileEntity2 = dgCreateArrow(world, shooter, crossbow, projectile);

                // BEGIN MODIFICATIONS
                boolean isPotionArrow = projectile.getItem() == Items.TIPPED_ARROW ||
                        !PotionUtil.getCustomPotionEffects(projectile).isEmpty() ||
                        !PotionUtil.getPotionEffects(projectile).isEmpty();
                if (isPotionArrow) {
                    ((PersistentProjectileEntity) projectileEntity2).pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
                } else {
                    ((PersistentProjectileEntity) projectileEntity2).pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                }
                // END MODIFICATIONS

                if (creative || simulated != 0.0F) {
                    ((PersistentProjectileEntity) projectileEntity2).pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }
            }

            if (shooter instanceof CrossbowUser) {
                CrossbowUser crossbowUser = (CrossbowUser) shooter;
                crossbowUser.shoot(crossbowUser.getTarget(), crossbow, projectileEntity2, simulated);
            } else {
                Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
                Quaternion quaternion = new Quaternion(new Vector3f(vec3d), simulated, true);
                Vec3d vec3d2 = shooter.getRotationVec(1.0F);
                Vector3f vector3f = new Vector3f(vec3d2);
                vector3f.rotate(quaternion);
                projectileEntity2.setVelocity(vector3f.getX(), vector3f.getY(), vector3f.getZ(), speed, divergence);
            }

            crossbow.damage(bl ? 3 : 1, shooter, (e) -> {
                e.sendToolBreakStatus(hand);
            });
            world.spawnEntity(projectileEntity2);
            world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, SoundCategory.PLAYERS, 1.0F, soundPitch);
        }
    }

    private static PersistentProjectileEntity dgCreateArrow(World world, LivingEntity entity, ItemStack crossbow, ItemStack arrow) {
        ArrowItem arrowItem = (ArrowItem) (arrow.getItem() instanceof ArrowItem ? arrow.getItem() : Items.ARROW);
        PersistentProjectileEntity persistentProjectileEntity = arrowItem.createArrow(world, arrow, entity);

        // BEGIN MODIFICATIONS

        CompoundTag tag = arrow.getOrCreateTag();
        double damage = persistentProjectileEntity.getDamage();

        // Vanilla is 6.0 for full draw without critical hit by using a 2.0 damage value combined with
        // everything else. Here we just multiply that 2.0 damage value by (target / vanilla).
        // Hopefully, it will work. - Restioson

        double vanillaDamage = 6.0;
        double targetDamage = DgItemUtil.rangedDamageOf(crossbow);

        persistentProjectileEntity.setDamage(damage * (targetDamage / vanillaDamage));

        // END MODIFICATIONS

        if (entity instanceof PlayerEntity) {
            persistentProjectileEntity.setCritical(true);
        }

        persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
        persistentProjectileEntity.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, crossbow);
        if (i > 0) {
            persistentProjectileEntity.setPierceLevel((byte) i);
        }

        return persistentProjectileEntity;
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.getItem() == DgItems.CROSSBOW;
    }

    @Override
    public Item asProxy() {
        return Items.CROSSBOW;
    }
}
