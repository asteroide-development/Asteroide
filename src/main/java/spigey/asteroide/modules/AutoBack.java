package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoRespawn;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;

import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.msg;

public class AutoBack extends Module {
    public AutoBack() {
        super(AsteroideAddon.CATEGORY, "Auto-Back", "REQUIRES AUTORESPAWN Automatically runs /back upon dying");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<AutoBack.AutoBackMode> mode = sgGeneral.add(new EnumSetting.Builder<AutoBack.AutoBackMode>()
        .name("event type")
        .description("Event type to trigger")
        .defaultValue(AutoBack.AutoBackMode.OnDeath)
        .build()
    );
    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("trigger messages")
        .description("trigger the autoback when the message contains one of these strings")
        .defaultValue("/back")
        .visible(() -> mode.get() == AutoBackMode.OnMessage)
        .build()
    );
    public Setting<String> backMessage = sgGeneral.add(new StringSetting.Builder()
        .name("back-message")
        .description("The message to send when dying.")
        .defaultValue("/back")
        .build()
    );
    public Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("Delay in ticks before sending the back command.")
        .defaultValue(5)
        .min(0)
        .sliderMax(40)
        .build()
    );

    private int tick = -1;

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event){
        if(mode.get() != AutoBackMode.OnDeath || !isActive() || !(event.packet instanceof DeathMessageS2CPacket packet)) return;
        if(mc.world.getEntityById(packet.playerId()) != mc.player){return;}
        this.tick = delay.get();
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event){
        if(!isActive() || mode.get() != AutoBackMode.OnMessage) return;
        for(String msg : messages.get()) if(!event.getMessage().getString().toLowerCase().contains(msg.toLowerCase())) return;
        this.tick = delay.get();
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(this.tick == -1 || !isActive()) return;
        if(this.tick > 0) { this.tick--; return; }
        msg(backMessage.get());
        this.tick = -1;
    }

    @Override
    public void onActivate() {
        Module thing = Modules.get().get(AutoRespawn.class);
        if(!thing.isActive()){thing.toggle();}
    }

    private static final Random random = new Random();
    public static int randomNum(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    public enum AutoBackMode {
        OnDeath,
        OnMessage
    }
}
