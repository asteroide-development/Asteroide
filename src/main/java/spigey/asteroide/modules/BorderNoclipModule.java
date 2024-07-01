package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import spigey.asteroide.AsteroideAddon;

public class BorderNoclipModule extends Module {
    public BorderNoclipModule() {
        super(AsteroideAddon.CATEGORY, "border-noclip", "Removes the world border collision");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Double> damage = sgGeneral.add(new DoubleSetting.Builder()
        .name("damage-per-block")
        .description("Damage dealt per block when outside the world border")
        .defaultValue(0)
        .min(0)
        .sliderMax(10)
        .build()
    );
}
