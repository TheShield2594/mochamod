package com.theshield2594.mochamod.client.renderer;

import com.theshield2594.mochamod.client.model.MochaModel;
import com.theshield2594.mochamod.entity.MochaEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.Color;

public class MochaRenderer extends GeoEntityRenderer<MochaEntity> {
    /** The model is authored at full wolf scale; shrink it to fit a small (~6 lb) dog. */
    private static final float SIZE_SCALE = 0.7F;
    /** Soaked fur reads darker, same as the vanilla wolf, until she shakes it off. */
    private static final float WET_SHADE = 0.75F;

    public MochaRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new MochaModel());
        this.shadowRadius = 0.4F * SIZE_SCALE;
        this.withScale(SIZE_SCALE);
    }

    @Override
    public Color getRenderColor(MochaEntity animatable, float partialTick, int packedLight) {
        Color color = super.getRenderColor(animatable, partialTick, packedLight);
        if (!animatable.isWet()) {
            return color;
        }
        // Darken the RGB only, keeping the parent's alpha (e.g. translucent while invisible to spectators)
        return Color.ofARGB(color.getAlpha(), Math.round(color.getRed() * WET_SHADE),
                Math.round(color.getGreen() * WET_SHADE), Math.round(color.getBlue() * WET_SHADE));
    }
}
