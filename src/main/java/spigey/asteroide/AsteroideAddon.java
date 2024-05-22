package spigey.asteroide;

import com.google.gson.Gson;
import net.minecraft.item.Items;
import spigey.asteroide.commands.*;
import spigey.asteroide.hud.*;
import spigey.asteroide.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static spigey.asteroide.util.*;

public class AsteroideAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Asteroide", Items.MAGMA_BLOCK.getDefaultStack());
    public static final HudGroup HUD = new HudGroup("Asteroide");
    public static final Gson gson = new Gson();
    public static List<String> banlist = new ArrayList<>();
    public static String spoofedIP = "?";
    public static String MinehutIP = "?";
    @Override
    public void onInitialize() {
        LOG.info("\nLoaded Asteroide v0.1.4-fix\n");
        // Modules
        addModule(new AutoKys());
        addModule(new ServerCrashModule());
        addModule(new AutoChatGame());
        addModule(new AntiAnnouncement());
        addModule(new AutoBack());
        addModule(new ChatBot());
        addModule(new AutoMacro());
        addModule(new ExperimentalModules());
        addModule(new BanStuffs());
        addModule(new AutoSlotSwitchModule());
        // addModule(new WordFilterModule());
        // addModule(new AutoEz());
        addModule(new MultiCommandCommandBlockModule());
        addModule(new PlatformFlyModule());
        addModule(new BetterBungeeSpoofModule());
        addModule(new BetterCollisionsModule());
        addModule(new CreativeFlightModule());
        addModule(new AutoFuckModule());
        addModule(new ChestStealerModule());
        addModule(new MinehutAutoJoinRandomModule());
        addModule(new AutoLoginModule());
        addModule(new InvCleanerModule());
        addModule(new BorderNoclipModule());
        addModule(new WordFilterModule());
        addModule(new PacketLoggerModule());
        addModule(new VersionSpoofModule());
        addModule(new SwimModule());

        // Commands
        addCommand(new CrashAll());
        addCommand(new CrashPlayer());
        addCommand(new ServerCrash());
        addCommand(new GetNbtItem());
        addCommand(new PermLevel());
        addCommand(new FuckServerCommand());
        addCommand(new MeCommand());
        addCommand(new CommandBlockCommand());
        addCommand(new PhaseCommand());
        addCommand(new BypassCommand());
        addCommand(new MathCommand());
        addCommand(new DevCommand());
        addCommand(new CalcCommand());

        // HUD
        addHud(Username.INFO);
        addHud(SpoofedIPHUD.INFO);
        addHud(MinehutIPHud.INFO);
        util.banstuff();
    }


    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "spigey.asteroide";
    }
}
