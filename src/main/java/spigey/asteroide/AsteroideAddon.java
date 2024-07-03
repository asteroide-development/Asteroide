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
import spigey.asteroide.utils.Regex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.util.*;

public class AsteroideAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Asteroide", Items.MAGMA_BLOCK.getDefaultStack());
    public static final HudGroup HUD = new HudGroup("Asteroide");
    public static final Gson gson = new Gson();
    public static List<String> banlist = new ArrayList<>();
    public static String spoofedIP = "?";
    public static String MinehutIP = "?";
    public static String trackedPlayer = null;
    public static double[] lastPos = {0, 0, 0};
    public static List<String> trolls = new ArrayList<>();
    public static List<String> notInsults = new ArrayList<>();
    @Override
    public void onInitialize() {


        String[] whitelisted = {"Spigey", "SkyFeiner", "RaisinCrayzin", "Arnaquer", "SteefWayer", "fortnitegamersmh"};


        LOG.info("\nLoaded Asteroide v0.1.6\n");
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("trolls.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                trolls.add(line);
            }
        } catch (Exception e) {/**/}

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("notinsults.txt");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                notInsults.add(line);
            }
        } catch (Exception e) {/**/}

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
        if(Arrays.asList(whitelisted).contains(mc.getSession().getUsername()) || mc.getSession().getUsername().startsWith("Player")) addModule(new PacketLoggerModule());
        addModule(new VersionSpoofModule());
        // addModule(new OPNotifierModule());
        addModule(new TrackerModule());
        addModule(new AimbotModule());
        addModule(new EncryptChatModule());
        addModule(new DistributeModule());
        addModule(new TrollModule());
        // addModule(new SwimModule());

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
        if(Arrays.asList(whitelisted).contains(mc.getSession().getUsername()) || mc.getSession().getUsername().startsWith("Player")) addCommand(new DevCommand());
        addCommand(new CalcCommand());
        addCommand(new WhereIsCommand());
        addCommand(new TrackerCommand());

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
