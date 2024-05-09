package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.PlayerMoveC2SPacketAccessor;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

public class CreativeFlightModule extends Module {
    public CreativeFlightModule() {
        super(AsteroideAddon.CATEGORY, "creative-flight", "Allows you to fly like you're in creative mode");
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if(!isActive()) return;
        assert mc.player != null;
        mc.player.getAbilities().allowFlying = true;
    }
    @Override
    public void onDeactivate() {
        assert mc.player != null;
        if(!mc.player.getAbilities().creativeMode) {mc.player.getAbilities().allowFlying = false; mc.player.getAbilities().flying = false;}
    }
    @EventHandler
    private void onSendPacket(PacketEvent.Send event){
        if(!(event.packet instanceof PlayerMoveC2SPacketAccessor)) return;
        if(!isActive()) return;
        ((PlayerMoveC2SPacketAccessor) event.packet).setOnGround(true);
    }
}
