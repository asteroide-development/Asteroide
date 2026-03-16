package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import spigey.asteroide.AsteroideAddon;

import java.util.ArrayList;
import java.util.Set;

public class PacketPauseModule extends Module {
    public PacketPauseModule() { super(AsteroideAddon.CATEGORY, "Packet-Pause", "Pauses packets until the module is disabled."); }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Set<Class<? extends Packet<?>>>> packets = sgGeneral.add(new PacketListSetting.Builder()
        .name("packets")
        .description("Packets to pause.")
        .filter(aClass -> PacketUtils.getC2SPackets().contains(aClass))
        .build()
    );
    private final Setting<Boolean> feedback = sgGeneral.add(new BoolSetting.Builder()
        .name("Feedback")
        .description("Tells you how many packets were sent")
        .defaultValue(true)
        .build()
    );

    private ArrayList<Packet<?>> pausedPackets = new ArrayList<>();

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (!isActive() || !packets.get().contains(event.packet.getClass())) return;
        pausedPackets.add(event.packet);
        event.cancel();
    }

    @Override
    public void onDeactivate() {
        if (mc.player == null || mc.world == null) { pausedPackets.clear(); return; }
        for (Packet<?> packet : pausedPackets) mc.player.networkHandler.sendPacket(packet);
        if(feedback.get()) info("Sent " + pausedPackets.size() + " paused packets.");
        pausedPackets.clear();
    }
}
