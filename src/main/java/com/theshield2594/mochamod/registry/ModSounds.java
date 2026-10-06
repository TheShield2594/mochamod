package com.theshield2594.mochamod.registry;

import com.theshield2594.mochamod.MochaMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Mocha's own sound events. sounds.json currently points each one at the matching vanilla
 * wolf/fox sound, so she gets her own subtitles ("Mocha barks" rather than "Wolf barks") and
 * real recordings can be dropped in later without touching any code.
 */
public final class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(Registries.SOUND_EVENT, MochaMod.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> MOCHA_AMBIENT = register("entity.mocha.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOCHA_GROWL = register("entity.mocha.growl");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOCHA_PANT = register("entity.mocha.pant");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOCHA_WHINE = register("entity.mocha.whine");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOCHA_SNORE = register("entity.mocha.snore");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOCHA_HURT = register("entity.mocha.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOCHA_DEATH = register("entity.mocha.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOCHA_SHAKE = register("entity.mocha.shake");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOCHA_STEP = register("entity.mocha.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOCHA_EAT = register("entity.mocha.eat");

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(
                ResourceLocation.fromNamespaceAndPath(MochaMod.MODID, name)));
    }

    private ModSounds() {
    }
}
