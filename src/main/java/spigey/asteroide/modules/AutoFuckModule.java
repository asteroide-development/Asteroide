package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

public class AutoFuckModule extends Module {
    public AutoFuckModule() {
        super(AsteroideAddon.CATEGORY, "auto-fuck", "Automatically fucks other players");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> sneakTime = sgGeneral.add(new IntSetting.Builder()
        .name("sneak-time")
        .description("How many ticks to stay sneaked.")
        .defaultValue(1)
        .min(1)
        .sliderMin(1)
        .sliderMax(20)
        .max(100)
        .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("standing-time")
        .description("How many ticks to not stay sneaked.")
        .defaultValue(1)
        .min(1)
        .sliderMin(1)
        .sliderMax(20)
        .max(100)
        .build()
    );
    private int tick = -1;
    private boolean sneaking = false;
    @EventHandler
    private void onTick(TickEvent.Post event){
        if(tick > 0){tick--; return;}
        if(!isActive()) return;
        if(tick == -1){
            sneaking = true;
            tick = sneakTime.get();
            assert mc.player != null;
            mc.options.sneakKey.setPressed(true);
        }
        if(sneaking){
            sneaking = false;
            tick = delay.get();
            assert mc.player != null;
            mc.options.sneakKey.setPressed(false);
        } else{
            sneaking = true;
            tick = sneakTime.get();
            assert mc.player != null;
            mc.options.sneakKey.setPressed(true);
        }
    }
}


