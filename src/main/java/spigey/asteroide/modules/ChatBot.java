package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.msg;
import spigey.asteroide.util.*;

public class ChatBot extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Trigger messages").defaultValue("say", "type", "write").build());
    private final Setting<List<String>> reversers = sgGeneral.add(new StringListSetting.Builder().name("reversers").description("Strings that will reverse the answer").defaultValue("reverse").build());
    private final Setting<List<String>> quotes = sgGeneral.add(new StringListSetting.Builder().name("quotes").description("Quotes").defaultValue("\"", "'", "`").build());
    private final Setting<List<String>> dont = sgGeneral.add(new StringListSetting.Builder().name("blacklisted messages").description("Do not reply if it contains one of these Strings").defaultValue("pay", "sethome", "claim", "withdraw", "disconnect", "suicide").build());
    private final Setting<List<String>> mether = sgGeneral.add(new StringListSetting.Builder().name("mather").description("Strings that will solve an equation as solution").defaultValue("solve", "equation", "calculate").build());
    private final Setting<List<String>> contain = sgGeneral.add(new StringListSetting.Builder().name("must contain").description("Requires the message to contain all of these Strings").defaultValue().build());
    private final Setting<Boolean> hidetr = sgGeneral.add(new BoolSetting.Builder()
        .name("Hide triggers")
        .description("Prevents messages that trigger the bot from being shown")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> soutput = sgGeneral.add(new BoolSetting.Builder()
        .name("Show Chat")
        .description("Automatically sends the chat output in the chat")
        .defaultValue(false)
        .build()
    );
    private final Setting<List<String>> dontoutput = sgGeneral.add(new StringListSetting.Builder()
        .name("blacklisted output")
        .description("Do not send the chat when it contains one of these strings")
        .defaultValue(mc.getSession().getUsername())
        .visible(() -> soutput.get())
        .build()
    );
    private final Setting<List<String>> mustcontain = sgGeneral.add(new StringListSetting.Builder()
        .name("output must contain all")
        .description("output has to contain these to send in chat")
        .defaultValue()
        .visible(() -> soutput.get())
        .build()
    );
    private final Setting<List<String>> omessage = sgGeneral.add(new StringListSetting.Builder()
        .name("output must contain one")
        .description("output has to contain one of these to send in chat")
        .defaultValue()
        .visible(() -> soutput.get())
        .build()
    );
    private int tick;
    private String solution = "";
    private boolean subscribed = false;
    private Random rand = new Random();
    public ChatBot() {
        super(AsteroideAddon.CATEGORY, "chat-bot", "Automatically answers to other people using commands");
    }
    int meth(String equation) {
        return (int) util.meth(equation); // I was too lazy to actually change the method everywhere
    }
    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void PacketReceive(PacketEvent.Receive event) {
        util.banstuff();
        if (!((event.packet instanceof GameMessageS2CPacket) || (event.packet instanceof ChatMessageS2CPacket) || (event.packet instanceof ProfilelessChatMessageS2CPacket))) {return;}
        String content = null;
        if(event.packet instanceof GameMessageS2CPacket) content = String.valueOf(((GameMessageS2CPacket) event.packet).content().getString());
        if(event.packet instanceof ChatMessageS2CPacket) content = ((ChatMessageS2CPacket) event.packet).body().content();
        if(event.packet instanceof ProfilelessChatMessageS2CPacket) content = util.ParsePacket(String.valueOf(((ProfilelessChatMessageS2CPacket) event.packet).message()));
        boolean yes = false;
        boolean no = false;
        boolean reverse = false;
        boolean dometh = false;
        String quote = null;
        for(int i = 0; i < messages.get().size(); i++){
            if(content.toLowerCase().contains(messages.get().get(i).toLowerCase())){yes = true;}
        }
        for(int i = 0; i < reversers.get().size(); i++){
            if(content.toLowerCase().contains(reversers.get().get(i).toLowerCase())){yes = true; reverse = true;}
        }
        for(int i = 0; i < quotes.get().size(); i++){
            if(content.toLowerCase().contains(quotes.get().get(i)) && content.toLowerCase().indexOf(quotes.get().get(i), content.indexOf(quotes.get().get(i)) + 1) != -1 && quote == null){no = true; quote = quotes.get().get(i);}
        }
        for(int i = 0; i < mether.get().size(); i++){
            if(content.toLowerCase().contains(mether.get().get(i))){yes = true; dometh = true;}
        }
        for(int i = 0; i < dont.get().size(); i++){
            if(content.toLowerCase().contains(dont.get().get(i))){yes = false;}
        }
        boolean[] isit = new boolean[contain.get().size()];
        for(int i = 0; i < contain.get().size(); i++){
            isit[i] = content.toLowerCase().contains(contain.get().get(i).toLowerCase());
        }
        for(int i = 0;i < isit.length; i++){
            if(!isit[i]){yes = false;}
        }
        if(soutput.get()){
            boolean send = true;
            boolean[] maybe = new boolean[mustcontain.get().size()];
            boolean theyes = false;
            for(int i = 0; i < omessage.get().size(); i++){
                if(content.toLowerCase().contains(omessage.get().get(i).toLowerCase())){theyes = true;}
            }
            if(!theyes){
                send = false;
            }
            for(int i = 0; i < mustcontain.get().size(); i++){
                maybe[i] = content.toLowerCase().contains(mustcontain.get().get(i).toLowerCase());
            }
            for(int i = 0; i < maybe.length; i++){
                if (!maybe[i]) {send = false; break;}
            }
            for(int i = 0; i < dontoutput.get().size(); i++){
                if(content.toLowerCase().contains(dontoutput.get().get(i).toLowerCase())){send = false;}
            }
            if(send){
                if(content.length() > 256){msg(content.substring(0,255));} else{msg(content);}
            }
        }
        if(yes && no){
            if(hidetr.get()){event.cancel();}
            if(reverse){
                solution = String.valueOf(new StringBuilder(content.split(quote)[1]).reverse());
            } else if(dometh) {
                boolean cum = false;
                int themeth;
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
                    return;
                } else{
                    solution = String.valueOf(themeth);
                }

            } else {
                solution = content.split(quote)[1];
            }
                msg(solution);
        }

    }
}
