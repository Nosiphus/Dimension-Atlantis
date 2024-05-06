package com.mystic.atlantis.structures;

import com.mystic.atlantis.util.Reference;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.world.level.levelgen.structure.StructureType;

import java.util.function.Supplier;

import static net.minecraft.core.registries.Registries.STRUCTURE_TYPE;

public class AtlantisStructures {
    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Reference.MODID, STRUCTURE_TYPE);

    public static final Supplier<StructureType<AtlanteanFountain>> ATLANTEAN_FOUNTAIN = DEFERRED_REGISTRY_STRUCTURE.register("atlantean_fountain", () -> () ->  AtlanteanFountain.CODEC);
    public static final Supplier<StructureType<AtlantisHouse1>> ATLANTIS_HOUSE_1 = DEFERRED_REGISTRY_STRUCTURE.register("atlantis_house_1", () -> () -> AtlantisHouse1.CODEC);
    public static final Supplier<StructureType<AtlantisHouse3>> ATLANTIS_HOUSE_3 = DEFERRED_REGISTRY_STRUCTURE.register("atlantis_house_3", () -> () -> AtlantisHouse3.CODEC);
    public static final Supplier<StructureType<AtlantisTower>> ATLANTIS_TOWER = DEFERRED_REGISTRY_STRUCTURE.register("atlantis_tower", () -> () -> AtlantisTower.CODEC);
    public static final Supplier<StructureType<OysterStructure>> OYSTER_STRUCTURE = DEFERRED_REGISTRY_STRUCTURE.register("oyster_structure", () -> () -> OysterStructure.CODEC);
    public static final Supplier<StructureType<AtlanteanTemple>> ATLANTEAN_TEMPLE = DEFERRED_REGISTRY_STRUCTURE.register("atlantean_temple", () -> () -> AtlanteanTemple.CODEC);
    public static final Supplier<StructureType<AtlanteanSpire>> ATLANTEAN_SPIRE = DEFERRED_REGISTRY_STRUCTURE.register("atlantean_spire", () -> () -> AtlanteanSpire.CODEC);

    public static void init() {
        DEFERRED_REGISTRY_STRUCTURE.register();
    }
}