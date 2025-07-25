package spigey.asteroide;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardDisplayS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;
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
import spigey.asteroide.utils.ws;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    public static boolean slotttt = false;
    public static Set<String> users = new HashSet<>();
    public static ws wss;

    @Override
    public void onInitialize() {
        String[] whitelisted = {"Spigey", "SkyFeiner", "EdwardTerris", "Arnaquer", "SteefWayer", "Yanicbubatz", "spoofedservers"};

        try{
            wss = new ws(new URI("ws://rtc.asteroide.cc/asws"));
            wss.connect();
        } catch(Exception e){ /* whoopsy daisy!! */ }

        LOG.info("\nLoaded Asteroide v0.1.8\n");
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
        /*if(Arrays.asList(whitelisted).contains(mc.getSession().getUsername()) || mc.getSession().getUsername().startsWith("Player"))*/ addModule(new PacketLoggerModule());
        addModule(new VersionSpoofModule());
        // addModule(new OPNotifierModule());
        addModule(new TrackerModule());
        addModule(new AimbotModule());
        addModule(new EncryptChatModule());
        addModule(new DistributeModule());
        addModule(new TrollModule());
        if(Arrays.asList(whitelisted).contains(mc.getSession().getUsername()) || mc.getSession().getUsername().startsWith("Player")) addModule(new DevModule());
        // addModule(new SwimModule());
        addModule(new FastStaircaseModule());
        addModule(new BlockHitboxesModule());
        addModule(new ClientDeleteModule());
        addModule(new RTCSettingsModule());
        //if(Arrays.asList(whitelisted).contains(mc.getSession().getUsername()) || mc.getSession().getUsername().startsWith("Player")) addModule(new OPNotifierModule());
        if(Arrays.asList(whitelisted).contains(mc.getSession().getUsername()) || mc.getSession().getUsername().startsWith("Player")) addModule(new SpamTwo());

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
        addCommand(new CalcCommand());
        addCommand(new WhereIsCommand());
        addCommand(new TrackerCommand());
        addCommand(new BCommand());
        addCommand(new RTCCommand());
        addCommand(new CloseCommand());


        // HUD
        addHud(Username.INFO);
        addHud(SpoofedIPHUD.INFO);
        addHud(MinehutIPHud.INFO);
    }

    @EventHandler
    public void MinehutHud(PacketEvent.Receive event){ // untested
        String content = "";
        if(!(event.packet instanceof GameMessageS2CPacket || event.packet instanceof ChatMessageS2CPacket)) return;
        if(event.packet instanceof GameMessageS2CPacket) content = ((GameMessageS2CPacket) event.packet).content().getString();
        if(event.packet instanceof ChatMessageS2CPacket) content = event.packet.toString();
        if(content.startsWith("Sending you to")){
            String coom = content.replace("Sending you to ", "");
            MinehutIP = coom.substring(0, coom.length() - 1);
        }
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
