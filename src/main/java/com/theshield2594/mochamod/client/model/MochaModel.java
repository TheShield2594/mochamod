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
    private static final ResourceLocation ANIMATIONS =
            ResourceLocation.fromNamespaceAndPath(MochaMod.MODID, "animations/mocha.animation.json");

    @Override
    public ResourceLocation getModelResource(MochaEntity animatable) {
        return MODEL;
    }

    @Override
    public ResourceLocation getTextureResource(MochaEntity animatable) {
        return TEXTURE;
    }

    @Override
    public ResourceLocation getAnimationResource(MochaEntity animatable) {
        return ANIMATIONS;
    }

    @Override
    public void setCustomAnimations(MochaEntity animatable, long instanceId, AnimationState<MochaEntity> animationState) {
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
}
