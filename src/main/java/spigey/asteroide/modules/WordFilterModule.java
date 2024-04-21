package spigey.asteroide.modules;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;

import java.util.List;
import java.util.regex.Pattern;

import static spigey.asteroide.util.msg;

public class WordFilterModule extends Module {
    public WordFilterModule() {
        super(AsteroideAddon.CATEGORY, "word-filter", "Filters words you send in the chat to prevent getting banned");
    }
    final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("messages to filter")
        .description("Filter these messages")
        .defaultValue("cum", "sex", "dick", "nigga", "nigger", "retard", "hitler")
        .build()
    );
    private final Setting<String> replacement = sgGeneral.add(new StringSetting.Builder()
        .name("filter replacement")
        .description("String to replace filtered messages with")
        .defaultValue("@$#!?&")
        .build()
    );
    boolean activated = false;
    @Override
    public void onActivate() {
        if(activated){return;}
        MeteorClient.EVENT_BUS.subscribe(this);
        activated = true;
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        if(!isActive()){return;}
        String[] datshit = event.message.split(" ");
        String message = "";
        for(int i = 0; i < datshit.length; i++){
            for(int j = 0; j < messages.get().size(); j++){
                if(datshit[i].toLowerCase().contains(messages.get().get(j).toLowerCase())){datshit[i] = replacement.get();}
            }
        }
        for(int i = 0; i < datshit.length; i++){
            message += datshit[i] + " ";
        }
        event.message = message.trim();
    }
}
