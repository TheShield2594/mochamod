package com.theshield2594.mochamod.entity;

import java.util.EnumSet;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

/**
 * Mirrors the vanilla wolf's BegGoal: when a nearby player holds something interesting
 * (a bone before taming, healing food afterwards), Mocha locks eyes on them and begs.
 */
public class MochaBegGoal extends Goal {
    private final MochaEntity mocha;
    private final Level level;
    private final float lookDistance;
    private final TargetingConditions begTargeting;
    @Nullable
    private Player player;
    private int lookTime;

    public MochaBegGoal(MochaEntity mocha, float lookDistance) {
        this.mocha = mocha;
        this.level = mocha.level();
        this.lookDistance = lookDistance;
        this.begTargeting = TargetingConditions.forNonCombat().range(lookDistance);
        this.setFlags(EnumSet.of(Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        this.player = this.level.getNearestPlayer(this.begTargeting, this.mocha);
        return this.player != null && this.playerHoldingInteresting(this.player);
    }

    @Override
    public boolean canContinueToUse() {
        if (this.player == null || !this.player.isAlive()) {
            return false;
        }
        if (this.mocha.distanceToSqr(this.player) > (double) (this.lookDistance * this.lookDistance)) {
            return false;
        }
        return this.lookTime > 0 && this.playerHoldingInteresting(this.player);
    }

    @Override
    public void start() {
        this.mocha.setBegging(true);
        this.lookTime = this.adjustedTickDelay(40 + this.mocha.getRandom().nextInt(40));
    }

    @Override
    public void stop() {
        this.mocha.setBegging(false);
        this.player = null;
    }

    @Override
    public void tick() {
        this.mocha.getLookControl().setLookAt(this.player.getX(), this.player.getEyeY(), this.player.getZ(),
                10.0F, (float) this.mocha.getMaxHeadXRot());
        this.lookTime--;
    }

    private boolean playerHoldingInteresting(Player player) {
        for (InteractionHand hand : InteractionHand.values()) {
            if (this.mocha.isInterestingItem(player.getItemInHand(hand))) {
                return true;
            }
        }
        return false;
    }
}
