package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;

public class DevModule extends Module {
    public DevModule() {
        super(AsteroideAddon.CATEGORY, "dev", "What the fuck are you doing here??");
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (!(event.packet instanceof ClickSlotC2SPacket)) return;
        if(!AsteroideAddon.slotttt) return;
        ChatUtils.sendMsg(Text.of("§7SLOT " + ((ClickSlotC2SPacket) event.packet).getSlot()));
        ChatUtils.sendMsg(Text.of("§7REVISION " + ((ClickSlotC2SPacket) event.packet).getRevision()));
        ChatUtils.sendMsg(Text.of("§7SYNC ID " + ((ClickSlotC2SPacket) event.packet).getSyncId()));
        AsteroideAddon.slotttt = false;
    }
}
