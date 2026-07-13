package com.yourname.mochamod.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MochaEntity extends TamableAnimal implements GeoEntity {
    protected static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenLoop("animation.mocha.idle");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("animation.mocha.walk");
    protected static final RawAnimation SIT_ANIM = RawAnimation.begin()
            .thenPlay("animation.mocha.sit_down")
            .thenLoop("animation.mocha.sit_idle");
    protected static final RawAnimation TAME_ANIM = RawAnimation.begin().thenPlay("animation.mocha.tame");

    private static final float HEAL_AMOUNT = 4.0F;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public MochaEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setTame(false, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(4, new FollowOwnerGoal(this, 1.0D, 10.0F, 2.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this.level().isClientSide) {
            boolean willHandle = this.isTame() && this.isOwnedBy(player)
                    || isHealingFood(stack) && this.isTame() && this.getHealth() < this.getMaxHealth()
                    || stack.is(Items.BONE) && !this.isTame();
            return willHandle ? InteractionResult.CONSUME : InteractionResult.PASS;
        }

        if (this.isTame()) {
            // Feeding: cooked chicken or cooked beef heals 4 HP
            if (isHealingFood(stack) && this.getHealth() < this.getMaxHealth()) {
                this.heal(HEAL_AMOUNT);
                stack.consume(1, player);
                this.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
                this.gameEvent(GameEvent.EAT);
                return InteractionResult.SUCCESS;
            }

            // Sit / stand toggle for the owner
            if (this.isOwnedBy(player)) {
                this.setOrderedToSit(!this.isOrderedToSit());
                this.jumping = false;
                this.navigation.stop();
                this.setTarget(null);
                return InteractionResult.SUCCESS;
            }
        } else if (stack.is(Items.BONE)) {
            // Taming attempt: 33% chance, same as the vanilla wolf
            stack.consume(1, player);

            if (this.random.nextInt(3) == 0 && !EventHooks.onAnimalTame(this, player)) {
                this.tame(player);
                this.navigation.stop();
                this.setTarget(null);
                this.setOrderedToSit(true);
                this.level().broadcastEntityEvent(this, (byte) 7); // heart particles
                this.triggerAnim("reaction", "tame");
            } else {
                this.level().broadcastEntityEvent(this, (byte) 6); // smoke particles
            }
            return InteractionResult.SUCCESS;
        }

        return super.mobInteract(player, hand);
    }

    private static boolean isHealingFood(ItemStack stack) {
        return stack.is(Items.COOKED_CHICKEN) || stack.is(Items.COOKED_BEEF);
    }

    @Override
    public boolean isFood(ItemStack stack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
        return null;
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return true;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_AMBIENT;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.WOLF_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    @Override
    protected void playStepSound(net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(SoundEvents.WOLF_STEP, 0.15F, 1.0F);
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.NEUTRAL;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 3, this::movementAnimController));
        controllers.add(new AnimationController<>(this, "reaction", 3, this::reactionAnimController)
                .triggerableAnim("tame", TAME_ANIM));
    }

    protected <E extends MochaEntity> PlayState movementAnimController(AnimationState<E> state) {
        if (this.isOrderedToSit()) {
            return state.setAndContinue(SIT_ANIM);
        }
        if (state.isMoving()) {
            return state.setAndContinue(WALK_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    protected <E extends MochaEntity> PlayState reactionAnimController(AnimationState<E> state) {
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
