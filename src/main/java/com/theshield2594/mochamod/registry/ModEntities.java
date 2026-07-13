package com.theshield2594.mochamod.registry;

import com.theshield2594.mochamod.MochaMod;
import com.theshield2594.mochamod.entity.MochaEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, MochaMod.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<MochaEntity>> MOCHA =
            ENTITY_TYPES.register("mocha", () -> EntityType.Builder.of(MochaEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.85F)
                    .eyeHeight(0.68F)
                    .clientTrackingRange(10)
                    .build("mocha"));

    private ModEntities() {
    }
}
