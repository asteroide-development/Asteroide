package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import spigey.asteroide.AsteroideAddon;

public class BetterBungeeSpoofModule extends Module {
    public BetterBungeeSpoofModule() {
        super(AsteroideAddon.CATEGORY, "better-bungeespoof", "Lets you join BungeeCord servers with an exposed backend");
    }
    SettingGroup sgGeneral = settings.getDefaultGroup();
    public Setting<Boolean> randomize = sgGeneral.add(new BoolSetting.Builder()
        .name("Randomize IP")
        .description("Randomizes the IP upon joining")
        .defaultValue(true)
        .build()
    );
    public Setting<String> spoofedAddress = sgGeneral.add(new StringSetting.Builder()
        .name("spoofed-address")
        .description("The spoofed IP address that will be sent to the server.")
        .defaultValue("127.0.0.1")
        .filter((text, c) -> (text + c).matches("^[0-9a-f\\\\.:]{0,45}$"))
        .visible(() -> !randomize.get())
        .build()
    );
    public final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("ip range")
        .description("range of 255 = 0.0.0.0 to 255.255.255.255")
        .defaultValue(240)
        .min(0)
        .sliderMax(255)
        .visible(randomize::get)
        .build()
    );
}


