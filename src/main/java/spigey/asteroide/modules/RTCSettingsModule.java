package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import spigey.asteroide.utils.ws;
import spigey.asteroide.AsteroideAddon;
import meteordevelopment.meteorclient.settings.*;

import java.util.Set;

public class RTCSettingsModule extends Module {
    public RTCSettingsModule() {
        super(AsteroideAddon.CATEGORY, "RTC-Settings", "Settings for the .rtc command. Enable to apply settings.");
    }


    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<colors> color = sgGeneral.add(new EnumSetting.Builder<colors>()
        .name("chat-color")
        .description("Chat Color.")
        .defaultValue(colors.aqua)
        .build()
    );

    public final Setting<format> formath = sgGeneral.add(new EnumSetting.Builder<format>()
        .name("chat-format")
        .description("Chat Format.")
        .defaultValue(format.none)
        .build()
    );

    public final Setting<Boolean> hideMessages = sgGeneral.add(new BoolSetting.Builder()
        .name("Hide RTC Messages")
        .description("Hides all messages received from the RTC.")
        .defaultValue(false)
        .onChanged((value) -> AsteroideAddon.showRtc = !(value && isActive()))
        .build()
    );

    public final Setting<Boolean> disableIcon = sgGeneral.add(new BoolSetting.Builder()
        .name("Highlight Asteroide Users in the tab list")
        .description("Highlights Asteroide users in the player list with a custom icon.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> censor = sgGeneral.add(new BoolSetting.Builder()
        .name("Censor Slurs")
        .description("Censors slurs in the RTC")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> broadcastOnline = sgGeneral.add(new BoolSetting.Builder()
        .name("Get notified when someone comes online")
        .description("Self explanatory")
        .onChanged((value) -> { ws.call("online", isActive() && value); } )
        .defaultValue(false)
        .build()
    );

    @Override public void onActivate() { ws.call("online", isActive() && broadcastOnline.get()); }
    @Override public void onDeactivate() { ws.call("online", false); }

    public enum colors {
        dark_red,
        red,
        gold,
        yellow,
        dark_green,
        green,
        aqua,
        dark_aqua,
        dark_blue,
        blue,
        light_purple,
        dark_purple,
        white,
        gray,
        dark_gray,
        black
    }

    public enum format {
        none,
        bold,
        italic,
        underline,
        strike,
        obfuscated
    }
}


