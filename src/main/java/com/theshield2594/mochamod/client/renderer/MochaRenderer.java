package com.theshield2594.mochamod.client.renderer;

import com.theshield2594.mochamod.client.model.MochaModel;
import com.theshield2594.mochamod.entity.MochaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MochaRenderer extends GeoEntityRenderer<MochaEntity> {
    public MochaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MochaModel());
        this.shadowRadius = 0.4F;
    }
}
