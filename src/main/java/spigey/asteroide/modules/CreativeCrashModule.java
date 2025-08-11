package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import spigey.asteroide.AsteroideAddon;
import net.minecraft.block.entity.CommandBlockBlockEntity;

public class CreativeCrashModule extends Module {
    public CreativeCrashModule() {
        super(AsteroideAddon.CATEGORY, "Server-Fuck", "Crashes the server without using OP. Creative mode required");
    }
    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof GameMessageS2CPacket))
            return;
        Text message = ((GameMessageS2CPacket)event.packet).content();
        if (message.getContent() instanceof TranslatableTextContent) {
            String key = ((TranslatableTextContent)message.getContent()).getKey();
            if (key.equals("advMode.notEnabled")) {
                ChatUtils.info("Command blocks are deactivated");
                event.cancel();
            } else if (key.equals("advMode.notAllowed") || key.equals("advMode.setCommand.success")) {
                ChatUtils.info("Command blocks are activated");
                event.cancel();
            }
        }
    }
}


