package com.theshield2594.mochamod.client.model;

import com.theshield2594.mochamod.MochaMod;
import com.theshield2594.mochamod.entity.MochaEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

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
}
