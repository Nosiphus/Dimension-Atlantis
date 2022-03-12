package com.mystic.atlantis;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.mystic.atlantis.blocks.blockentities.registry.TileRegistry;
import com.mystic.atlantis.config.AtlantisConfig;
import com.mystic.atlantis.configfeature.AtlantisFeature;
import com.mystic.atlantis.dimension.DimensionAtlantis;
import com.mystic.atlantis.entities.AtlantisEntities;
import com.mystic.atlantis.init.BlockInit;
import com.mystic.atlantis.init.FluidInit;
import com.mystic.atlantis.init.ItemInit;
import com.mystic.atlantis.init.ToolInit;
import com.mystic.atlantis.itemgroup.AtlantisGroup;
import com.mystic.atlantis.setup.ClientSetup;
import com.mystic.atlantis.structures.AtlantisConfiguredStructures;
import com.mystic.atlantis.structures.AtlantisStructures;
import com.mystic.atlantis.util.Reference;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.kyrptonaught.customportalapi.api.CustomPortalBuilder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.FlatLevelSource;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.StructureSettings;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;

import java.util.HashMap;
import java.util.Map;

@Mod(Reference.MODID)
public class Atlantis {
    /**
     * @deprecated this is not a thread-safe thing.
     * Imagine that you open your single-player level and close it
     * soon, and join a dedicated server. At that time this value
     * is absolutely wrong, AND stops JVM from removing it from
     * your RAM.
     */
    @Deprecated @SuppressWarnings({"unused"})
    private static final MinecraftServer server = null;
    public static final Logger LOGGER = LogManager.getLogger(Reference.MODID);

    private static final PlacedFeature ORE_AQUAMARINE_OVERWORLD = Feature.ORE
            .configured(new OreConfiguration(
                    OreFeatures.STONE_ORE_REPLACEABLES,
                    BlockInit.AQUAMARINE_ORE.defaultBlockState(),
                    9)).placed(
                    CountPlacement.of(20), // number of veins per chunk
                    InSquarePlacement.spread(), // spreading horizontally
                    HeightRangePlacement.triangle(VerticalAnchor.bottom(), VerticalAnchor.top())); // height
    public static ResourceKey<PlacedFeature> oreAquamarineOverworld;

    @NotNull @Deprecated
    public static MinecraftServer getServer() {
        throw new UnsupportedOperationException();
    }

    public static AtlantisConfig CONFIG;

