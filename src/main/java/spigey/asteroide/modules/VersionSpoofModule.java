package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import spigey.asteroide.AsteroideAddon;

public class VersionSpoofModule extends Module {
    public VersionSpoofModule() {
        super(AsteroideAddon.CATEGORY, "Version-Spoof", "UNMAINTAINED | Spoofs your minecraft version.");
    }
    SettingGroup sgGeneral = settings.getDefaultGroup();
    public Setting<String> spoofedVersion = sgGeneral.add(new StringSetting.Builder()
        .name("spoofed-version")
        .description("The spoofed that will be sent to the server.")
        .defaultValue("1.21")
        .build()
    );

    ///////////////////////////////   CODE USES A MIXIN   ///////////////////////////////

    public static int readable(String asd){
        return switch(asd) {
            case "1.21" -> 767;
            case "1.20.6", "1.20.5" -> 766;
            case "1.20.2" -> 758;
            case "1.20.1" -> 757;
            case "1.20" -> 756;
            case "1.19.4" -> 755;
            case "1.19.3" -> 754;
            case "1.19.2" -> 753;
            case "1.19" -> 752;
            case "1.18.2" -> 751;
            case "1.18" -> 498;
            case "1.17.1" -> 490;
            case "1.17" -> 393;
            case "1.16.5" -> 340;
            case "1.16.4" -> 338;
            case "1.16.3" -> 335;
            case "1.16.2" -> 332;
            case "1.16.1" -> 316;
            case "1.16" -> 210;
            case "1.15.2" -> 107;
            case "1.15.1" -> 108;
            case "1.15" -> 578;
            case "1.14.4" -> 155;
            case "1.14.3" -> 110;
            case "1.14.2" -> 94;
            case "1.14.1" -> 93;
            case "1.14" -> 90;
            case "1.13.2" -> 401;
            case "1.13.1" -> 404;
            case "1.13" -> 393;
            case "1.12.2" -> 340;
            case "1.12.1" -> 338;
            case "1.12" -> 335;
            case "1.11" -> 316;
            case "1.10" -> 210;
            case "1.9.4" -> 110;
            case "1.9.2" -> 109;
            case "1.9.1" -> 108;
            case "1.9" -> 107;
            case "1.8.9", "1.8.8", "1.8.5", "1.8.4", "1.8.3", "1.8.2", "1.8.1", "1.8" -> 47;
            case "1.7.10", "1.7.9", "1.7.8", "1.7.7", "1.7.6" -> 5;
            case "1.7.5", "1.7.4", "1.7.3", "1.7.2" -> 4;
            case "1.7.1", "1.7" -> 3;
            case "1.6.4" -> 78;
            case "1.6.3" -> 77;
            case "1.6.2" -> 74;
            case "1.6.1" -> 73;
            case "1.6" -> 72;
            case "1.5.2" -> 71;
            case "1.5.1" -> 61;
            case "1.5" -> 60;
            default -> Integer.parseInt(asd);
        };
    }
}
