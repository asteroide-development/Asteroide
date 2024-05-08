package spigey.asteroide.modules;

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
        super(AsteroideAddon.CATEGORY, "anti-announcement", "Prevents the chat from being flooded from automated messages");
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void PacketReceive(PacketEvent.Receive event){
        banstuff();
        if(event.packet instanceof GameMessageS2CPacket) {
            String content = String.valueOf(((GameMessageS2CPacket) event.packet).content().getString());
            for (int i = 0; i < messages.get().size(); i++) {
                if (content.toLowerCase().contains(messages.get().get(i).toLowerCase())) {
                    event.cancel();
                }
            }
            if (content.replaceAll(" ", "").replaceAll("\n", "") == "" && mtmsg.get()) {
                event.cancel();
            }
        } else if(event.packet instanceof ChatMessageS2CPacket){
            String content = String.valueOf(event.packet.toString());
            for(int i = 0; i < messages.get().size(); i++){
                if(content.toLowerCase().contains(messages.get().get(i).toLowerCase())){event.cancel();}
            }
            if(content.replaceAll(" ", "").replaceAll("\\n", "").replaceAll("\n", "").equals("") && mtmsg.get()){event.cancel();}
        }
    }
}
