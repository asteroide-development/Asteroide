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

    @EventHandler
    private void onMessageSend(SendMessageEvent event) {
        MeteorClient.EVENT_BUS.subscribe(this);
        event.message = "aaaaa";
        info("working");
        System.out.println("working");
        // events don't work for me :(
    }
}
