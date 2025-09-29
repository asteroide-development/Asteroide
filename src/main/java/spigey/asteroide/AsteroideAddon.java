package spigey.asteroide;

import com.google.gson.Gson;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import spigey.asteroide.commands.*;
import spigey.asteroide.hud.*;
import spigey.asteroide.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;
import spigey.asteroide.utils.ws;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Supplier;

import static meteordevelopment.meteorclient.MeteorClient.mc;

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

    public static boolean showRtc = false;

    @Override
    public void onInitialize() {                                                                                                                                                                                                                                                                                                                                if(mc.getGameProfile().getName().toLowerCase().equals("rama8")) mc.close(); // piss me off
        // LOG.info(mc.getSession().getAccessToken()); // Turns out my cracked players access token is "FabricMC"??

        MeteorClient.EVENT_BUS.subscribe(this); // dear fuck chatskibidi...
        String[] whitelisted = {"Spigey", "SkyFeiner", "EdwardTerris", "Arnaquer", "SteefWayer", "Yanicbubatz", "spoofedservers"};

        try{
            wss = new ws(new URI("ws://rtc.asteroide.cc/asws?version=0.2.1"));
            wss.connect();
        } catch(Exception e){ /* whoopsy daisy!! */ }

        LOG.info("\nLoaded Asteroide v0.2.1\n");
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

        Modules modules = Modules.get();

        // Modules
        modules.add(new AutoKys());
        modules.add(new ServerCrashModule());
        modules.add(new AutoChatGame());
        modules.add(new AntiAnnouncement());
        modules.add(new AutoBack());
        modules.add(new ChatBot());
        modules.add(new AutoMacro());
        modules.add(new ExperimentalModules());
        modules.add(new AutoSlotSwitchModule());
        // addModule(new WordFilterModule());

        // addModule(new AutoEz());
        modules.add(new MultiCommandCommandBlockModule());
        modules.add(new PlatformFlyModule());
        modules.add(new BetterBungeeSpoofModule());
        modules.add(new BetterCollisionsModule());
        modules.add(new CreativeFlightModule());
        modules.add(new AutoFuckModule());
        modules.add(new ChestStealerModule());
        modules.add(new MinehutAutoJoinRandomModule());
        modules.add(new AutoLoginModule());
        modules.add(new InvCleanerModule());
        modules.add(new BorderNoclipModule());
        modules.add(new WordFilterModule());
        /*if(Arrays.asList(whitelisted).contains(mc.getSession().getUsername()) || mc.getSession().getUsername().startsWith("Player"))*/ modules.add(new PacketLoggerModule());
        modules.add(new VersionSpoofModule());
        // addModule(new OPNotifierModule());
        modules.add(new TrackerModule());
        modules.add(new AimbotModule());
        modules.add(new EncryptChatModule());
        modules.add(new DistributeModule());
        modules.add(new TrollModule());
        if(Arrays.asList(whitelisted).contains(mc.getSession().getUsername()) || mc.getSession().getUsername().startsWith("Player")) modules.add(new DevModule());
        // addModule(new SwimModule());
        modules.add(new FastStaircaseModule());
        modules.add(new BlockHitboxesModule());
        modules.add(new ClientDeleteModule());
        modules.add(new RTCSettingsModule());
        modules.add(new BetterNoInteractModule());
        //if(Arrays.asList(whitelisted).contains(mc.getSession().getUsername()) || mc.getSession().getUsername().startsWith("Player")) addModule(new OPNotifierModule());
        if(Arrays.asList(whitelisted).contains(mc.getSession().getUsername()) || mc.getSession().getUsername().startsWith("Player")) modules.add(new SpamTwo());
        modules.add(new BetterAntiCrashModule());
        modules.add(new AutoCrashModule());
        modules.add(new TypoModule());
        modules.add(new ChatBypassModule());
        modules.add(new NoOcclusionModule());
        modules.add(new PacketSpammerModule());
        modules.add(new AutoCommandModule());
        modules.add(new PacketPauseModule());
        modules.add(new ClickEventsModule());
        modules.add(new MurderMysteryESP());
        modules.add(new SilentSwapModule());

        // Commands
        Commands.add(new CrashAll());
        Commands.add(new CrashPlayer());
        Commands.add(new ServerCrash());
        Commands.add(new GetNbtItem());
        Commands.add(new PermLevel());
        Commands.add(new FuckServerCommand());
        Commands.add(new MeCommand());
        Commands.add(new CommandBlockCommand());
        Commands.add(new PhaseCommand());
        Commands.add(new BypassCommand());
        Commands.add(new MathCommand());
        Commands.add(new CalcCommand());
        Commands.add(new WhereIsCommand());
        Commands.add(new TrackerCommand());
        Commands.add(new BCommand());
        Commands.add(new RTCCommand());
        Commands.add(new CloseCommand());
        Commands.add(new UUIDCommand());
        Commands.add(new DupeCommand());
        Commands.add(new DelayCommand());



        // HUD
        Hud hud = Hud.get();
        hud.register(Username.INFO);
        hud.register(SpoofedIPHUD.INFO);
        hud.register(MinehutIPHud.INFO);
        hud.register(ImageHUD.INFO);

        showRtc = !(Modules.get().get(RTCSettingsModule.class).isActive() && Modules.get().get(RTCSettingsModule.class).hideMessages.get());
        ChatUtils.registerCustomPrefix("spigey.asteroide.modules", this::getPrefix);
    }

    private Text getPrefix(){ // https://github.com/MeteorClientPlus/MeteorPlus/blob/1.21.8/src/main/java/nekiplay/meteorplus/features/modules/misc/ChatPrefix.java
        MutableText value = Text.literal("Asteroide");
        MutableText prefix = Text.literal("");
        value.setStyle(value.getStyle().withColor(TextColor.fromFormatting(Formatting.RED)));
        prefix.setStyle(prefix.getStyle().withFormatting(Formatting.DARK_GRAY))
            .append(Text.literal("["))
            .append(value)
            .append(Text.literal("] "));
        return prefix;
    } // Text.of("§7[§cAsteroide§7] ")

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event){
        String content = event.getMessage().getString();
        if(!content.startsWith("Sending you to") || !content.endsWith("!")) return;
        try {
            String coom = content.replace("Sending you to ", "");
            MinehutIP = coom.substring(0, coom.length() - 1);
        }catch(Exception L) { /* scawy */ }
    }

    @Override public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }
    @Override public String getPackage() {
        return "spigey.asteroide";
    }
    @Override public GithubRepo getRepo() { return new GithubRepo("asteroide-development", "Asteroide"); }
    @Override public String getWebsite() { return "https://www.asteroide.cc/"; }
    @Override public String getCommit() { return "d99742cf8cfed610ddb1e5ff5cd9041dca39d973"; } // Crashes when I try to do it dynamically, requires any commit for the website & GitHub to be clickable
}
