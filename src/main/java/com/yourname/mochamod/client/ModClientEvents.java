package com.yourname.mochamod.client;

import com.yourname.mochamod.MochaMod;
import com.yourname.mochamod.client.renderer.MochaRenderer;
import com.yourname.mochamod.registry.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = MochaMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClientEvents {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.MOCHA.get(), MochaRenderer::new);
    }

    private ModClientEvents() {
    }
}
