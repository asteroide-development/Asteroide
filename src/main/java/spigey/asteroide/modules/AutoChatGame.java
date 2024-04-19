package spigey.asteroide.modules;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.msg;

public class AutoChatGame extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Trigger messages").defaultValue("say", "type", "write").build());
    private final Setting<List<String>> reversers = sgGeneral.add(new StringListSetting.Builder().name("reversers").description("Strings that will reverse the solution").defaultValue("reverse").build());
    private final Setting<List<String>> quotes = sgGeneral.add(new StringListSetting.Builder().name("quotes").description("Quotes").defaultValue("\"", "'", "`").build());
    private final Setting<List<String>> dont = sgGeneral.add(new StringListSetting.Builder().name("blacklisted messages").description("Do not solve the chatgame if it contains one of these Strings").defaultValue("solved", "successfully").build());
    private final Setting<List<String>> mether = sgGeneral.add(new StringListSetting.Builder().name("mather").description("Strings that will solve an equation as solution").defaultValue("solve", "equation", "calculate").build());
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay before sending the solution in ticks")
        .defaultValue(30)
        .min(0)
        .sliderMax(200)
        .build()
    );
    private final Setting<Boolean> contain = sgGeneral.add(new BoolSetting.Builder()
        .name("Must contain 'Chat'")
        .description("Requires the message to contain 'Chat'")
        .defaultValue(true)
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
    public AutoChatGame() {
        super(AsteroideAddon.CATEGORY, "auto-chatgame", "Automatically answers most chat games when triggered");
    }
    int meth(String equation) throws ScriptException {
        return (int) util.eval(equation); // I was too lazy to actually change the method everywhere
    }
    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void PacketReceive(PacketEvent.Receive event) throws ScriptException {
        if (!(event.packet instanceof GameMessageS2CPacket)) {return;}
        String content = String.valueOf(((GameMessageS2CPacket) event.packet).content().getString());
        boolean yes = false;
        boolean no = false;
        boolean reverse = false;
        boolean dometh = false;
        String quote = "";
        for(int i = 0; i < messages.get().size(); i++){
            if(content.toLowerCase().contains(messages.get().get(i).toLowerCase())){yes = true;}
        }
        for(int i = 0; i < reversers.get().size(); i++){
            if(content.toLowerCase().contains(reversers.get().get(i).toLowerCase())){yes = true; reverse = true;}
        }
        for(int i = 0; i < quotes.get().size(); i++){
            if(content.toLowerCase().contains(quotes.get().get(i)) && content.toLowerCase().indexOf(quotes.get().get(i), content.indexOf(quotes.get().get(i)) + 1) != -1){no = true; quote = quotes.get().get(i);}
        }
        for(int i = 0; i < mether.get().size(); i++){
            if(content.toLowerCase().contains(mether.get().get(i))){yes = true; dometh = true;}
        }
        for(int i = 0; i < dont.get().size(); i++){
            if(content.toLowerCase().contains(dont.get().get(i))){yes = false;}
        }
        if(contain.get() && !content.toLowerCase().contains("chat")){yes = false;}
        if(yes && no){
            if(reverse){
                solution = String.valueOf(new StringBuilder(content.split(quote)[1]).reverse());
            } else if(dometh) {
                boolean cum = false;
                int themeth = -1;
                String except = null;
                try{
                    themeth = meth(content.split(quote)[1]);
                } catch(Exception L){
                    except = "§c[X] " + L + "§r";
                    themeth = -1;
                    cum = true;
                }
                if(cum){
                    info(except);
                    yes = false;
                } else{
                    solution = String.valueOf(themeth);
                }

            } else {
                solution = content.split(quote)[1];
            }
            if(showsul.get()){info("[\uD83D\uDEC8] §fThe solution is " + solution + ".§r");} else{
                this.tick = delay.get();
                if(!subscribed){
                    MeteorClient.EVENT_BUS.subscribe(this);
                    subscribed = true;
                }
            };
        }

    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(!isActive()){return;} // this got me banned off my favorite server
        if(this.tick > 0){this.tick--; return;} // don't execute when it's not done waiting
        if(this.tick == -1){return;} // disable when on -1
        msg(this.solution);
        this.tick = -1;
    }
}
