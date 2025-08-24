package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.AmbientOcclusionEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

public class NoOcclusionModule extends Module {
    public NoOcclusionModule() {
        super(AsteroideAddon.CATEGORY, "No-Occlusion", "Removes ambient occlusion.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> lightLevel = sgGeneral.add(new IntSetting.Builder().name("Light Level").description("The light level to set.").defaultValue(1).sliderMin(0).sliderMax(15).onChanged((value) -> mc.worldRenderer.reload()).build());

    @Override public void onActivate(){ mc.worldRenderer.reload(); }
    @Override public void onDeactivate(){ mc.worldRenderer.reload(); }

    @EventHandler
    private void onAmbientOcclusion(AmbientOcclusionEvent event){ // wow, this was so hard to code!
        if(!isActive()) return;
        event.lightLevel = lightLevel.get();
    }
}
