package com.theshield2594.mochamod.client.renderer;

import com.theshield2594.mochamod.client.model.MochaModel;
import com.theshield2594.mochamod.entity.MochaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MochaRenderer extends GeoEntityRenderer<MochaEntity> {
    /** The model is authored at full wolf scale; shrink it to fit a small (~6 lb) dog. */
    private static final float SIZE_SCALE = 0.7F;

    public MochaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MochaModel());
        this.shadowRadius = 0.4F * SIZE_SCALE;
        this.withScale(SIZE_SCALE);
    }
}
