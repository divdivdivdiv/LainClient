//How does this dogshit code bypass a £125 A MONTH anticheat
package com.lainclient.modules.movement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

// i cant for the fucking life of me get subscribe event working so im registering every single FUCKING MODULE
@Mod(modid = "LongJump", name = "LongJump")
public class LongJump {

    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final KeyBinding TOGGLE_LONGJUMP = new KeyBinding("LongJump", Keyboard.KEY_F, "LainClient");//Keybind manager
    private boolean isLongJumpEnabled = false;
    private long longJumpEnabledTime = 0;
    private static final long LONG_JUMP_TIMEOUT = 3000;
    private long itemUseSentTime = 0;
    private static final long FORCE_DELAY = 500;
    private boolean forceApplied = false;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ClientRegistry.registerKeyBinding(TOGGLE_LONGJUMP);
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (TOGGLE_LONGJUMP.isPressed()) {
            isLongJumpEnabled = !isLongJumpEnabled;
            if (isLongJumpEnabled) {
                longJumpEnabledTime = System.currentTimeMillis();
                itemUseSentTime = System.currentTimeMillis();
                simulateItemUseOnBlockBelow();
                forceApplied = false;
                sendToggleMessage();
            } else {
                resetMotion();
                sendToggleMessage();
            }
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (isLongJumpEnabled) {
            long elapsedTime = System.currentTimeMillis() - longJumpEnabledTime;
            if (elapsedTime > LONG_JUMP_TIMEOUT) {
                isLongJumpEnabled = false;
                resetMotion();
                sendToggleMessage();
            }

            if (System.currentTimeMillis() - itemUseSentTime >= FORCE_DELAY && !forceApplied) {
                applyForce();
                forceApplied = true;
            }
        }
    }

    private void applyForce() {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null && isLongJumpEnabled) {
            float yaw = player.rotationYaw;
            double horizontalForce = 7;
            player.motionX += -Math.sin(Math.toRadians(yaw)) * horizontalForce;
            player.motionZ += Math.cos(Math.toRadians(yaw)) * horizontalForce;
            double verticalForce = 0.7;
            player.motionY += verticalForce;
        }
    }

    private void resetMotion() {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null) {
            player.motionX = 0;
            player.motionY = 0;
            player.motionZ = 0;
        }
    }

    private void sendToggleMessage() {
        String message = isLongJumpEnabled ? "Long jump enabled" : "Long jump disabled";
        EnumChatFormatting color = isLongJumpEnabled ? EnumChatFormatting.GREEN : EnumChatFormatting.RED;
        mc.thePlayer.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_GRAY + "[LC] " + color + message));
    }
    //Logic to simulate using a fireball under the players feet (this shit does not work Lmao)
    private void simulateItemUseOnBlockBelow() {
        EntityPlayerSP player = mc.thePlayer;
        if (player != null) {
            BlockPos blockBelow = new BlockPos(player.posX, player.posY - 2, player.posZ);
            NetHandlerPlayClient netHandler = mc.getNetHandler();
            C08PacketPlayerBlockPlacement packet = new C08PacketPlayerBlockPlacement(
                blockBelow,
                EnumFacing.UP.ordinal(),
                player.getHeldItem(),
                0.0F, 0.0F, 0.0F
            );
            netHandler.addToSendQueue(packet);
        }
    }
}

