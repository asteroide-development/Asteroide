package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import spigey.asteroide.AsteroideAddon;

public class VersionSpoofModule extends Module {
    public VersionSpoofModule() {
        super(AsteroideAddon.CATEGORY, "version-spoof", "Spoofs your minecraft version. In development.");
    }
    SettingGroup sgGeneral = settings.getDefaultGroup();
    public Setting<Integer> spoofedVersion = sgGeneral.add(new IntSetting.Builder()
        .name("spoofed-protocol-version")
        .description("The spoofed protocol version that will be sent to the server.")
        .defaultValue(671)
        .build()
    );

    ///////////////////////////////   CODE USES A MIXIN   ///////////////////////////////

}


