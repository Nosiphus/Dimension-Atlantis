package com.mystic.atlantis.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mystic.atlantis.init.BlockInit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class OverlayEventHandler {

    public static final ResourceLocation COCONUT_BLUR = new ResourceLocation("atlantis", "textures/misc/coconutblur.png");

    private static final Minecraft minecraft = Minecraft.getInstance();

    public static void renderCoconutBlur(GuiGraphics stack) {
        if (minecraft.player != null) {
            if (minecraft.options.getCameraType().isFirstPerson()) {
                if (!minecraft.player.isScoping()) {
                    ItemStack itemstack = minecraft.player.getInventory().getArmor(3);
                    if (itemstack.is(BlockInit.CARVED_COCONUT.get().asItem())) {
                        Screen screen = Minecraft.getInstance().screen;
                        renderTextureOverlay(stack, COCONUT_BLUR, 1.0f, screen.width, screen.height);
                    }
                }
            }
        }
    }

    public static void renderTextureOverlay(GuiGraphics pGuiGraphics, ResourceLocation pShaderLocation, float pAlpha, int width, int height) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, pAlpha);
        pGuiGraphics.blit(pShaderLocation, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        pGuiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}