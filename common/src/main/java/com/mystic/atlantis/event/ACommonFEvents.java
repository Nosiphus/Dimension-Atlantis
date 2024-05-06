package com.mystic.atlantis.event;

import com.mystic.atlantis.config.AtlantisConfig;
import com.mystic.atlantis.dimension.DimensionAtlantis;
import com.mystic.atlantis.init.EffectsInit;
import com.mystic.atlantis.init.EnchantmentInit;
import com.mystic.atlantis.init.ItemInit;
import com.mystic.atlantis.util.Reference;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LightningEvent;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingHurtEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.*;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.GAME)
public class ACommonFEvents {
    public static void init() {
        LightningEvent.STRIKE.register((bolt, level, pos, toStrike) -> {
            for(var entity : toStrike) {
                onLightningStrike(entity);
            }
        });

        EntityEvent.LIVING_HURT.register((entity, source, amount) -> {
            spikesEffectEvent(entity, source);
            return EventResult.pass();
        });

        PlayerEvent.PLAYER_JOIN.register(new PlayerEvent.PlayerJoin() {
            @Override
            public void join(ServerPlayer player) {

            }
        });
    }

    public static boolean hasEnchantment(ItemStack itemStack, Enchantment enchantment) {
        return itemStack.getEnchantments().getLevel(enchantment) > 0;
    }

    public static void onLightningStrike(Entity event) {
        if (event instanceof ItemEntity item) {
            if (item.getItem().getItem() == ItemInit.SEA_SALT.get()) {
                Level world = item.level();
                ItemEntity item2 = new ItemEntity(world, item.getX(), item.getY(), item.getZ(), new ItemStack(ItemInit.SODIUM_NUGGET.get(), item.getItem().getCount()));
                if (!world.isClientSide) {
                    world.addFreshEntity(item2);
                    if (item2.isOnFire()) {
                        item2.clearFire();
                    }
                }
            }
        }
    }

    public static final String NOT_FIRST_SPAWN_NBT = "atlantis.not_first_spawn";
    public static ResourceKey<Level> previousDimension;

    public static void spikesEffectEvent(Entity e, DamageSource source) {
        if (e instanceof Player player) {
            RandomSource random = player.getRandom();
            Entity entity = source.getEntity();
            if (player.hasEffect(Holder.direct(EffectsInit.SPIKES.get()))) {
                if (player.isHurt()) {
                    entity.hurt(player.damageSources().thorns(player), (float) getDamage(3, (Random) random));
                }
            }
        }
    }

    public static Map<ResourceLocation, Integer> map;

    @SubscribeEvent
    public static void onPlayerLoginEvent(Player event) {
        if (AtlantisConfig.INSTANCE.startInAtlantis.get()) {
            if (event.getServer() != null) {
                ServerLevel atlantisLevel = event.getServer().getLevel(DimensionAtlantis.ATLANTIS_WORLD);
                CompoundTag tag = event.getEntity().getPersistentData();
                CompoundTag persistedTag = tag.getCompound(Player.PERSISTED_NBT_TAG);
                if (DimensionAtlantis.ATLANTIS_WORLD != null) {
                    boolean isFirstTimeSpawning = !persistedTag.getBoolean(NOT_FIRST_SPAWN_NBT);
                    if (isFirstTimeSpawning) {
                        if (atlantisLevel != null) {
                            persistedTag.putBoolean(NOT_FIRST_SPAWN_NBT, true);
                            tag.put(Player.PERSISTED_NBT_TAG, persistedTag);
                            if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                                sendPlayerToDimension(serverPlayer, atlantisLevel, new Vec3(atlantisLevel.getLevel().getLevelData().getSpawnPos().getX(), atlantisLevel.getLevel().getLevelData().getSpawnPos().getY(), atlantisLevel.getLevel().getLevelData().getSpawnPos().getZ()));
                            }
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerRespawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            ServerLevel serverLevel = serverPlayer.serverLevel();
            if (DimensionAtlantis.ATLANTIS_DIMENSION != null) {
                if (previousDimension == DimensionAtlantis.ATLANTIS_WORLD) {
                    serverPlayer.setRespawnPosition(DimensionAtlantis.ATLANTIS_WORLD, serverPlayer.blockPosition(), serverPlayer.getYHeadRot(), true, false);
                    serverPlayer.serverLevel().setDefaultSpawnPos(serverPlayer.blockPosition(), 16);
                    if (serverPlayer.getRespawnPosition() != null) {
                        Optional<Vec3> bedPos = Player.findRespawnPositionAndUseSpawnBlock(DimensionAtlantis.ATLANTIS_DIMENSION, serverPlayer.getRespawnPosition(), serverPlayer.getRespawnAngle(), serverPlayer.isRespawnForced(), false);
                        if (bedPos.isEmpty()) {
                            serverPlayer.setRespawnPosition(DimensionAtlantis.ATLANTIS_WORLD, serverLevel.getSharedSpawnPos(), serverPlayer.getYHeadRot(), true, false);
                            sendPlayerToDimension(serverPlayer, DimensionAtlantis.ATLANTIS_DIMENSION, new Vec3(serverPlayer.getRespawnPosition().getX(), serverPlayer.getRespawnPosition().getY(), serverPlayer.getRespawnPosition().getZ()));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDeathEvent(LivingDeathEvent event) {
        previousDimension = event.getEntity().level().dimension();
    }

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event) {
        if (event.getEntity() instanceof Player player) {
            for (ItemStack stack : player.getArmorSlots()) {
                if (hasEnchantment(stack, EnchantmentInit.LIGHTNING_PROTECTION.get())) {
                    if (event.getSource().is(DamageTypes.LIGHTNING_BOLT)) {
                        event.setAmount(0);
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    public static ServerLevel getDimension(ResourceKey<Level> arg, ServerPlayer player) {
        return Objects.requireNonNull(player.getServer()).getLevel(arg);
    }

    private static void sendPlayerToDimension(ServerPlayer serverPlayer, ServerLevel targetWorld, Vec3 targetVec) {
        // ensure destination chunk is loaded before we put the player in it
        targetWorld.getChunk(new BlockPos((int) targetVec.x, (int) targetVec.y, (int) targetVec.z));
        serverPlayer.teleportTo(targetWorld, targetVec.x(), targetVec.y(), targetVec.z(), serverPlayer.getYRot(), serverPlayer.getXRot());
    }

    private static int getDamage(int i, Random random) {
        return i > 10 ? i - 10 : 1 + random.nextInt(4);
    }

    private static boolean isAtlanteanBiome(ResourceKey<Biome> key) {

        return key.location().getNamespace().
                equals(Reference.MODID);
    }

}