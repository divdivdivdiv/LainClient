package com.example.render;

import com.example.movement.Flight;
import com.example.movement.Speed; // Import the Speed class
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class GuiExample {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final KeyBinding openGuiKey = new KeyBinding("key.opengui", Keyboard.KEY_Y, "key.categories.gui_example");
    private static final KeyBinding detectKeyG = new KeyBinding("key.detectG", Keyboard.KEY_G, "key.categories.gui_example");
    private static boolean isGuiOpen = false;

    public GuiExample() {
        MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(openGuiKey);
        ClientRegistry.registerKeyBinding(detectKeyG);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (openGuiKey.isPressed()) {
            if (!isGuiOpen) {
                mc.displayGuiScreen(new ExampleGuiScreen());
            } else {
                mc.displayGuiScreen(null);
            }
            isGuiOpen = !isGuiOpen;
        }

        if (detectKeyG.isPressed()) {
            System.out.println("G key was pressed outside of the GUI!");
            Flight.toggleFlight();
        }

        // Check if the "N" key is pressed to toggle speed
        if (Keyboard.isKeyDown(Keyboard.KEY_N)) {
            Speed.toggleSpeed(); // Toggle speed when "N" is pressed
        }
    }

    public static class ExampleGuiScreen extends GuiScreen {
        private GuiButton flightToggleButton;
        private GuiButton speedToggleButton;

        @Override
        public void initGui() {
            super.initGui();
            int buttonWidth = 100;
            int buttonHeight = 20;
            int buttonX = (this.width - buttonWidth) / 2;
            int flightButtonY = (this.height / 2) + 20;
            int speedButtonY = flightButtonY + 30; // Position the speed button below the flight button

            this.buttonList.clear();
            this.flightToggleButton = new GuiButton(0, buttonX, flightButtonY, buttonWidth, buttonHeight, "Flight On/Off");
            this.speedToggleButton = new GuiButton(1, buttonX, speedButtonY, buttonWidth, buttonHeight, "Speed On/Off");
            this.buttonList.add(this.flightToggleButton);
            this.buttonList.add(this.speedToggleButton);
        }

        @Override
        protected void actionPerformed(GuiButton button) {
            if (button.id == 0) {
                Flight.toggleFlight(); // Toggle flight mode
            } else if (button.id == 1) {
                Speed.toggleSpeed(); // Toggle speed mode
            }
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            String displayText = "LainClient dogshit gui";
            int textX = this.width / 2;
            int textY = this.height / 2 - 20;
            drawCenteredString(this.fontRendererObj, displayText, textX, textY, 0xFFFFFF);

            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean doesGuiPauseGame() {
            return false;
        }

        @Override
        protected void keyTyped(char typedChar, int keyCode) {
            if (keyCode == Keyboard.KEY_ESCAPE || keyCode == openGuiKey.getKeyCode()) {
                mc.displayGuiScreen(null);
                isGuiOpen = false;
            }
        }
    }
}

