package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

import static spigey.asteroide.util.msg;

public class AutoLoginModule extends Module {
    public AutoLoginModule() {
        super(AsteroideAddon.CATEGORY, "Auto-Login", "Automatically logs in on cracked servers");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<List<String>> register = sgGeneral.add(new StringListSetting.Builder()
        .name("register-triggers")
        .description("Messages have to contain one of these Strings to trigger the register command")
        .defaultValue("register")
        .build()
    );
    private final Setting<List<String>> login = sgGeneral.add(new StringListSetting.Builder()
        .name("login-triggers")
        .description("Messages have to contain one of these Strings to trigger the login command")
        .defaultValue("login")
        .build()
    );
    private final Setting<List<String>> blacklist = sgGeneral.add(new StringListSetting.Builder()
        .name("message-blacklist")
        .description("Do not login/register if the message contains this string")
        .defaultValue("not found")
        .build()
    );
    private final Setting<String> password = sgGeneral.add(new StringSetting.Builder()
        .name("password")
        .description("Password to use to login")
        .defaultValue("•••••••••••")
        .build()
    );
    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event){
        String content = event.getMessage().getString();
        if(!blacklist.get().isEmpty()) for(int i = 0; i < blacklist.get().size(); i++) if(content.toLowerCase().contains(blacklist.get().get(i).toLowerCase())) return;
        for(String str : register.get()){
            if(content.toLowerCase().contains(str)) {
                msg(String.format("/register %s %s", password.get(), password.get()));
                break;
            }
        }

        for(String str : login.get()){
            if(content.toLowerCase().contains(str)) {
                msg(String.format("/login %s", password.get()));
                break;
            }
        }
    }
}


