package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;

import static spigey.asteroide.util.msg;

public class AutoLoginModule extends Module {
    public AutoLoginModule() {
        super(AsteroideAddon.CATEGORY, "auto-login", "Automatically logs in on cracked servers");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<String> register = sgGeneral.add(new StringSetting.Builder()
        .name("register-trigger")
        .description("Messages have to contain this String to trigger the register command") // why does this seem so ai generated :sob:
        .defaultValue("register")
        .build()
    );
    private final Setting<String> login = sgGeneral.add(new StringSetting.Builder()
        .name("login-trigger")
        .description("Messages have to contain this String to trigger the login command") // why does this seem so ai generated :sob:
        .defaultValue("login")                                                            // copy n paste when
        .build()
    );
    private final Setting<String> blacklist = sgGeneral.add(new StringSetting.Builder()
        .name("message-blacklist")
        .description("Do not login/register if the message contains this string") // why does this seem so ai generated :sob:
        .defaultValue("login")                                                            // copy n paste when
        .build()
    );
    private final Setting<String> password = sgGeneral.add(new StringSetting.Builder()
        .name("password")
        .description("Password to use to login")  // why does this seem so ai generated :sob: YES ONCE AGAIN COPY AND PASTE
        .defaultValue("•••••••••••")                                                            // copy n paste when
        .build()
    );
    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event){
        if(!(event.packet instanceof GameMessageS2CPacket)) return;
        String content = String.valueOf(((GameMessageS2CPacket) event.packet).content());
        if(content.toLowerCase().contains(blacklist.get())) return;
        if(content.toLowerCase().contains(register.get())){
            msg("/register " + password.get() + " " + password.get());
        }
        if(content.toLowerCase().contains(login.get())){
            msg("/login " + password.get());
        }
    }
}


