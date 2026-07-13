package com.yourname.mochamod.registry;

import com.yourname.mochamod.MochaMod;
import com.yourname.mochamod.item.MochaItem;
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
