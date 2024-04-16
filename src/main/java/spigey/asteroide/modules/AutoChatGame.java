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

public class AutoChatGame extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Trigger messages").defaultValue("say", "type", "write").build());

    public AutoChatGame() {
        super(AsteroideAddon.CATEGORY, "auto-chatgame", "Automatically answers 'say [...]' chat games when triggered");
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void PacketReceive(PacketEvent.Receive event) {
        if (!(event.packet instanceof GameMessageS2CPacket)) {
            return;
        }
        if (((GameMessageS2CPacket) event.packet).content().toString().contains(mc.getSession().getUsername())) {
            return;
        }
        boolean yes = false;
        boolean no = !yes; // no = not yes;
        for (int i = 0; i < messages.get().size(); i++) {
            if (((GameMessageS2CPacket) event.packet).content().toString().contains(messages.get().get(i))) {
                yes = true;
            }
            no = !yes; // once again does not equal yes
        }
        if (no) {
            return;
        } // I just fucking felt like it
        if (!(((GameMessageS2CPacket) event.packet).content().toString().contains("\"") || ((GameMessageS2CPacket) event.packet).content().toString().contains("'"))) {
            return;
        } // help wtf is this does it even work??
        String fuckingkys = ((GameMessageS2CPacket) event.packet).content().toString()
            .split("literal")[2];
        String kys2 = fuckingkys
            .split(((GameMessageS2CPacket) event.packet).content().toString().contains("'") ? "'" : "\"")[0];
        if(kys2.substring(1, kys2.indexOf("}")).length() <= 1){return;}
        msg(kys2.substring(1, kys2.indexOf("}")));
    }
}
