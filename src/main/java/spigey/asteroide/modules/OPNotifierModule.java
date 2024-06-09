package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerSessionC2SPacket;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;

import static spigey.asteroide.util.getPermissionLevel;

public class OPNotifierModule extends Module {
    public OPNotifierModule() {
        super(AsteroideAddon.CATEGORY, "op-notifier", "Tells you your permission level upon joining a server");
    }
    int tick = -1;
    @EventHandler
    private void onPacketSend(PacketEvent.Send event){
        if(!isActive()) return;
        if(!(event.packet instanceof PlayerSessionC2SPacket)) return;
        tick = 6;
    }
    @EventHandler
    private void onTick(TickEvent.Post event){
        if(!isActive()) return;
        tick--;
        if(tick != 0) return;
        ChatUtils.sendMsg(Text.of(switch(getPermissionLevel()){
            case 0 -> "§cYou do not have any permission on this server.";
            case 1 -> "§6Your permission level on this server is 1.";
            case 2 -> "§aYou have some permissions on this server.";
            case 3 -> "§9Your permission level on this server is 3.";
            case 4 -> "§eYou are opped on this server!";
            default -> "§4" + getPermissionLevel() + "???";
        }));
        tick = -1;
    }
}


