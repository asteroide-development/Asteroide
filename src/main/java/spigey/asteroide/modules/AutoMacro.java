package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;
import meteordevelopment.meteorclient.systems.config.Config;
import spigey.asteroide.util;

import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.banstuff;
import static spigey.asteroide.util.msg;

public class AutoMacro extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("messages")
        .description("Keywords to execute the macro")
        .defaultValue("Hello world!")
        .build()
    );

    private final Setting<List<String>> macro = sgGeneral.add(new StringListSetting.Builder()
        .name("macro")
        .description("macro name to execute")
        .defaultValue("testmacro")
        .build()
    );

    private final Setting<AutoChatGame.Mode> mode = sgGeneral.add(new EnumSetting.Builder<AutoChatGame.Mode>().name("delay type").description("Whether it waits for a random or precise amount of time").defaultValue(AutoChatGame.Mode.Random).build());
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder().name("delay").description("The delay before sending the solution in ticks").defaultValue(30).min(0).sliderMax(200).build());
    private final Setting<Integer> minoffset = sgGeneral.add(new IntSetting.Builder().name("delay min offset").description("Minimum offset from the delay in ticks").defaultValue(0).min(0).sliderMax(40).visible(() -> mode.get() == AutoChatGame.Mode.Random).build());
    private final Setting<Integer> maxoffset = sgGeneral.add(new IntSetting.Builder().name("delay max offset").description("Maximum offset from the delay in ticks").defaultValue(10).min(0).sliderMax(40).visible(() -> mode.get() == AutoChatGame.Mode.Random).build());

    public AutoMacro() {
        super(AsteroideAddon.CATEGORY, "Auto-Macro", "Automatically runs a command when a specified message is sent in the chat");
    }

    private int tick = -1;
    private String message;

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event){
        if(!isActive()) return;
        String content = event.getMessage().getString();
        for(int i = 0; i < messages.get().size(); i++){
            if(!content.toLowerCase().contains(messages.get().get(i).toLowerCase())) continue;
            if(macro.get().size() <= i) continue;
            if(macro.get().get(i) != null){
                this.message = macro.get().get(i);
                if(mode.get() == AutoChatGame.Mode.Precise) this.tick = delay.get();
                else {
                    int rdm = new Random().nextInt(maxoffset.get() - minoffset.get() + 1) + minoffset.get();
                    if (Math.random() > 0.5) rdm = -rdm;
                    this.tick = delay.get() + rdm;
                }
            }
            else error("Error: Macro is null");
        }
        if(this.tick == 0) { msg(this.message); this.tick = -1; }
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(this.tick == -1 || !isActive()) return;
        if(this.tick > 0) { tick--; return; }
        msg(this.message);
        tick = -1;
    }
}
