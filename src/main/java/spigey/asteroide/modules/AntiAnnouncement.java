package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

import static spigey.asteroide.util.banstuff;

public class AntiAnnouncement extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Keywords to filter").defaultValue("❙", "-------------").build());
    private final Setting<Boolean> mtmsg = sgGeneral.add(new BoolSetting.Builder()
        .name("Hide empty messages")
        .description("also prevents empty messages from showing in the chat")
        .defaultValue(true)
        .build()
    );

    public AntiAnnouncement() {
        super(AsteroideAddon.CATEGORY, "Anti-Announcement", "Prevents the chat from being flooded from automated messages");
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event){
        String content = event.getMessage().getString();
        for (int i = 0; i < messages.get().size(); i++) { if (content.toLowerCase().contains(messages.get().get(i).toLowerCase())) { event.cancel(); } }
        if (content.replaceAll(" ", "").replaceAll("\n", "").isEmpty() && mtmsg.get()) { event.cancel(); }
    }
}
