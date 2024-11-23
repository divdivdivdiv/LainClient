//My god i hate java WHY IS THIS SHIT SO LONG
package com.lainclient.modules.movement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;

// i cant for the fucking life of me get subscribe event working so im registering every single FUCKING MODULE
@Mod(modid="Speed", name = "Speed")
public class Speed {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static boolean movementEnabled = false;
    private static final EnumChatFormatting COLOR = EnumChatFormatting.DARK_GRAY;
    private static KeyBinding toggleMovementKey;

    private static final float GROUND_SPEED = 0.5f;//The speed for when the player is on the ground
    private static final float AIR_SPEED = 0.34f;//The speed for when the player is not on the ground °o°

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        toggleMovementKey = new KeyBinding("Speed", mc.gameSettings.keyBindSprint.getKeyCode(), "LainClient");
        ClientRegistry.registerKeyBinding(toggleMovementKey);
    }

    @SubscribeEvent
    public void onPlayerUpdate(LivingUpdateEvent event) {
        if (!(event.entity instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.entity;

        if (toggleMovementKey.isPressed()) {
            toggleSpeed();
        }

        if (!movementEnabled || mc.currentScreen != null) {
            limitSpeed(player, 1.0f);
            return;
        }

        handleMovement(player);
    }

    public static void toggleSpeed() {
        movementEnabled = !movementEnabled;

        String message = movementEnabled ?
                String.format("%s[LC]%s %s", COLOR, EnumChatFormatting.GREEN, "Speed Enabled") :
                String.format("%s[LC]%s %s", COLOR, EnumChatFormatting.RED, "Speed Disabled");

        mc.thePlayer.addChatMessage(new ChatComponentText(message));
        mc.thePlayer.playSound("gui.button.press", 2.5F, 2.5F);
    }

    private void handleMovement(EntityPlayer player) {
        boolean forward = mc.gameSettings.keyBindForward.isKeyDown();
        boolean left = mc.gameSettings.keyBindLeft.isKeyDown();
        boolean backward = mc.gameSettings.keyBindBack.isKeyDown();
        boolean right = mc.gameSettings.keyBindRight.isKeyDown();

        if (!forward && !left && !backward && !right) {
            gradualSlowDown(player, 0.05f);
            return;
        }

        float speedLimit = player.onGround ? GROUND_SPEED : AIR_SPEED;

        if (player.onGround) {
            player.jump();
        }

        adjustPlayerMotion(player, speedLimit, forward, left, backward, right);
        limitSpeed(player, speedLimit);
    }

    private void adjustPlayerMotion(EntityPlayer player, float speed, boolean forward, boolean left, boolean backward, boolean right) {
        double yaw = Math.toRadians(player.rotationYaw);

        double sinYaw = Math.sin(yaw);
        double cosYaw = Math.cos(yaw);

        if (forward) {
            player.motionX -= sinYaw * speed;
            player.motionZ += cosYaw * speed;
        }
        if (backward) {
            player.motionX += sinYaw * speed;
            player.motionZ -= cosYaw * speed;
        }
        if (left) {
            player.motionX += cosYaw * speed;
            player.motionZ += sinYaw * speed;
        }
        if (right) {
            player.motionX -= cosYaw * speed;
            player.motionZ -= sinYaw * speed;
        }
    }

    private void gradualSlowDown(EntityPlayer player, float decel) {
        player.motionX = adjustToZero(player.motionX, decel);
        player.motionZ = adjustToZero(player.motionZ, decel);
    }

    private double adjustToZero(double value, float amount) {
        if (value > 0) return Math.max(0, value - amount);
        else if (value < 0) return Math.min(0, value + amount);
        return value;
    }

    private void limitSpeed(EntityPlayer player, float maxSpeed) {
        double currentSpeed = Math.sqrt(player.motionX * player.motionX + player.motionZ * player.motionZ);
        if (currentSpeed > maxSpeed) {
            double scale = maxSpeed / currentSpeed;
            player.motionX *= scale;
            player.motionZ *= scale;
        }
    }
}
