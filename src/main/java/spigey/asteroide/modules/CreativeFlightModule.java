package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

public class CreativeFlightModule extends Module {
    public CreativeFlightModule() {
        super(AsteroideAddon.CATEGORY, "creative-flight", "Allows you to fly like you're in creative mode");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("fly-speed")
        .description("How fast you can fly.")
        .defaultValue(0.05)
        .min(0)
        .sliderMax(20)
        .build()
    );
    @EventHandler
    public void onTick(TickEvent.Post event) {
        if(!isActive()) return;
        assert mc.player != null;
        mc.player.getAbilities().allowFlying = true;
        mc.player.getAbilities().setFlySpeed(Float.parseFloat(speed.get().toString()));
    }
    @Override
    public void onDeactivate() {
        assert mc.player != null;
        mc.player.getAbilities().setFlySpeed(0.05F);
        if(!mc.player.getAbilities().creativeMode) {mc.player.getAbilities().allowFlying = false; mc.player.getAbilities().flying = false;}
    }
    @EventHandler
    private void onSendPacket(PacketEvent.Send event){
        if(!(event.packet instanceof PlayerMoveC2SPacketAccessor)) return;
        if(!isActive()) return;
        ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
    }
}
