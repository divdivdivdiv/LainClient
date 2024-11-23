package com.lainclient.modules.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

@Mod(modid = "mainmenu", name = "MainMenu")
public class MainMenu {

    private static final int BUTTON_RADIUS = 0;
    private static final int BUTTON_COLOR = 0x4D000000;
    private static final int HOVER_COLOR = 0x99000000;
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("mainmenu", "textures/background.png");


    private final Minecraft mc = Minecraft.getMinecraft();

    // Fade parameters
    private static final float FADE_LENGTH = 3.0f;
    private long fadeStartTime;
    private boolean fadeIn = true;
    private float fadeAlpha = 1.0f;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.gui instanceof GuiMainMenu) {
            event.gui = new CustomMainMenu();
        }
    }

    public class CustomMainMenu extends GuiMainMenu {

        @Override
        public void initGui() {
            super.initGui();
            this.buttonList.clear();

            // Custom button layout
            int buttonWidth = 250;
            int buttonHeight = 25;
            int spacing = 37;
            int startX = this.width / 2 - 130;
            int startY = this.height / 4 + 48;

            // Add custom buttons
            this.buttonList.add(new CustomButton(1, startX, startY, buttonWidth, buttonHeight, "LoneleyPlayer"));
            this.buttonList.add(new CustomButton(2, startX, startY + spacing, buttonWidth, buttonHeight, "MultiPlayer"));
            this.buttonList.add(new CustomButton(0, startX, startY + 2 * spacing, buttonWidth, buttonHeight, "Settings"));
            this.buttonList.add(new CustomButton(4, startX, startY + 3 * spacing, buttonWidth, buttonHeight, "Quit LainClient"));

            // Start the fade effect
            fadeStartTime = System.currentTimeMillis();
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            long elapsedTime = System.currentTimeMillis() - fadeStartTime;
            float fadeProgress = Math.min(elapsedTime / (FADE_LENGTH * 1000f), 1.0f);

            // Apply fade effect
            if (fadeIn) {
                fadeAlpha = 1.0f - fadeProgress;  // Fade from black (1) to transparent (0)
            }

            // Clear the screen to prevent ghosting
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

            // Draw the background image
            mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            drawModalRectWithCustomSizedTexture(0, 0, 0, 0, this.width, this.height, this.width, this.height);

            // Render the buttons
            for (GuiButton button : this.buttonList) {
                button.drawButton(this.mc, mouseX, mouseY);
            }

            // Draw the fade effect (black overlay)
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(0.0f, 0.0f, 0.0f, fadeAlpha);  // Fade from black

            // Increase the size of the overlay
            int overlayWidth = this.width + 20000;  // Increase width by 100 pixels
            int overlayHeight = this.height + 20000;  // Increase height by 100 pixels
            int overlayX = -50;  // Move the overlay 50 pixels to the left
            int overlayY = -50;  // Move the overlay 50 pixels up

            drawModalRectWithCustomSizedTexture(overlayX, overlayY, 0, 0, overlayWidth, overlayHeight, overlayWidth, overlayHeight);

            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            switch (button.id) {
                case 1: // SinglePlayer
                    mc.displayGuiScreen(new net.minecraft.client.gui.GuiSelectWorld(this));
                    break;
                case 2: // Multiplayer
                    mc.displayGuiScreen(new net.minecraft.client.gui.GuiMultiplayer(this));
                    break;
                case 0: // Options
                    mc.displayGuiScreen(new net.minecraft.client.gui.GuiOptions(this, mc.gameSettings));
                    break;
                case 4: // Quit Game
                    mc.shutdown();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onGuiClosed() {
            super.onGuiClosed();
            // Reset the fade effect when transitioning back
            fadeIn = true;
            fadeAlpha = 1.0f;
            fadeStartTime = System.currentTimeMillis();
        }
    }

    public class CustomButton extends GuiButton {

        public CustomButton(int buttonId, int x, int y, int width, int height, String buttonText) {
            super(buttonId, x, y, width, height, buttonText);
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
                int fillColor = hovered ? HOVER_COLOR : BUTTON_COLOR;

                // Draw rounded rectangle
                drawRoundedRect(this.xPosition, this.yPosition, this.width, this.height, BUTTON_RADIUS, fillColor);

                // Draw button text
                int textColor = hovered ? 0xFFFFFF : 0xAAAAAA;
                this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, textColor);
            }
        }

        private void drawRoundedRect(int x, int y, int width, int height, int radius, int color) {
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // Fill the center rectangle
            drawRect(x + radius, y, x + width - radius, y + height, color);
            drawRect(x, y + radius, x + radius, y + height - radius, color);
            drawRect(x + width - radius, y + radius, x + width, y + height - radius, color);

            // Add rounded corners
            drawCircleSegment(x + radius, y + radius, radius, color, 0, 90);
            drawCircleSegment(x + width - radius, y + radius, radius, color, 90, 180);
            drawCircleSegment(x + radius, y + height - radius, radius, color, 270, 360);
            drawCircleSegment(x + width - radius, y + height - radius, radius, color, 180, 270);

            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
        }

        private void drawCircleSegment(int centerX, int centerY, int radius, int color, int startAngle, int endAngle) {
            float alpha = (color >> 24 & 255) / 255.0F;
            float red = (color >> 16 & 255) / 255.0F;
            float green = (color >> 8 & 255) / 255.0F;
            float blue = (color & 255) / 255.0F;

            GL11.glColor4f(red, green, blue, alpha);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);

            GL11.glVertex2d(centerX, centerY);  // Center of the circle
            for (int angle = startAngle; angle <= endAngle; angle += 5) {
                double radian = Math.toRadians(angle);
                double x = centerX + Math.cos(radian) * radius;
                double y = centerY + Math.sin(radian) * radius;
                GL11.glVertex2d(x, y);
            }

            GL11.glEnd();
        }
    }
}
