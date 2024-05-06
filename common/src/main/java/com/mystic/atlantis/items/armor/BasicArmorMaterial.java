package com.mystic.atlantis.items.armor;

import com.mystic.atlantis.init.ItemInit;
import com.mystic.atlantis.util.Reference;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;

public class BasicArmorMaterial {
    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIAL = DeferredRegister.create(Reference.MODID, Registries.ARMOR_MATERIAL);

    public static final Holder<ArmorMaterial> ARMOR_AQUAMARINE = ARMOR_MATERIAL.register("aquamarine", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), p_323378_ -> {
                p_323378_.put(ArmorItem.Type.BOOTS, 2);
                p_323378_.put(ArmorItem.Type.LEGGINGS, 6);
                p_323378_.put(ArmorItem.Type.CHESTPLATE, 7);
                p_323378_.put(ArmorItem.Type.HELMET, 3);
                p_323378_.put(ArmorItem.Type.BODY, 6);
            }), 9, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(ItemInit.AQUAMARINE_GEM.get()), List.of(new ArmorMaterial.Layer(new ResourceLocation("aquamarine"))), 1.0F, 0.0F));
    public static final Holder<ArmorMaterial> ARMOR_ORICHALCUM = ARMOR_MATERIAL.register("orichalcum", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), p_323378_ -> {
        p_323378_.put(ArmorItem.Type.BOOTS, 3);
        p_323378_.put(ArmorItem.Type.LEGGINGS, 7);
        p_323378_.put(ArmorItem.Type.CHESTPLATE, 8);
        p_323378_.put(ArmorItem.Type.HELMET, 4);
        p_323378_.put(ArmorItem.Type.BODY, 7);
    }), 9, SoundEvents.ARMOR_EQUIP_GOLD, () -> Ingredient.of(ItemInit.AQUAMARINE_GEM.get()), List.of(new ArmorMaterial.Layer(new ResourceLocation("orichalcum"))), 1.0F, 0.0F));
    public static final Holder<ArmorMaterial> ARMOR_BROWN_WROUGHT = ARMOR_MATERIAL.register("wrought", () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), p_323378_ -> {
                p_323378_.put(ArmorItem.Type.BOOTS, 3);
                p_323378_.put(ArmorItem.Type.LEGGINGS, 5);
                p_323378_.put(ArmorItem.Type.CHESTPLATE, 5);
                p_323378_.put(ArmorItem.Type.HELMET, 4);
                p_323378_.put(ArmorItem.Type.BODY, 5);
            }), 15, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(ItemInit.BROWN_WROUGHT_PATCHES.get()), List.of(new ArmorMaterial.Layer(new ResourceLocation("wrought"))), 2.0F, 0.0F));

    public static void init() {
        ARMOR_MATERIAL.register();
    }
}