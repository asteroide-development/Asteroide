package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.lwjgl.glfw.GLFW;
import spigey.asteroide.AsteroideAddon;

public class PlatformFlyModule extends Module {
    public PlatformFlyModule() {
        super(AsteroideAddon.CATEGORY, "platform-fly", "Stops you from falling");
    }
    private int level;
    private boolean uwu;

    @Override
    public void onActivate() {
        assert mc.player != null;
        level = mc.player.getBlockPos().getY();
    }
    @EventHandler
    private void onTick(TickEvent.Pre event){
        assert mc.player != null;
        if(mc.player.getVelocity().getY() == 0) level = mc.player.getBlockPos().getY();
        if(Input.isKeyPressed(GLFW.GLFW_KEY_SPACE)){level++;}
        if(Input.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT) && !mc.player.isOnGround()){level--;}
        if(mc.player.getBlockPos().getY() <= level){
            /* mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), level, mc.player.getZ(), true));
            mc.player.setPosition(mc.player.getX(), level, mc.player.getZ()); */
            if(mc.player.getVelocity().y < 0) mc.player.setVelocity(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
        }
    }
}


