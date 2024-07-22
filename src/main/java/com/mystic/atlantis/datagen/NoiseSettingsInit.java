package com.mystic.atlantis.datagen;

import com.mystic.atlantis.dimension.DimensionAtlantis;
import com.mystic.atlantis.init.BlockInit;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

import java.util.List;

import static net.minecraft.world.level.levelgen.DensityFunctions.*;
import static net.minecraft.world.level.levelgen.SurfaceRules.*;
import static net.minecraft.world.level.levelgen.VerticalAnchor.absolute;

public class NoiseSettingsInit {
    private final BootstrapContext<NoiseGeneratorSettings> context;
    private final HolderGetter<NormalNoise.NoiseParameters> noiseRegistry;
    private final HolderGetter<DensityFunction> functionRegistry;

    public NoiseSettingsInit(BootstrapContext<NoiseGeneratorSettings> context) {
        this.context = context;
        this.noiseRegistry = context.lookup(Registries.NOISE);
        this.functionRegistry = context.lookup(Registries.DENSITY_FUNCTION);

        context.register(DimensionAtlantis.ATLANTIS_DIMENSION_NOISE_SETTING, new NoiseGeneratorSettings(
                    new NoiseSettings(-64, 512, 1, 2),
                    Blocks.STONE.defaultBlockState(),
                    Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 0),

                    new NoiseRouter(
                            noise(noiseRegistry.getOrThrow(Noises.AQUIFER_BARRIER), 1, 0.5),
                            noise(noiseRegistry.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 1, 0.67),
                            noise(noiseRegistry.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 1, 0.7142857142857143),
                            zero(),
                            shiftedNoise2d(noiseRegistry.getOrThrow(Noises.TEMPERATURE)),
                            shiftedNoise2d(noiseRegistry.getOrThrow(Noises.VEGETATION)),
                            function(functionRegistry, NoiseRouterData.CONTINENTS),
                            function(functionRegistry, NoiseRouterData.EROSION),
                            function(functionRegistry, NoiseRouterData.DEPTH),
                            function(functionRegistry, NoiseRouterData.RIDGES),
                            zero(),
                            mul(
                                    constant(0.64),
                                    interpolated(
                                            blendDensity(
                                                    add(
                                                            constant(2.5),
                                                            mul(
                                                                    yClampedGradient(100, 0, 1, 0),
                                                                    add(
                                                                            constant(-2.77),
                                                                            add(
                                                                                    constant(2),
                                                                                    mul(
                                                                                            yClampedGradient(100, 0, 1, 0),
                                                                                            add(
                                                                                                    constant(-2),
                                                                                                    mul(
                                                                                                            yClampedGradient(350, 450, 1, 0),
                                                                                                            function(functionRegistry, NoiseRouterData.BASE_3D_NOISE_OVERWORLD)
                                                                                                    )
                                                                                            )
                                                                                    )
                                                                            )
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            ).squeeze(),
                            zero(),
                            zero(),
                            zero()
                    ),
                sequence(
                        conditionalVerticalBlockPlacement("minecraft:bedrock_floor", -58, -54, Blocks.BEDROCK),
                        sequence(
                                conditionalVerticalBlockPlacement("atlantis:deepslate_layer", -1, 5, Blocks.DEEPSLATE.defaultBlockState().setValue(RotatedPillarBlock.AXIS, Direction.Axis.X)),
                                conditionalVerticalBlockPlacement("atlantis:stone_layer", 29, 34, Blocks.STONE),
                                conditionalVerticalBlockPlacement("atlantis:detritus_sandstone_layer", 49, 56, BlockInit.DETRITUS_SANDSTONE.get()),
                                conditionalVerticalBlockPlacement("atlantis:sandstone_layer", 58, 64, Blocks.SANDSTONE),
                                conditionalVerticalBlockPlacement("atlantis:seabed_layer", 58, 64, BlockInit.SEABED.get()),
                                ifTrue(waterBlockCheck(0, 0),
                                        ifTrue(
                                                waterBlockCheck(-1, 0),
                                                sequence(
                                                        sequence(
                                                                sequence(
                                                                        ifTrue(
                                                                                waterBlockCheck(0, 0),
                                                                                block(Blocks.SAND)
                                                                        )
                                                                )
                                                        )
                                                )
                                        )
                                ),
                                ifTrue(waterStartCheck(2, -1),
                                        sequence(
                                                ifTrue(stoneDepthCheck(0, true, 0, CaveSurface.FLOOR),
                                                        block(Blocks.SANDSTONE)
                                                )
                                        )
                                )
                        )
                ),
                List.of(),
                350,
                false,
                true,
                true,
                false)
        );
    }

    private RuleSource block(Block block) {
        return state(block.defaultBlockState());
    }

    private DensityFunction noise(Holder<NormalNoise.NoiseParameters> parameter, double xz_scale, double y_scale) {
        return DensityFunctions.noise(parameter, xz_scale, y_scale);
    }

    private RuleSource conditionalVerticalBlockPlacement(String name, int min, int max, BlockState state) {
        return ifTrue(verticalGradient(name, absolute(min), absolute(max)), state(state));
    }

    private static DensityFunction function(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
    }

    private RuleSource conditionalVerticalBlockPlacement(String name, int min, int max, Block block) {
        return conditionalVerticalBlockPlacement(name, min, max, block.defaultBlockState());
    }

    private DensityFunction shiftedNoise2d(Holder<NormalNoise.NoiseParameters> noise) {
        return DensityFunctions.shiftedNoise2d(
                function(functionRegistry, NoiseRouterData.SHIFT_X),
                function(functionRegistry, NoiseRouterData.SHIFT_Z),
                    0.25, noise);
    }
}
