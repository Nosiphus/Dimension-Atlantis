package com.mystic.atlantis.init;

import com.mystic.atlantis.blocks.blockentities.AtlantisPortalBlockEntity;
import com.mystic.atlantis.blocks.blockentities.plants.*;
import com.mystic.atlantis.blocks.blockentities.plants.AnemoneTileEntity;
import com.mystic.atlantis.util.Reference;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class TileEntityInit {
	public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister
			.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Reference.MODID);

	public static final RegistryObject<BlockEntityType<UnderwaterShroomTileEntity>> UNDERWATER_SHROOM_TILE = TILE_ENTITIES.register("underwater_shroom", () -> BlockEntityType.Builder.of(UnderwaterShroomTileEntity::new, BlockInit.UNDERWATER_SHROOM_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<TuberUpTileEntity>> TUBER_UP_TILE = TILE_ENTITIES.register("tuber_up", () -> BlockEntityType.Builder.of(TuberUpTileEntity::new, BlockInit.TUBER_UP_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<BlueLilyTileEntity>> BLUE_LILY_TILE = TILE_ENTITIES.register("blue_lily", () -> BlockEntityType.Builder.of(BlueLilyTileEntity::new, BlockInit.BLUE_LILY_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<BurntDeepTileEntity>> BURNT_DEEP_TILE = TILE_ENTITIES.register("burnt_deep", () -> BlockEntityType.Builder.of(BurntDeepTileEntity::new, BlockInit.BURNT_DEEP_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<AnemoneTileEntity>> ANEMONE_TILE = TILE_ENTITIES.register("anemone", () -> BlockEntityType.Builder.of(AnemoneTileEntity::new, BlockInit.ANEMONE_BLOCK.get()).build(null));
	public static final RegistryObject<BlockEntityType<AtlantisPortalBlockEntity>> ATLANTIS_PORTAL = TILE_ENTITIES.register("atlantis_portal", () -> BlockEntityType.Builder.of(AtlantisPortalBlockEntity::new, BlockInit.ATLANTIS_CLEAR_PORTAL.get()).build(null));
	public static void init(IEventBus bus) {
		TILE_ENTITIES.register(bus);
	}

}
