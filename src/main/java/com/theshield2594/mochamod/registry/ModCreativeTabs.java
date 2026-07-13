package com.theshield2594.mochamod.registry;

import com.theshield2594.mochamod.MochaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MochaMod.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOCHA_TAB =
            CREATIVE_MODE_TABS.register("mocha_mod", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + MochaMod.MODID))
                    .icon(() -> new ItemStack(ModItems.MOCHA.get()))
                    .displayItems((parameters, output) -> output.accept(ModItems.MOCHA.get()))
                    .build());

    private ModCreativeTabs() {
    }
}
