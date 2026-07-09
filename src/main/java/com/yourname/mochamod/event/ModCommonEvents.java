package com.yourname.mochamod.event;

import com.yourname.mochamod.MochaMod;
import com.yourname.mochamod.entity.MochaEntity;
import com.yourname.mochamod.registry.ModEntities;
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
