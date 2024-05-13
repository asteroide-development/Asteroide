package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

public class BorderNoclipModule extends Module {
    public BorderNoclipModule() {
        super(AsteroideAddon.CATEGORY, "", "");
    }
    @EventHandler
    public void onTick(TickEvent.Post event){
        assert mc.world != null;
        info(mc.world.getWorldBorder().toString());
    }
}


