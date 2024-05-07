package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
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
    private void onKey(KeyEvent event){
        if(event.action != KeyAction.Press && event.action != KeyAction.Repeat) return;
        if(mc.options.sneakKey.matchesKey(event.key, 0)){level--; mc.player.setVelocity(mc.player.getVelocity().x, -1, mc.player.getVelocity().z); return;}
        if(!mc.options.jumpKey.matchesKey(event.key, 0)){level++; return;}
        assert mc.player != null;
        if(mc.player.isOnGround()) level = mc.player.getBlockPos().getY();
    }
    @EventHandler
    private void onTick(TickEvent.Pre event){
        assert mc.player != null;
        if(mc.player.isOnGround()) return;
        if(mc.player.getBlockPos().getY() <= level){
            /* mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), level, mc.player.getZ(), true));
            mc.player.setPosition(mc.player.getX(), level, mc.player.getZ()); */
            if(mc.player.getVelocity().y < 0) mc.player.setVelocity(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
        }
    }
}


