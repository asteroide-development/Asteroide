package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import spigey.asteroide.AsteroideAddon;

public class BungeeSpoofModule extends Module {
    public BungeeSpoofModule() {
        super(AsteroideAddon.CATEGORY, "BungeeSpoof", "Lets you join BungeeCord servers with an exposed backend");
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
}


