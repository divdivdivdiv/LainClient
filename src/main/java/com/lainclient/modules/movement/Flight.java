package com.lainclient.modules.movement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// i cant for the fucking life of me get subscribe event working so im registering every single FUCKING MODULE
@Mod(modid="Flight", name="Flight")
public class Flight {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static boolean flightEnabled = false;
    private static final float flightSpeed = 2f;  // Flight speed while moving
    private static final EnumChatFormatting COLOR = EnumChatFormatting.DARK_GRAY;

    private static KeyBinding toggleFlightKey;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        // Create a new key binding for the Flight toggle under LainClient category
        toggleFlightKey = new KeyBinding("Flight", mc.gameSettings.keyBindSprint.getKeyCode(), "LainClient");
        ClientRegistry.registerKeyBinding(toggleFlightKey);
    }

    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event) {
        if (!(event.entity instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.entity;

        // Check if the Flight toggle key is pressed
        if (toggleFlightKey.isPressed()) {
            toggleFlight();
        }

        // Handle flight if enabled
        if (flightEnabled) {
            if (player.onGround) {
                player.motionY = 0;  // Prevent falling when on the ground while flying
            }
            handleFlight(player);
        }


    }

    // Toggle flight on/off
    public static void toggleFlight() {
        flightEnabled = !flightEnabled;

        // Send a chat message with the current flight status
        String message = flightEnabled ?
                String.format("%s[LC]%s %s", COLOR, EnumChatFormatting.GREEN, "Flight Enabled") :
                String.format("%s[LC]%s %s", COLOR, EnumChatFormatting.RED, "Flight Disabled");

        mc.thePlayer.addChatMessage(new ChatComponentText(message));
        mc.thePlayer.playSound("gui.button.press", 2.5F, 2.5F);
    }

    // Handle flight logic, including movement and speed
    private void handleFlight(EntityPlayer player) {
        boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
        boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean backward = mc.gameSettings.keyBindBack.isKeyDown();
        boolean right = mc.gameSettings.keyBindRight.isKeyDown();
        boolean up = mc.gameSettings.keyBindJump.isKeyDown();  // Fly up
        boolean down = mc.gameSettings.keyBindSneak.isKeyDown(); // Fly down

        // Apply upward force if the "Jump" key (up) is pressed
        if (up) {
            player.motionY = flightSpeed;  // Move the player upwards
        } else if (down) {
            player.motionY = -flightSpeed;  // Move the player downwards
        } else {
            player.motionY = 0;  // No vertical movement if no up/down key is pressed
        }

        // Apply movement forces based on direction keys
        applyMovementForce(player, forward, left, backward, right);

        // Cap the player's speed so it doesn't exceed flightSpeed
        limitFlightSpeed(player);

        // Instant deceleration when no movement keys are pressed
        if (!forward && !left && !backward && !right) {
            player.motionX = 0;
            player.motionZ = 0;
        }
    }

    // Apply movement forces in corresponding directions
    private void applyMovementForce(EntityPlayer player, boolean forward, boolean left, boolean backward, boolean right) {
        double yaw = Math.toRadians(player.rotationYaw);

        // Apply forward/backward movement
        if (forward) {
            player.motionX -= Math.sin(yaw) * flightSpeed;
            player.motionZ += Math.cos(yaw) * flightSpeed;
        }
        if (backward) {
            player.motionX += Math.sin(yaw) * flightSpeed;
            player.motionZ -= Math.cos(yaw) * flightSpeed;
        }

        // Apply left/right movement
        if (left) {
            player.motionX += Math.cos(yaw) * flightSpeed;
            player.motionZ += Math.sin(yaw) * flightSpeed;
        }
        if (right) {
            player.motionX -= Math.cos(yaw) * flightSpeed;
            player.motionZ -= Math.sin(yaw) * flightSpeed;
        }
    }

    // Limit the player's speed to the flightSpeed so it doesn't exceed the set value
    private void limitFlightSpeed(EntityPlayer player) {
        double currentSpeed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);

        // If the player's speed exceeds the flightSpeed, scale the motion to the max speed
        if (currentSpeed > flightSpeed) {
            double scale = flightSpeed / currentSpeed;
            player.motionX *= scale;
            player.motionZ *= scale;
        }
    }
}