    public Atlantis() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(ClientSetup::onInitializeClient);
        onInitialize(bus);
    }

    public void onInitialize(IEventBus bus) {
        BlockInit.init(bus);
        ItemInit.init(bus);
        TileRegistry.init(bus);
        FluidInit.init(bus);

        AtlantisGroup.init();
        GeckoLib.initialize();
        AtlantisEntities.initialize(bus);

        DimensionAtlantis.init(bus);

        bus.addListener(this::onCommonSet);

        MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoad);
        MinecraftForge.EVENT_BUS.addListener(this::addDimensionalSpacing);
    }

    public void onCommonSet(FMLCommonSetupEvent event) {
        AutoConfig.register(AtlantisConfig.class, GsonConfigSerializer::new);
        ToolInit.init();

        CustomPortalBuilder.beginPortal()
                .frameBlock(BlockInit.ATLANTEAN_CORE)
                .lightWithWater()
                .flatPortal()
                .destDimID(new ResourceLocation("atlantis", "atlantis"))
                .tintColor(0, 125, 255)
                .customPortalBlock(BlockInit.ATLANTIS_CLEAR_PORTAL)
                .registerPortal();

        GeckoLibMod.DISABLE_IN_DEV = true;

        event.enqueueWork(() -> {
            DimensionAtlantis.registerBiomeSources();
            AtlantisFeature.ConfiguredFeaturesAtlantis.registerConfiguredFeatures();
            AtlantisStructures.setupStructures();
            AtlantisConfiguredStructures.registerConfiguredStructures();

            oreAquamarineOverworld = ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation("atlantis", "ore_aquamarine_overworld"));
            Registry.register(BuiltinRegistries.PLACED_FEATURE, oreAquamarineOverworld.location(), ORE_AQUAMARINE_OVERWORLD);
        });
    }

    public void onBiomeLoad(BiomeLoadingEvent event) {
        //TODO: FIgure out how to get the overworld
        event.getGeneration().addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ORE_AQUAMARINE_OVERWORLD);
    }


    public void addDimensionalSpacing(final WorldEvent.Load event) {
        if(event.getWorld() instanceof ServerLevel serverLevel) {
            ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();

            // Skip superflat worlds to prevent issues with it. Plus, users don't want structures clogging up their superflat worlds.
            if (chunkGenerator instanceof FlatLevelSource && serverLevel.dimensionType().equals(Level.OVERWORLD)) {
                return;
            }

            StructureSettings worldStructureConfig = chunkGenerator.getSettings();

            HashMap<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> STStructureToMultiMap = new HashMap<>();

            for (Map.Entry<ResourceKey<Biome>, Biome> biomeEntry : serverLevel.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).entrySet()) {
                // Skip all ocean, end, nether, and none category biomes.
                // You can do checks for other traits that the biome has.
                if(isAlanteanBiome(biomeEntry.getKey())) {
                    associateBiomeToConfiguredStructure(STStructureToMultiMap, AtlantisConfiguredStructures.CONFIGURED_ATLANTEAN_FOUNTAIN, biomeEntry.getKey());
                    associateBiomeToConfiguredStructure(STStructureToMultiMap, AtlantisConfiguredStructures.CONFIGURED_ATLANTEAN_HOUSE_1, biomeEntry.getKey());
                    associateBiomeToConfiguredStructure(STStructureToMultiMap, AtlantisConfiguredStructures.CONFIGURED_ATLANTEAN_HOUSE_3, biomeEntry.getKey());
                    associateBiomeToConfiguredStructure(STStructureToMultiMap, AtlantisConfiguredStructures.CONFIGURED_ATLANTEAN_TOWER, biomeEntry.getKey());
                    associateBiomeToConfiguredStructure(STStructureToMultiMap, AtlantisConfiguredStructures.CONFIGURED_OYSTER_STRUCTURE, biomeEntry.getKey());
                }
            }

            ImmutableMap.Builder<StructureFeature<?>, ImmutableMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> tempStructureToMultiMap = ImmutableMap.builder();
            worldStructureConfig.configuredStructures.entrySet().stream().filter(entry -> !STStructureToMultiMap.containsKey(entry.getKey())).forEach(tempStructureToMultiMap::put);

            // Add our structures to the structure map/multimap and set the world to use this combined map/multimap.
            STStructureToMultiMap.forEach((key, value) -> tempStructureToMultiMap.put(key, ImmutableMultimap.copyOf(value)));

            // Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
            worldStructureConfig.configuredStructures = tempStructureToMultiMap.build();
        }
    }

    /**
     * Helper method that handles setting up the map to multimap relationship to help prevent issues.
     */
    private static void associateBiomeToConfiguredStructure(Map<StructureFeature<?>, HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>>> STStructureToMultiMap, ConfiguredStructureFeature<?, ?> configuredStructureFeature, ResourceKey<Biome> biomeRegistryKey) {
        STStructureToMultiMap.putIfAbsent(configuredStructureFeature.feature, HashMultimap.create());
        HashMultimap<ConfiguredStructureFeature<?, ?>, ResourceKey<Biome>> configuredStructureToBiomeMultiMap = STStructureToMultiMap.get(configuredStructureFeature.feature);
        if(configuredStructureToBiomeMultiMap.containsValue(biomeRegistryKey)) {
            LOGGER.error(String.format("""
                    Detected 2 ConfiguredStructureFeatures that share the same base StructureFeature trying to be added to same biome. One will be prevented from spawning.
                    This issue happens with vanilla too and is why a Snowy Village and Plains Village cannot spawn in the same biome because they both use the Village base structure.
                    The two conflicting ConfiguredStructures are: %s, %s
                    The biome that is attempting to be shared: %s
                """,
                    BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getKey(configuredStructureFeature).toString(),
                    BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE.getKey(configuredStructureToBiomeMultiMap.entries().stream().filter(e -> e.getValue() == biomeRegistryKey).findFirst().get().getKey()).toString(),
                    biomeRegistryKey
            ));
        }
        else{
            configuredStructureToBiomeMultiMap.put(configuredStructureFeature, biomeRegistryKey);
        }
    }

    private boolean isAlanteanBiome(ResourceKey<Biome> key) {
        return key.location().getNamespace().
                equals(Reference.MODID);
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation("atlantis", id);
    }

    //Don't remove needed for legacy portal block!
    public static ResourceKey<Level> getOverworldKey() {
        ResourceLocation OVERWORLD_ID = LevelStem.OVERWORLD.location();
        return ResourceKey.create(Registry.DIMENSION_REGISTRY, OVERWORLD_ID);
    }
}
