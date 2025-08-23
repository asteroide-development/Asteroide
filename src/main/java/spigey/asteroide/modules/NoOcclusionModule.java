package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.AmbientOcclusionEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

public class NoOcclusionModule extends Module {
    public NoOcclusionModule() {
        super(AsteroideAddon.CATEGORY, "No-Occlusion", "Removes ambient occlusion.");
    }

    @EventHandler
    private void onAmbientOcclusion(AmbientOcclusionEvent event){ // wow, this was so hard to code!
        if(!isActive()) return;
        event.lightLevel = 1;
    }
}
