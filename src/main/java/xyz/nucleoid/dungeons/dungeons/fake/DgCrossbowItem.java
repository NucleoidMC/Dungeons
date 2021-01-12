package xyz.nucleoid.dungeons.dungeons.fake;

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
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import xyz.nucleoid.dungeons.dungeons.game.loot.weapon.DgRangedWeapon;
import xyz.nucleoid.plasmid.fake.FakeItem;

import java.util.List;

public class DgCrossbowItem extends CrossbowItem implements FakeItem {
    public DgCrossbowItem(Settings settings) {
        super(settings);
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

        for(int i = 0; i < list.size(); ++i) {
            ItemStack itemStack = (ItemStack)list.get(i);
            boolean bl = entity instanceof PlayerEntity && ((PlayerEntity)entity).abilities.creativeMode;
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
                if (creative || simulated != 0.0F) {
                    ((PersistentProjectileEntity)projectileEntity2).pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }
            }

            if (shooter instanceof CrossbowUser) {
                CrossbowUser crossbowUser = (CrossbowUser)shooter;
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
        double targetDamage = vanillaDamage;
        if (tag.contains(DgRangedWeapon.DRAW_TICKS_TAG)) {
            targetDamage = tag.getInt(DgRangedWeapon.DAMAGE_TAG);
        }

        persistentProjectileEntity.setDamage(damage * (targetDamage / vanillaDamage));

        // END MODIFICATIONS

        if (entity instanceof PlayerEntity) {
            persistentProjectileEntity.setCritical(true);
        }

        persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
        persistentProjectileEntity.setShotFromCrossbow(true);
        int i = EnchantmentHelper.getLevel(Enchantments.PIERCING, crossbow);
        if (i > 0) {
            persistentProjectileEntity.setPierceLevel((byte)i);
        }

        return persistentProjectileEntity;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int i = this.getMaxUseTime(stack) - remainingUseTicks;
        float f = dgGetPullProgress(i, stack);
        if (f >= 1.0F && !isCharged(stack) && CrossbowItem.loadProjectiles(user, stack)) {
            CrossbowItem.setCharged(stack, true);
            SoundCategory soundCategory = user instanceof PlayerEntity ? SoundCategory.PLAYERS : SoundCategory.HOSTILE;
            world.playSound((PlayerEntity)null, user.getX(), user.getY(), user.getZ(), SoundEvents.ITEM_CROSSBOW_LOADING_END, soundCategory, 1.0F, 1.0F / (RANDOM.nextFloat() * 0.5F + 1.0F) + 0.2F);
        }
    }

    // from vanilla
    public static int dgGetPullTime(ItemStack stack) {
        int fullDrawTicks = 25; // vanilla
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(DgRangedWeapon.DRAW_TICKS_TAG)) {
            fullDrawTicks = tag.getInt(DgRangedWeapon.DRAW_TICKS_TAG);
        }

        int i = EnchantmentHelper.getLevel(Enchantments.QUICK_CHARGE, stack);
        return i == 0 ? fullDrawTicks : fullDrawTicks - 5 * i;
    }

    private static float dgGetPullProgress(int useTicks, ItemStack stack) {
        float f = (float)useTicks / (float) dgGetPullTime(stack);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    @Override
    public Item asProxy() {
        return Items.CROSSBOW;
    }
}
