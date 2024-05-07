package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

public class PlatformFlyModule extends Module {
    public PlatformFlyModule() {
        super(AsteroideAddon.CATEGORY, "platform-fly", "Stops you from falling");
    }
    private int level;

    @Override
    public void onActivate() {
        assert mc.player != null;
        level = mc.player.getBlockPos().getY();
    }
    @EventHandler
    private void onKey(KeyEvent event){
        if(event.action != KeyAction.Press) return;
        if(mc.options.sneakKey.matchesKey(event.key, 0)){level--; return;}
        assert mc.player != null;
        level = mc.player.getBlockPos().getY();
    }
    @EventHandler
    private void onTick(TickEvent.Pre event){
        assert mc.player != null;
        if(mc.player.isOnGround()) return;
        if(mc.player.getBlockPos().getY() < level) mc.player.jump();
    }
}


