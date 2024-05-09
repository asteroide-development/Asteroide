package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;
import meteordevelopment.meteorclient.systems.config.Config;

import java.util.List;

import static spigey.asteroide.util.banstuff;
import static spigey.asteroide.util.msg;

public class AutoMacro extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("messages")
        .description("Keywords to execute the macro")
        .defaultValue("Hello world!")
        .build()
    );
    private final Setting<List<String>> macro = sgGeneral.add(new StringListSetting.Builder()
        .name("macro")
        .description("macro to execute")
        .defaultValue("testmacro")
        .build()
    );

    public AutoMacro() {
        super(AsteroideAddon.CATEGORY, "auto-macro", "Automatically runs a macro when a specified message is sent in the chat");
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void PacketReceive(PacketEvent.Receive event){
        banstuff();
        if(!isActive()) return;
        if(!(event.packet instanceof ChatMessageS2CPacket)){return;}
        String content = String.valueOf(event.packet.toString());
        for(int i = 0; i < messages.get().size(); i++){
            if(content.toLowerCase().contains(messages.get().get(i).toLowerCase())){
                if(macro.get().get(i) != null){
                    msg(Config.get().prefix.get() + "macro " + macro.get().get(i));
                } else{
                    error("Error: Macro " + macro.get().get(i) + " is null");
                }
            }
        }
    }
}
