package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoRespawn;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;

import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.banstuff;
import static spigey.asteroide.util.msg;

public class AutoBack extends Module {
    public AutoBack() {
        super(AsteroideAddon.CATEGORY, "auto-back", "REQUIRES AUTORESPAWN Automatically runs /back upon dying");
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
    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event){
        banstuff();
        if(!(mode.get() == AutoBackMode.OnDeath)){
            if(event.packet instanceof GameMessageS2CPacket){
                String content = String.valueOf(((GameMessageS2CPacket) event.packet).content().getString());
                for (int i = 0; i < messages.get().size(); i++) {
                    if (content.toLowerCase().contains(messages.get().get(i).toLowerCase())) {
                        msg("/back");
                    }
                }
            }
        }
        if(event.packet instanceof DeathMessageS2CPacket packet){
            assert mc.world != null;
            Entity entity = mc.world.getEntityById(packet.getEntityId());
            if(entity != mc.player){return;}
            msg("/back");
        }
    }

    @Override
    public void onActivate() {
        banstuff();
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
