package com.theshield2594.mochamod.event;

import com.theshield2594.mochamod.MochaMod;
import com.theshield2594.mochamod.entity.MochaEntity;
import com.theshield2594.mochamod.registry.ModEntities;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = MochaMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModCommonEvents {
    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MOCHA.get(), MochaEntity.createAttributes().build());
    }

    private ModCommonEvents() {
    }
}
