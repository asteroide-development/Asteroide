package spigey.asteroide.modules;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.banstuff;
import static spigey.asteroide.util.msg;

public class AutoChatGame extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> experimental = sgGeneral.add(new BoolSetting.Builder()
        .name("Enable experimental features")
        .description("Enables some features that are still in development")
        .defaultValue(false)
        .build()
    );
    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Trigger messages").defaultValue("say", "type", "write").build());
    private final Setting<List<String>> reversers = sgGeneral.add(new StringListSetting.Builder().name("reversers").description("Strings that will reverse the solution").defaultValue("reverse").build());
    private final Setting<List<String>> mether = sgGeneral.add(new StringListSetting.Builder().name("mather").description("Strings that will solve an equation as solution").defaultValue("solve", "equation", "calculate").build());
    private final Setting<List<String>> variablegame = sgGeneral.add(new StringListSetting.Builder()
        .name("variable game")
        .description("Strings that will trigger the variable game")
        .defaultValue("✗", "solve for")
        .visible(() -> experimental.get())
        .build()
    );
    private final Setting<List<String>> quotes = sgGeneral.add(new StringListSetting.Builder().name("quotes").description("Quotes").defaultValue("\"", "'", "`").build());
    private final Setting<List<String>> contain = sgGeneral.add(new StringListSetting.Builder().name("must contain").description("Requires the message to contain all of these Strings").defaultValue().build());
    private final Setting<List<String>> dont = sgGeneral.add(new StringListSetting.Builder().name("blacklisted messages").description("Do not solve the chatgame if it contains one of these Strings").defaultValue("solved", "successfully").build());
    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("delay type")
        .description("Whether it waits for a random or precise amount of time")
        .defaultValue(Mode.Random)
        .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay before sending the solution in ticks")
        .defaultValue(30)
        .min(0)
        .sliderMax(200)
        .build()
    );
    private final Setting<Integer> minoffset = sgGeneral.add(new IntSetting.Builder()
        .name("delay min offset")
        .description("Minimum offset from the delay in ticks")
        .defaultValue(0)
        .min(0)
        .sliderMax(40)
        .visible(() -> mode.get() == Mode.Random)
        .build()
    );
    private final Setting<Integer> maxoffset = sgGeneral.add(new IntSetting.Builder()
        .name("delay max offset")
        .description("Maximum offset from the delay in ticks")
        .defaultValue(10)
        .min(0)
        .sliderMax(40)
        .visible(() -> mode.get() == Mode.Random)
        .build()
    );
    private final Setting<Boolean> hidetr = sgGeneral.add(new BoolSetting.Builder()
        .name("Hide Triggers")
        .description("Hides messages that trigger the chatgame module")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> shmsg = sgGeneral.add(new BoolSetting.Builder()
        .name("Shorter message instead")
        .description("Shows a shorter chatgame message instead")
        .defaultValue(false)
        .visible(() -> hidetr.get())
        .build()
    );
    private final Setting<Boolean> showsul = sgGeneral.add(new BoolSetting.Builder()
        .name("Show solution")
        .description("Shows you the chatgame solution instead of sending it automatically")
        .defaultValue(false)
        .build()
    );
    private int tick;
    private String solution = "";
    private boolean subscribed = false;
    private Random rand = new Random();
    private String OutputMode = "send";
    public AutoChatGame() {
        super(AsteroideAddon.CATEGORY, "auto-chatgame", "Automatically answers most chat games when triggered");
    }
    int meth(String equation) {
        return (int) util.meth(equation); // I was too lazy to actually change the method everywhere
    }

    @Override
    public void onActivate() {
        this.tick = -1;
    }

    @EventHandler(priority = EventPriority.HIGHEST + 20)
    private void PacketReceive(PacketEvent.Receive event) {
        try {
            if (!(event.packet instanceof GameMessageS2CPacket)) {
                return;
            }
            String content = String.valueOf(((GameMessageS2CPacket) event.packet).content().getString());
            boolean yes = false;
            boolean no = false;
            boolean reverse = false;
            boolean dometh = false;
            boolean vargame = false;
            String quote = "";
            for (int i = 0; i < messages.get().size(); i++) {
                if (content.toLowerCase().contains(messages.get().get(i).toLowerCase())) {
                    yes = true;
                }
            }
            for (int i = 0; i < reversers.get().size(); i++) {
                if (content.toLowerCase().contains(reversers.get().get(i).toLowerCase())) {
                    yes = true;
                    reverse = true;
                }
            }
            for (int i = 0; i < quotes.get().size(); i++) {
                if (content.toLowerCase().contains(quotes.get().get(i)) && content.toLowerCase().indexOf(quotes.get().get(i), content.indexOf(quotes.get().get(i)) + 1) != -1) {
                    no = true;
                    quote = quotes.get().get(i);
                }
            }
            for (int i = 0; i < mether.get().size(); i++) {
                if (content.toLowerCase().contains(mether.get().get(i))) {
                    yes = true;
                    dometh = true;
                }
            }
            for (int i = 0; i < dont.get().size(); i++) {
                if (content.toLowerCase().contains(dont.get().get(i))) {
                    yes = false;
                }
            }
            boolean[] isit = new boolean[contain.get().size()];
            for (int i = 0; i < contain.get().size(); i++) {
                isit[i] = content.toLowerCase().contains(contain.get().get(i).toLowerCase());
            }
            for (int i = 0; i < isit.length; i++) {
                if (!isit[i]) {
                    yes = false;
                }
            }
            for (int i = 0; i < variablegame.get().size(); i++) {
                if (!experimental.get()) {
                    return;
                }
                if (content.toLowerCase().contains(variablegame.get().get(i))) {
                    yes = true;
                    dometh = false;
                    reverse = false;
                    vargame = true;
                }
            }
            if (yes && no) {
                if (reverse) {
                    solution = String.valueOf(new StringBuilder(content.split(quote)[1]).reverse());
                } else if (dometh) {
                    boolean cum = false;
                    int themeth;
                    String except = null;
                    try {
                        themeth = meth(content.split(quote)[1]);
                    } catch (Exception L) {
                        except = "§c[X] " + L + "§r";
                        themeth = -1;
                        cum = true;
                    }
                    if (cum) {
                        info(except);
                        return;
                    } else {
                        solution = String.valueOf(themeth);
                    }

                } else if (vargame) {
                    solution = String.valueOf(DoVariableGame(content));
                } else {
                    solution = content.split(quote)[1];
                }
                if (hidetr.get()) {
                    event.cancel();
                    if (shmsg.get()) {
                        String mode = "write out";
                        if (reverse) {
                            mode = "reverse";
                        } else if (dometh) {
                            mode = "meth";
                        }
                        ChatUtils.sendMsg(Text.of("§7[§9Chatgame§7]§f " + mode + ": " + quote + content.split(quote)[1] + quote + "!"));
                    }
                }
                if (mode.get() == Mode.Precise) {
                    this.tick = delay.get();
                } else if (mode.get() == Mode.Random) {
                    int randomshit = this.rand.nextInt(maxoffset.get() - minoffset.get() + 1) + minoffset.get();
                    if (Math.random() > 0.5) {
                        randomshit -= randomshit * 2;
                    }
                    this.tick = delay.get() + randomshit;
                }
                if (showsul.get()) {
                    this.OutputMode = "show";
                    this.solution = "§7[§f\uD83D\uDEC8§7] §fThe solution is " + solution + ".§r";
                    this.tick = 1;
                }
                if (!subscribed) {
                    MeteorClient.EVENT_BUS.subscribe(this);
                    subscribed = true;
                }
            }
        } catch(Exception L){
            info("Something got very wrong for some reason?? you should be kicked rn");
            error(String.valueOf(L));
        }
    }
    int DoVariableGame(String output){ // No, this is actually not AI generated.
        String[] equations = output.split("\n");

        int Z = Integer.parseInt(equations[2].split(" = ")[1]);
        int B = Integer.parseInt(equations[3].split(" = ")[1]);
        int N = Integer.parseInt(equations[4].split(" = ")[1]);

        return N - ((Z / 3) + (B / 3));
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        banstuff();
        if(!isActive()){return;} // this got me banned off my favorite server
        if(this.tick > 0){this.tick--; return;} // don't execute when it's not done waiting
        if(this.tick == -1){return;} // disable when on -1
        if (this.OutputMode == "send") {msg(this.solution);} else{ChatUtils.sendMsg(Text.of(this.solution));}
        this.tick = -1;
    }

    public enum Mode{
        Random,
        Precise
    }
}
