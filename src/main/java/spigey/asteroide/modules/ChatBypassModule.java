package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;
import java.util.List;

public class ChatBypassModule extends Module {
    public ChatBypassModule() {
        super(AsteroideAddon.CATEGORY, "Chat-Bypass", "Bypasses most chat filters.");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> toggleOnMessage = sgGeneral.add(new BoolSetting.Builder()
        .name("toggle on msg")
        .description("Toggles the module if a specific message is received")
        .defaultValue(false)
        .build()
    );
    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("messages")
        .description("Toggle the module if any of the messages is received.")
        .defaultValue("special characters in the chat")
        .visible(toggleOnMessage::get)
        .build()
    );

    @EventHandler
    private void onMessageSend(SendMessageEvent event){
        if(!isActive()) return;
        event.message = event.message.replaceAll("a", "а").replaceAll("c", "с").replaceAll("e", "е").replaceAll("h", "һ").replaceAll("i", "і").replaceAll("j", "ј").replaceAll("n", "ո").replaceAll("o", "о").replaceAll("p", "р").replaceAll("u", "ս").replaceAll("v", "ν").replaceAll("x", "х").replaceAll("y", "у");
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event){
        if(!isActive() || !toggleOnMessage.get() || messages.get().isEmpty()) return;
        for(String message : messages.get()) if(event.getMessage().getString().toLowerCase().contains(message.toLowerCase())) toggle();
    }
}
