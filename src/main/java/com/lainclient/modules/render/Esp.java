package com.lainclient.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// i cant for the fucking life of me get subscribe event working so im registering every single FUCKING MODULE
@Mod(modid="Esp", name = "Esp")
public class Esp {

    private static final Minecraft mc = Minecraft.getMinecraft();
    
    // Customise the color to something pweity instead of eye burning turquoise
    private float red = 0.0F;
    private float green = 1.0F;
    private float blue = 0.9F;
    private float alpha = 1.0F;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderWorldLast(RenderWorldLastEvent event) {
        for (Entity entity : mc.theWorld.loadedEntityList) {
            if (entity instanceof EntityPlayer && entity != mc.thePlayer) {
                renderESPBox((EntityPlayer) entity, event.partialTicks);
            }
        }
    }

    private void renderESPBox(EntityPlayer player, float partialTicks) {
        // Get player pos
        double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        // Get the player's hitbox
        AxisAlignedBB boundingBox = player.getEntityBoundingBox();
        boundingBox = boundingBox.offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ); // Adjust for camera position

        // Set OpenGL settings for rendering the ESP box
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(770, 771);
        
        // Set color for the ESP box
        GlStateManager.color(red, green, blue, alpha);

        // Render wireframe box around the player
        RenderGlobal.drawSelectionBoundingBox(boundingBox);

        // Reset OpenGL settings
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }


    public void setColor(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
}

