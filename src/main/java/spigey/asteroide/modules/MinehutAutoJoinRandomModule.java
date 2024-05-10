package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import spigey.asteroide.AsteroideAddon;

public class MinehutAutoJoinRandomModule extends Module {
    public MinehutAutoJoinRandomModule() {
        super(AsteroideAddon.CATEGORY, "minehut-auto-join", "Automatically joins random minehut servers when in the lobby");
    }
    private void onPacketSend(PacketEvent.Send event){
        if(!(event.packet instanceof ClickSlotC2SPacket)) return;
        info(((ClickSlotC2SPacket) event.packet).getSlot() + ", " + ((ClickSlotC2SPacket) event.packet).getRevision() + ", " + ((ClickSlotC2SPacket) event.packet).getSyncId());
    }
}


