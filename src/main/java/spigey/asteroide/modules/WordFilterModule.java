package spigey.asteroide.modules;

import meteordevelopment.meteorclient.MeteorClient;
import spigey.asteroide.events.SendMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

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
    private final Setting<Boolean> woblox = sgGeneral.add(new BoolSetting.Builder()
        .name("Roblox-like Replacement")
        .description("Filters the message to look more like roblox filtering")
        .defaultValue(false)
        .build()
    );
    private final Setting<String> replacement = sgGeneral.add(new StringSetting.Builder()
        .name("filter replacement")
        .description("String to replace filtered messages with")
        .defaultValue("@$#!?&")
        .visible(() -> !woblox.get())
        .build()
    );
    private final Setting<String> roblock = sgGeneral.add(new StringSetting.Builder()
        .name("roblox-like filter replacement")
        .description("String to replace filtered messages with")
        .defaultValue("#")
        .visible(() -> woblox.get())
        .build()
    );
    private boolean activated = false;
    @Override
    public void onActivate() {
        activated = false;
        if(activated){info("Already activated"); return;}
        MeteorClient.EVENT_BUS.subscribe(this);
        info("Subscribed! Hit that bell too.");
        activated = true;
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        info("Someone has sent a message");
        if(!isActive()){return;}
        info("Alr it's activated ig");
        String[] datshit = event.message.split(" ");
        String message = "";
        for(int i = 0; i < datshit.length; i++){
            for(int j = 0; j < messages.get().size(); j++){
                if(datshit[i].toLowerCase().contains(messages.get().get(j).toLowerCase())) {
                    if (woblox.get()) {
                        String temp = "";
                        for(int k = 0; k < datshit[i].length(); k++){
                            temp += roblock.get();
                        }
                        datshit[i] = temp;
                    } else {
                        datshit[i] = replacement.get();
                    }
                }
            }
        }
        for(int i = 0; i < datshit.length; i++){
            message += datshit[i] + " ";
        }
        info("ok now changing the text");
        event.message = message.trim();
    }
}
