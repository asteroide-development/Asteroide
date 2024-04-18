package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
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
    private final Setting<List<String>> quotes = sgGeneral.add(new StringListSetting.Builder().name("quotes").description("Quotes").defaultValue("\"", "'", "`").build());
    private final Setting<Boolean> contain = sgGeneral.add(new BoolSetting.Builder()
        .name("Must contain 'Chat'")
        .description("Requires the message to contain 'Chat'")
        .defaultValue(true)
        .build()
    );
    public AutoChatGame() {
        super(AsteroideAddon.CATEGORY, "auto-chatgame", "Automatically answers 'say [...]' chat games when triggered");
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void PacketReceive(PacketEvent.Receive event) {
        if (!(event.packet instanceof GameMessageS2CPacket)) {return;}
        String content = String.valueOf(((GameMessageS2CPacket) event.packet).content().getString());
        boolean yes = false;
        boolean no = false;
        boolean reverse = false;
        String quote = "";
        for(int i = 0; i < messages.get().size(); i++){
            if(content.toLowerCase().contains(messages.get().get(i).toLowerCase())){yes = true;}
            if(content.toLowerCase().contains("reverse")){yes = true; reverse = true;}
            if(contain.get() && !content.toLowerCase().contains("chat")){yes = false;}
        }
        for(int i = 0; i < quotes.get().size(); i++){
            if(content.toLowerCase().contains(quotes.get().get(i))){no = true; quote = quotes.get().get(i);}
        }
        if(yes && no){
            if(reverse){
                msg(String.valueOf(new StringBuilder(content.split(quote)[1]).reverse()));
            } else {
                msg(content.split(quote)[1]);
            }
        }
    }
}
