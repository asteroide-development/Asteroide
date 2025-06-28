package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

public class DevModule extends Module {
    public DevModule() {
        super(AsteroideAddon.CATEGORY, "dev", "What the fuck are you doing here??");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> testEvents = sgGeneral.add(new BoolSetting.Builder()
        .name("Test Events")
        .description("Tests SendMessageEvent")
        .defaultValue(false)
        .build()
    );

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (!(event.packet instanceof ClickSlotC2SPacket)) return;
        if(!AsteroideAddon.slotttt) return;
        ChatUtils.sendMsg(Text.of("§cSLOT " + ((ClickSlotC2SPacket) event.packet).getSlot()));
        ChatUtils.sendMsg(Text.of("§aREVISION " + ((ClickSlotC2SPacket) event.packet).getRevision()));
        ChatUtils.sendMsg(Text.of("§9SYNC ID " + ((ClickSlotC2SPacket) event.packet).getSyncId()));
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) throws Exception {
        if(!testEvents.get()) return;
        event.message = event.message.replaceAll("a", "а")
            .replaceAll("c", "с")
            .replaceAll("e", "е")
            .replaceAll("h", "һ")
            .replaceAll("i", "і")
            .replaceAll("j", "ј")
            .replaceAll("n", "ո")
            .replaceAll("o", "о")
            .replaceAll("p", "р")
            .replaceAll("u", "ս")
            .replaceAll("v", "ν")
            .replaceAll("x", "х")
            .replaceAll("y", "у") + " h";
    }
}
