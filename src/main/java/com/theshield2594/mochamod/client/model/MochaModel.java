package com.theshield2594.mochamod.client.model;

import com.theshield2594.mochamod.MochaMod;
import com.theshield2594.mochamod.entity.MochaEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MochaModel extends GeoModel<MochaEntity> {
    private static final ResourceLocation MODEL =
            ResourceLocation.fromNamespaceAndPath(MochaMod.MODID, "geo/entity/mocha.geo.json");
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MochaMod.MODID, "textures/entity/mocha.png");
    private static final ResourceLocation SLEEPING_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(MochaMod.MODID, "textures/entity/mocha_sleeping.png");
    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath(MochaMod.MODID, "animations/mocha.animation.json");

    /** How far each tail segment uncurls, in radians, at the brink of death. */
    private static final float TAIL_DROOP = 110.0F * Mth.DEG_TO_RAD;
    private static final float TAIL_TIP_DROOP = 45.0F * Mth.DEG_TO_RAD;

    @Override
    public ResourceLocation getModelResource(MochaEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MochaEntity animatable) {
        return animatable.isVisuallySleeping() ? SLEEPING_TEXTURE : TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MochaEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(MochaEntity animatable, long instanceId, AnimationState<MochaEntity> animationState) {
        this.applyTailDroop(animatable);

        if (animatable.isVisuallySleeping()) {
            return;
        }

        GeoBone head = getAnimationProcessor().getBone("head");
        if (head == null) {
            return;
        }

        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        if (entityData == null) {
            return;
        }

        // Added on top of the keyframed rotation so idle head motion still reads through
        head.setRotX(head.getRotX() + entityData.headPitch() * Mth.DEG_TO_RAD);
        head.setRotY(head.getRotY() + entityData.netHeadYaw() * Mth.DEG_TO_RAD);
    }

    /** Like the vanilla wolf, her tail sinks lower the more hurt she is, so her health reads at a glance. */
    private void applyTailDroop(MochaEntity animatable) {
        // Sitting and sleeping poses already lay the tail along the ground
        if (animatable.isInSittingPose() || animatable.getMaxHealth() <= 0.0F) {
            return;
        }
        float missingHealth = 1.0F - Mth.clamp(animatable.getHealth() / animatable.getMaxHealth(), 0.0F, 1.0F);
        if (missingHealth <= 0.0F) {
            return;
        }
        // Positive X in GeckoLib's bone space uncurls each segment back toward the ground, so a badly
        // hurt Mocha's plume hangs low behind her instead of curling up over her back
        droop("tail", missingHealth * TAIL_DROOP);
        droop("tail_tip", missingHealth * TAIL_TIP_DROOP);
        droop("tail_tip2", missingHealth * TAIL_TIP_DROOP);
    }

    private void droop(String boneName, float radians) {
        GeoBone bone = getAnimationProcessor().getBone(boneName);
        if (bone != null) {
            bone.setRotX(bone.getRotX() + radians);
        }
    }
}
