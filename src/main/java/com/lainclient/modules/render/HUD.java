package com.lainclient.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// i cant for the fucking life of me get subscribe event working so im registering every single FUCKING MODULE
@Mod(modid="hud", name = "Hud")
public class HUD {

    private static final Minecraft mc = Minecraft.getMinecraft();

    private static final int IMAGE_WIDTH = 75;  // Width of the watermark image
    private static final int IMAGE_HEIGHT = 34; // Height of the watermark image

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type == RenderGameOverlayEvent.ElementType.ALL) {
            renderImage();
        }
    }

    private void renderImage() {
        TextureManager textureManager = mc.getTextureManager();
        textureManager.bindTexture(new ResourceLocation("hud", "textures/watermark.png")); //Path to the watermark image with the fake ass fps counter
        int x = 2; // X position of the image
        int y = 2; // Y position of the image
        // Draw the image
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT); 
    }
}

