package com.yourname.mochamod.client.renderer;

import com.yourname.mochamod.client.model.MochaModel;
import com.yourname.mochamod.entity.MochaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MochaRenderer extends GeoEntityRenderer<MochaEntity> {
    public MochaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MochaModel());
        this.shadowRadius = 0.4F;
    }
}
