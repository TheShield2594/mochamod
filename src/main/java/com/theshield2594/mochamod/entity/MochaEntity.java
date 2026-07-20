package com.theshield2594.mochamod.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.SitWhenOrderedToGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
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
    protected static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("animation.mocha.run");
    protected static final RawAnimation SWIM_ANIM = RawAnimation.begin().thenLoop("animation.mocha.swim");
    protected static final RawAnimation AIRBORNE_ANIM = RawAnimation.begin().thenLoop("animation.mocha.airborne");
    protected static final RawAnimation ANGRY_ANIM = RawAnimation.begin().thenLoop("animation.mocha.angry");
    protected static final RawAnimation BEG_ANIM = RawAnimation.begin().thenLoop("animation.mocha.beg");
    protected static final RawAnimation SIT_ANIM = RawAnimation.begin()
            .thenPlay("animation.mocha.sit_down")
            .thenLoop("animation.mocha.sit_idle");
    protected static final RawAnimation SIT_REST_ANIM = RawAnimation.begin()
            .thenPlay("animation.mocha.sit_settle")
            .thenLoop("animation.mocha.sit_rest");
    protected static final RawAnimation SLEEP_ANIM = RawAnimation.begin()
            .thenPlay("animation.mocha.sleep_settle")
            .thenLoop("animation.mocha.sleep");
    protected static final RawAnimation STAND_UP_ANIM = RawAnimation.begin()
            .thenPlay("animation.mocha.stand_up")
            .thenLoop("animation.mocha.idle");
    protected static final RawAnimation TAME_ANIM = RawAnimation.begin().thenPlay("animation.mocha.tame");
    protected static final RawAnimation EAT_ANIM = RawAnimation.begin().thenPlay("animation.mocha.eat");
    protected static final RawAnimation SHAKE_ANIM = RawAnimation.begin().thenPlay("animation.mocha.shake");
    protected static final RawAnimation STRETCH_ANIM = RawAnimation.begin().thenPlay("animation.mocha.stretch");
    protected static final RawAnimation ATTACK_ANIM = RawAnimation.begin().thenPlay("animation.mocha.attack");
    protected static final RawAnimation HURT_ANIM = RawAnimation.begin().thenPlay("animation.mocha.hurt");
    protected static final RawAnimation LAND_ANIM = RawAnimation.begin().thenPlay("animation.mocha.land");

    private static final EntityDataAccessor<Boolean> DATA_BEGGING =
            SynchedEntityData.defineId(MochaEntity.class, EntityDataSerializers.BOOLEAN);

    /** Ticks spent continuously sitting before settling into a deeper rest pose. */
    private static final int SIT_SETTLE_TICKS = 100;
    /** Ticks spent continuously sitting before dozing off entirely. */
    private static final int SLEEP_TICKS = 400;

    /** Squared horizontal blocks-per-tick above which the gallop animation plays instead of the trot. */
    private static final double RUN_SPEED_SQR = 0.015D;
    /** Nominal horizontal blocks-per-tick of the base trot, used to scale the walk cycle to actual velocity. */
    private static final double WALK_NOMINAL_SPEED = 0.11D;

    private static final float HEAL_AMOUNT = 4.0F;

    /** Downward blocks-per-tick beyond which touching down plays the landing squash (roughly a 2-block fall). */
    private static final double LAND_ANIM_FALL_SPEED = -0.5D;

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private int sitStillTicks;
    private boolean standUpPending;
    private boolean wetFromSwimming;
    private boolean wasOnGround = true;
    private double lastFallSpeed;

    public MochaEntity(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
        this.setTame(false, false);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return TamableAnimal.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 3.0D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_BEGGING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));
        this.goalSelector.addGoal(2, new SitWhenOrderedToGoal(this));
        this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.2D, true));
        this.goalSelector.addGoal(5, new FollowOwnerGoal(this, 1.3D, 10.0F, 2.0F));
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(7, new MochaBegGoal(this, 8.0F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide) {
            if (this.isOrderedToSit()) {
                if (this.sitStillTicks < SLEEP_TICKS) {
                    this.sitStillTicks++;
                }
            } else {
                this.sitStillTicks = 0;
            }
            return;
        }

        // Shake off after climbing out of the water, like a proper dog
        if (this.isInWater()) {
            this.wetFromSwimming = true;
        } else if (this.wetFromSwimming && this.onGround() && !this.isOrderedToSit()) {
            // Stays wet while sitting so she still shakes off once told to stand
            this.wetFromSwimming = false;
            this.triggerAnim("reaction", "shake");
            this.playSound(SoundEvents.WOLF_SHAKE, 0.8F, 1.0F);
        }

        // Occasional idle stretch (play bow), roughly every 20 seconds of standing around
        if (!this.isOrderedToSit() && this.onGround() && !this.isInWater() && !this.isBegging()
                && this.getNavigation().isDone() && this.random.nextInt(400) == 0) {
            this.triggerAnim("reaction", "stretch");
        }

        // Landing squash after a real fall; velocity is captured a tick behind because
        // the collision has already zeroed it by the time this runs
        boolean grounded = this.onGround();
        if (grounded && !this.wasOnGround && !this.isInWater() && this.lastFallSpeed < LAND_ANIM_FALL_SPEED) {
            this.triggerAnim("reaction", "land");
        }
        this.wasOnGround = grounded;
        this.lastFallSpeed = this.getDeltaMovement().y;
    }

    public boolean isBegging() {
        return this.entityData.get(DATA_BEGGING);
    }

    public void setBegging(boolean begging) {
        this.entityData.set(DATA_BEGGING, begging);
    }

    /** Client-side render state: true once she has dozed off after sitting long enough. */
    public boolean isVisuallySleeping() {
        return this.isOrderedToSit() && this.sitStillTicks >= SLEEP_TICKS;
    }

    /** Items worth begging for: a bone before taming, healing food afterwards. */
    boolean isInterestingItem(ItemStack stack) {
        return this.isTame() ? isHealingFood(stack) : stack.is(Items.BONE);
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
                this.triggerAnim("reaction", "eat");
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

    static boolean isHealingFood(ItemStack stack) {
        return stack.is(Items.COOKED_CHICKEN) || stack.is(Items.COOKED_BEEF);
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        if (!this.level().isClientSide) {
            this.setOrderedToSit(false);
        }
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide) {
            this.triggerAnim("reaction", "hurt");
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        // Fires before the damage lands so the lunge still plays against a raised shield
        this.triggerAnim("reaction", "attack");
        return super.doHurtTarget(target);
    }

    /** Same exclusions as the vanilla wolf: no creepers, ghasts, or another owner's pets. */
    @Override
    public boolean wantsToAttack(LivingEntity target, LivingEntity owner) {
        if (target instanceof Creeper || target instanceof Ghast || target instanceof ArmorStand) {
            return false;
        }
        if (target instanceof MochaEntity otherMocha) {
            return !otherMocha.isTame() || otherMocha.getOwner() != owner;
        }
        if (target instanceof Player targetPlayer && owner instanceof Player ownerPlayer
                && !ownerPlayer.canHarmPlayer(targetPlayer)) {
            return false;
        }
        if (target instanceof AbstractHorse horse && horse.isTamed()) {
            return false;
        }
        return !(target instanceof TamableAnimal tamable) || !tamable.isTame();
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
        return this.isAggressive() ? SoundEvents.WOLF_GROWL : SoundEvents.WOLF_AMBIENT;
    }

    @Override
    public float getVoicePitch() {
        // Mocha is scaled down to a small dog (see MochaRenderer.SIZE_SCALE), but the wolf
        // bark/growl/hurt/death sounds are recorded for a full-size wolf. Pitch them up so she
        // sounds like a small dog instead of a wolf, the same trick vanilla uses for baby mobs.
        return (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F;
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
        controllers.add(new AnimationController<>(this, "expression", 5, this::expressionAnimController));
        controllers.add(new AnimationController<>(this, "reaction", 3, this::reactionAnimController)
                .triggerableAnim("tame", TAME_ANIM)
                .triggerableAnim("eat", EAT_ANIM)
                .triggerableAnim("shake", SHAKE_ANIM)
                .triggerableAnim("stretch", STRETCH_ANIM)
                .triggerableAnim("attack", ATTACK_ANIM)
                .triggerableAnim("hurt", HURT_ANIM)
                .triggerableAnim("land", LAND_ANIM));
    }

    protected <E extends MochaEntity> PlayState movementAnimController(AnimationState<E> state) {
        if (this.isOrderedToSit()) {
            this.standUpPending = true;
            state.getController().setAnimationSpeed(1.0D);
            if (this.sitStillTicks >= SLEEP_TICKS) {
                return state.setAndContinue(SLEEP_ANIM);
            }
            return state.setAndContinue(this.sitStillTicks >= SIT_SETTLE_TICKS ? SIT_REST_ANIM : SIT_ANIM);
        }

        if (this.isInWater()) {
            this.standUpPending = false;
            state.getController().setAnimationSpeed(1.0D);
            return state.setAndContinue(SWIM_ANIM);
        }

        if (!this.onGround()) {
            this.standUpPending = false;
            state.getController().setAnimationSpeed(1.0D);
            return state.setAndContinue(AIRBORNE_ANIM);
        }

        if (state.isMoving()) {
            this.standUpPending = false;
            double speedSqr = this.getDeltaMovement().horizontalDistanceSqr();
            if (speedSqr > RUN_SPEED_SQR) {
                state.getController().setAnimationSpeed(1.0D);
                return state.setAndContinue(RUN_ANIM);
            }
            // Scale the trot cycle to actual velocity so the feet don't slide
            state.getController().setAnimationSpeed(Mth.clamp(Math.sqrt(speedSqr) / WALK_NOMINAL_SPEED, 0.6D, 1.6D));
            return state.setAndContinue(WALK_ANIM);
        }

        state.getController().setAnimationSpeed(1.0D);
        if (this.standUpPending) {
            // Plays the get-up transition once, then settles into the regular idle loop
            return state.setAndContinue(STAND_UP_ANIM);
        }
        return state.setAndContinue(IDLE_ANIM);
    }

    protected <E extends MochaEntity> PlayState expressionAnimController(AnimationState<E> state) {
        // Ears pinned back and tail held stiff for the whole fight, layered over walk/run
        if (this.isAggressive() && !this.isOrderedToSit() && !this.isInWater()) {
            return state.setAndContinue(ANGRY_ANIM);
        }
        if (this.isBegging() && !this.isOrderedToSit() && !state.isMoving() && !this.isInWater()) {
            return state.setAndContinue(BEG_ANIM);
        }
        return PlayState.STOP;
    }

    protected <E extends MochaEntity> PlayState reactionAnimController(AnimationState<E> state) {
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }
}
