package com.mystic.atlantis.init;

import com.mystic.atlantis.Atlantis;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class PaintingVariantsInit {
    public static final DeferredRegister<PaintingVariant> UNDERWATER_PAINTING_VARIANTS = DeferredRegister.create(Registries.PAINTING_VARIANT, "atlantis");

    public static final Supplier<PaintingVariant> SPLASH = UNDERWATER_PAINTING_VARIANTS.register("splash", () -> new PaintingVariant(32, 32, Atlantis.id("textures/painting/splash.png")));
    public static final Supplier<PaintingVariant> DRAGON = UNDERWATER_PAINTING_VARIANTS.register("dragon", () -> new PaintingVariant(32, 32, Atlantis.id("textures/painting/dragon.png")));
    public static final Supplier<PaintingVariant> SUNRISE = UNDERWATER_PAINTING_VARIANTS.register("sunrise", () -> new PaintingVariant(16, 16, Atlantis.id("textures/painting/sunrise.png")));
    public static final Supplier<PaintingVariant> KRAKEN = UNDERWATER_PAINTING_VARIANTS.register("kraken", () -> new PaintingVariant(32, 64, Atlantis.id("textures/painting/kraken.png")));

    public static void init(IEventBus bus) {
        UNDERWATER_PAINTING_VARIANTS.register(bus);
    }
}