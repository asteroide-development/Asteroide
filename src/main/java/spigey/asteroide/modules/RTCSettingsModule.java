package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import spigey.asteroide.utils.ws;
import spigey.asteroide.AsteroideAddon;
import meteordevelopment.meteorclient.settings.*;

import java.util.List;

public class RTCSettingsModule extends Module {
    public RTCSettingsModule() {
        super(AsteroideAddon.CATEGORY, "RTC-Settings", "Settings for the .rtc command. Enable to apply settings.");
    }

    private final SettingGroup sgColors = settings.createGroup("Colors", true);
    private final SettingGroup sgSettings = settings.createGroup("Settings", true);

    public final Setting<ColorType> colorType = sgColors.add(new EnumSetting.Builder<ColorType>()
        .name("color-type")
        .description("Whether to use a custom gradient, color or a predefined color.")
        .defaultValue(ColorType.Predefined)
        .build()
    );

    public final Setting<colors> color = sgColors.add(new EnumSetting.Builder<colors>()
        .name("chat-color")
        .description("Chat Color.")
        .defaultValue(colors.aqua)
        .visible(() -> colorType.get() == ColorType.Predefined)
        .build()
    );

    public final Setting<SettingColor> customColor = sgColors.add(new ColorSetting.Builder()
        .name("custom-color")
        .description("Custom Chat Color.")
        .defaultValue(SettingColor.WHITE)
        .visible(() -> colorType.get() == ColorType.Custom)
        .build()
    );

    public final Setting<List<SettingColor>> gradientColors = sgColors.add(new ColorListSetting.Builder()
        .name("gradient-colors")
        .description("Which colors to use for the custom gradient.")
        .defaultValue(List.of(
            new SettingColor(135, 15, 255),
            new SettingColor(252, 40, 189)
        ))
        .visible(() -> colorType.get() == ColorType.Gradient)
        .build()
    );

    public final Setting<format> formath = sgColors.add(new EnumSetting.Builder<format>()
        .name("chat-format")
        .description("Chat Format.")
        .defaultValue(format.none)
        .build()
    );

    public final Setting<Boolean> connect = sgSettings.add(new BoolSetting.Builder()
        .name("Connect to RTC (Broken)")
        .description("Self-explanatory.")
        .defaultValue(true)
        .onChanged((value) -> {
            if(value){ if(!AsteroideAddon.attemptConnect("wss://rtc.asteroide.fun/")) AsteroideAddon.attemptConnect("ws://rtc.asteroide.cc/"); }
            else AsteroideAddon.wss.close();
        })
        .build()
    );

    public final Setting<Boolean> hideMessages = sgSettings.add(new BoolSetting.Builder()
        .name("Hide RTC Messages")
        .description("Hides all messages received from the RTC.")
        .defaultValue(false)
        .onChanged((value) -> AsteroideAddon.showRtc = !(value && isActive()))
        .build()
    );

    public final Setting<Boolean> disableIcon = sgSettings.add(new BoolSetting.Builder()
        .name("Highlight Asteroide Users in the tab list")
        .description("Highlights Asteroide users in the player list with a custom icon.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> censor = sgSettings.add(new BoolSetting.Builder()
        .name("Censor Slurs")
        .description("Censors slurs in the RTC")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> broadcastOnline = sgSettings.add(new BoolSetting.Builder()
        .name("Get notified when someone comes online")
        .description("Self explanatory")
        .onChanged((value) -> ws.call("online", isActive() && value))
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> starscript = sgSettings.add(new BoolSetting.Builder()
        .name("Enable Starscript Support")
        .description("Self explanatory")
        .defaultValue(false)
        .build()
    );

    public final Setting<Integer> reconnectDelay = sgSettings.add(new IntSetting.Builder()
        .name("Reconnect Delay (ms)")
        .description("Delay before reconnecting to the RTC in milliseconds")
        .defaultValue(3000)
        .min(0)
        .sliderRange(1000, 10_000)
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
        black,
        rainbow,
        ice,
        sunset,
        galaxy
    }

    public enum format {
        none,
        bold,
        italic,
        underline,
        strike,
        obfuscated
    }

    public enum ColorType {
        Predefined,
        Custom,
        Gradient
    }
}
