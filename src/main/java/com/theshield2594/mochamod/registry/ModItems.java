package com.theshield2594.mochamod.registry;

import com.theshield2594.mochamod.MochaMod;
import com.theshield2594.mochamod.item.MochaItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MochaMod.MODID);

    public static final DeferredItem<MochaItem> MOCHA =
            ITEMS.register("mocha", () -> new MochaItem(new Item.Properties()));

    private ModItems() {
    }
}
