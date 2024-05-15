package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.login.LoginHelloC2SPacket;
import spigey.asteroide.AsteroideAddon;

import static spigey.asteroide.util.getPermissionLevel;

public class OpNotifierModule extends Module {
    public OpNotifierModule() {
        super(AsteroideAddon.CATEGORY, "op-notifier", "Tells you when you join a server you're opped on");
    }
    @EventHandler
    private void onPacketSend(PacketEvent.Send event){
        if(!(event.packet instanceof LoginHelloC2SPacket)) return;
        info(getPermissionLevel() + "");
    }
}


