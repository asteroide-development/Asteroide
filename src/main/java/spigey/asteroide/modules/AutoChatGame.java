package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;
import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.msg;

public class AutoChatGame extends Module {
    public AutoChatGame() { super(AsteroideAddon.CATEGORY, "Auto-Chatgame", "Automatically answers most chat games when triggered"); }

    private final SettingGroup sgTriggers = settings.createGroup("Triggers", true);
    private final SettingGroup sgFilters = settings.createGroup("Filters", true);
    private final SettingGroup sgDelay = settings.createGroup("Delay", true);
    private final SettingGroup sgChat = settings.createGroup("Chat", true);

    private final Setting<List<String>> messages = sgTriggers.add(new StringListSetting.Builder().name("say").description("Strings that will say the solution").defaultValue("say", "type", "write").build());
    private final Setting<List<String>> reversers = sgTriggers.add(new StringListSetting.Builder().name("reverse").description("Strings that will reverse the solution").defaultValue("reverse").build());
    private final Setting<List<String>> mether = sgTriggers.add(new StringListSetting.Builder().name("math").description("Strings that will solve an equation as solution").defaultValue("solve", "equation", "calculate").build());
    private final Setting<List<String>> quotes = sgFilters.add(new StringListSetting.Builder().name("quotes").description("Quotes").defaultValue("\"", "'", "`").build());
    private final Setting<List<String>> contain = sgFilters.add(new StringListSetting.Builder().name("must contain all").description("Requires the message to contain all of these Strings").defaultValue().build());
    private final Setting<List<String>> dont = sgFilters.add(new StringListSetting.Builder().name("must not contain").description("Do not solve the chatgame if it contains one of these Strings").defaultValue("solved", "successfully").build());
    private final Setting<Mode> mode = sgDelay.add(new EnumSetting.Builder<Mode>().name("delay type").description("Whether it waits for a random or precise amount of time").defaultValue(Mode.Random).build());
    private final Setting<Integer> delay = sgDelay.add(new IntSetting.Builder().name("delay").description("The delay before sending the solution in ticks").defaultValue(30).min(0).sliderMax(200).build());
    private final Setting<Integer> minoffset = sgDelay.add(new IntSetting.Builder().name("delay min offset").description("Minimum offset from the delay in ticks").defaultValue(0).min(0).sliderMax(40).visible(() -> mode.get() == Mode.Random).build());
    private final Setting<Integer> maxoffset = sgDelay.add(new IntSetting.Builder().name("delay max offset").description("Maximum offset from the delay in ticks").defaultValue(10).min(0).sliderMax(40).visible(() -> mode.get() == Mode.Random).build());
    private final Setting<Boolean> hidetr = sgChat.add(new BoolSetting.Builder().name("Hide Triggers").description("Hides messages that trigger the chatgame module").defaultValue(false).build());
    private final Setting<Boolean> shmsg = sgChat.add(new BoolSetting.Builder().name("Shorter message instead").description("Shows a shorter chatgame message instead").defaultValue(false).visible(hidetr::get).build());
    private final Setting<Boolean> showsul = sgChat.add(new BoolSetting.Builder().name("Show solution").description("Shows you the chatgame solution instead of sending it automatically").defaultValue(false).build());

    @Override
    public void onActivate() { this.tick = -1; }

    private int tick;
    private String solution = "";
    private final Random rand = new Random();

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event){
        if(!isActive()) return;

        GameMode gameMode = GameMode.NONE;
        String content = event.getMessage().getString();
        String Quote = null;
        String game = null;

        // Triggers
        for(String msg : messages.get()) if (content.toLowerCase().contains(msg.toLowerCase())) { gameMode = GameMode.STRING; break; }
        for(String msg : reversers.get()) if (content.toLowerCase().contains(msg.toLowerCase())) { gameMode = GameMode.REVERSE; break; }
        for(String msg : mether.get()) if (content.toLowerCase().contains(msg.toLowerCase())) { gameMode = GameMode.SOLVE; break; }
        for(String msg : contain.get()) if (!content.toLowerCase().contains(msg.toLowerCase())) return;
        for(String msg : dont.get()) if (content.toLowerCase().contains(msg.toLowerCase())) return;
        for(String quote : quotes.get()) if (content.contains(quote)) Quote = quote;
        if(gameMode == GameMode.NONE || Quote == null) return;

        game = content.split(Quote)[1];

        if(hidetr.get()){
            if(shmsg.get()) event.setMessage(Text.of(String.format("§7[§9ChatGame§7]§f %s `%s`!", gameMode, game)));
            else event.cancel();
        }

        // Solving
        try{
            this.solution = switch(gameMode){
                case SOLVE -> String.valueOf((int) util.meth(game));
                case REVERSE -> new StringBuilder(game).reverse().toString();
                case STRING -> game;
                default -> "";
            };
        } catch (Exception e){
            info("§8§l[§4§lX§8§l] §c" + e.getMessage());
            return;
        }

        if(mode.get() == Mode.Precise) this.tick = delay.get();
        else {
            int rdm = this.rand.nextInt(maxoffset.get() - minoffset.get() + 1) + minoffset.get();
            if (Math.random() > 0.5) rdm = -rdm;
            this.tick = delay.get() + rdm;
        }
        this.tick--; // It's wrong by exactly 1 tick??
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(!isActive() || this.tick == -1) return;
        if(this.tick > 0){ this.tick--; return; }
        if(showsul.get()) ChatUtils.sendMsg(Text.of("§8[§9\uD83D\uDEC8§8] §7The solution is §e" + this.solution + "§7."));
        else msg(this.solution);
        this.tick = -1;
    }

    public enum Mode{
        Random,
        Precise
    }

    private enum GameMode{
        NONE,
        REVERSE,
        SOLVE,
        STRING
    }
}
