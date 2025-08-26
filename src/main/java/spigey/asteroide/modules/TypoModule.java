package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

public class TypoModule extends Module {
    public TypoModule() {
        super(AsteroideAddon.CATEGORY, "Typo", "Replace text in sent messages/commands.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<List<String>> keywords = sgGeneral.add(new StringListSetting.Builder().name("keywords").description("Keywords to replace").defaultValue("ex1", "ex2").build());
    public final Setting<List<String>> replacements = sgGeneral.add(new StringListSetting.Builder().name("replacements").description("Text to replace the keywords with").defaultValue("ex1 will be replaced with this text", "ex2 will be replaced with this text").build());
    public final Setting<Boolean> commands = sgGeneral.add(new BoolSetting.Builder().name("replace commands").description("Replace text in commands.").defaultValue(true).build());
    public final Setting<Boolean> chat = sgGeneral.add(new BoolSetting.Builder().name("replace chat").description("Replace text in chat messages.").defaultValue(true).build());

    @EventHandler
    private void onMessageSend(SendMessageEvent event){
        if (!chat.get() || !isActive()) return;
        String content = event.message;
        for(int i = 0; i < keywords.get().size(); i++) {
            if (!content.toLowerCase().contains(keywords.get().get(i).toLowerCase())) continue;
            if(replacements.get().size() <= i) break;
            content = content.replace(keywords.get().get(i), replacements.get().get(i));
        }
        event.message = content.length() > 256 ? content.substring(0, 256) : content;
    }
}
