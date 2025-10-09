package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

public class WordFilterModule extends Module {
    public WordFilterModule() {
        super(AsteroideAddon.CATEGORY, "Word-Filter", "Filters words you send in the chat to prevent getting banned");
    }

    final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("messages to filter")
        .description("Filter these messages")
        .defaultValue("cum", "sex", "dick", "nigga", "nigger", "retard", "hitler")
        .build()
    );
    public final Setting<Boolean> woblox = sgGeneral.add(new BoolSetting.Builder()
        .name("Roblox-like Replacement")
        .description("Filters the message to look more like roblox filtering")
        .defaultValue(false)
        .build()
    );
    public final Setting<String> replacement = sgGeneral.add(new StringSetting.Builder()
        .name("filter replacement")
        .description("String to replace filtered messages with")
        .defaultValue("@$#!?&")
        .visible(() -> !woblox.get())
        .build()
    );
    public final Setting<String> roblock = sgGeneral.add(new StringSetting.Builder()
        .name("roblox-like filter replacement")
        .description("String to replace filtered messages with")
        .defaultValue("#")
        .visible(() -> woblox.get())
        .build()
    );
    private final Setting<Type> typee = sgGeneral.add(new EnumSetting.Builder<Type>()
        .name("type")
        .description("Which way to not say the filtered words")
        .defaultValue(Type.Censor)
        .build()
    );

    private enum Type {
        Censor,
        Bypass
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) throws Exception {
        String[] datshit = event.message.split(" ");
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < datshit.length; i++) {
            for (int j = 0; j < messages.get().size(); j++) {
                if (datshit[i].toLowerCase().contains(messages.get().get(j).toLowerCase())) {
                    if(typee.get() == Type.Bypass){ datshit[i] = datshit[i].replaceAll("a", "а").replaceAll("c", "с").replaceAll("e", "е").replaceAll("h", "һ").replaceAll("i", "і").replaceAll("j", "ј").replaceAll("n", "ո").replaceAll("o", "о").replaceAll("p", "р").replaceAll("u", "ս").replaceAll("v", "ν").replaceAll("x", "х").replaceAll("y", "у"); }
                    else if (woblox.get()) {
                        StringBuilder temp = new StringBuilder();
                        for (int k = 0; k < datshit[i].length(); k++) {
                            temp.append(roblock.get());
                        }
                        datshit[i] = temp.toString();
                    } else {
                        datshit[i] = replacement.get();
                    }
                }
            }
        }
        for (String s : datshit) message.append(s).append(" ");
        event.message = message.toString().trim();
    }
}
