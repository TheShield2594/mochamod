package com.theshield2594.mochamod;

import com.theshield2594.mochamod.registry.ModCreativeTabs;
import com.theshield2594.mochamod.registry.ModEntities;
import com.theshield2594.mochamod.registry.ModItems;
import com.theshield2594.mochamod.registry.ModSounds;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MochaMod.MODID)
public class MochaMod {
    public static final String MODID = "mochamod";

    public MochaMod(IEventBus modEventBus) {
        ModSounds.SOUND_EVENTS.register(modEventBus);
        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    }
}
