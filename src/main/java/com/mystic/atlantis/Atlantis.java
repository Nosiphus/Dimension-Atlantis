package com.mystic.atlantis;

import com.mystic.atlantis.blocks.base.ExtendedBlockEntity;
import com.mystic.atlantis.config.AtlantisConfig;
import com.mystic.atlantis.feature.AtlantisFeature;
import com.mystic.atlantis.datagen.Providers;
import com.mystic.atlantis.dimension.DimensionAtlantis;
import com.mystic.atlantis.entities.*;
import com.mystic.atlantis.init.*;
import com.mystic.atlantis.items.armor.BasicArmorMaterial;
import com.mystic.atlantis.particles.ModParticleTypes;
import com.mystic.atlantis.screen.LinguisticScreen;
import com.mystic.atlantis.screen.WritingScreen;
import com.mystic.atlantis.structures.AtlantisStructures;
import com.mystic.atlantis.util.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.event.entity.SpawnPlacementRegisterEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLibClient;

@Mod(Reference.MODID)
@EventBusSubscriber(modid = Reference.MODID, bus = EventBusSubscriber.Bus.MOD)
public class Atlantis {
    public static final Logger LOGGER = LogManager.getLogger(Reference.MODID);

    public Atlantis(IEventBus bus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, AtlantisConfig.CONFIG_SPEC);
        ModParticleTypes.PARTICLES.register(bus);
        onInitialize(bus);
        AtlantisFeature.init(bus);
        AtlantisStructures.DEFERRED_REGISTRY_STRUCTURE.register(bus);
        Providers.init(bus);
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath("atlantis", id);
    }

    //Don't remove needed for legacy portal block!
    public static ResourceKey<Level> getOverworldKey() {
        ResourceLocation OVERWORLD_ID = LevelStem.OVERWORLD.location();
        return ResourceKey.create(Registries.DIMENSION, OVERWORLD_ID);
    }

    public void onInitialize(IEventBus bus) {
        GeckoLibClient.init();
        BlockInit.init(bus);
        BasicArmorMaterial.init(bus);
        ItemInit.init(bus);
        PaintingVariantsInit.init(bus);
        AtlantisModifierInit.init(bus);
        TileEntityInit.init(bus);
        FluidTypesInit.init(bus);
        FluidInit.init(bus);
        AtlantisGroupInit.init(bus);
        AtlantisEntityInit.init(bus);
        AtlantisSoundEventInit.init(bus);
        EffectsInit.init(bus);
        EnchantmentInit.init(bus);
        MenuTypeInit.init(bus);
        RecipesInit.init(bus);
        POITypesInit.init(bus);
        DimensionAtlantis.init(bus);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuTypeInit.LINGUISTIC.get(), LinguisticScreen::new);
        event.register(MenuTypeInit.WRITING.get(), WritingScreen::new);
    }

    @SubscribeEvent
    public static void onCommonSet(FMLCommonSetupEvent event) {
        TagsInit.init();
        ((ExtendedBlockEntity) BlockEntityType.SIGN).addAdditionalValidBlock(BlockInit.ATLANTEAN_SIGNS.get(), BlockInit.ATLANTEAN_WALL_SIGN.get());
        ((ExtendedBlockEntity) BlockEntityType.SIGN).addAdditionalValidBlock(BlockInit.PALM_SIGNS.get(), BlockInit.PALM_WALL_SIGN.get());
    }

    @SubscribeEvent
    public static void spawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(AtlantisEntityInit.CRAB.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CrabEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(AtlantisEntityInit.COCONUT_CRAB.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CoconutCrabEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(AtlantisEntityInit.JELLYFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, JellyfishEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(AtlantisEntityInit.STARFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, StarfishEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(AtlantisEntityInit.STARFISH_ZOM.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, StarfishZomEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(AtlantisEntityInit.SHRIMP.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, ShrimpEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(AtlantisEntityInit.LEVIATHAN.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LeviathanEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.AND);
        event.register(AtlantisEntityInit.SEAHORSE.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, SeahorseEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.AND);
    }
}
