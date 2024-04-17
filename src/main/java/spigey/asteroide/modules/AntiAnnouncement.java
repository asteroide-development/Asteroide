package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;

import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.msg;

public class AntiAnnouncement extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Keywords to filter").defaultValue("❙", "-------------").build());

    public AntiAnnouncement() {
        super(AsteroideAddon.CATEGORY, "anti-announcement", "Prevents the chat from being flooded from automated messages");
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void PacketReceive(PacketEvent.Receive event){
        if(!(event.packet instanceof GameMessageS2CPacket)){return;}
        String content = String.valueOf(((GameMessageS2CPacket) event.packet).content().getString());
        for(int i = 0; i < messages.get().size(); i++){
            if(content.toLowerCase().contains(messages.get().get(i).toLowerCase())){event.cancel();}
        }
    }
}
