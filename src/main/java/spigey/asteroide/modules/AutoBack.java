package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoRespawn;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;

import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.msg;

public class AutoBack extends Module {
    public AutoBack() {
        super(AsteroideAddon.CATEGORY, "auto-back", "REQUIRES AUTORESPAWN Automatically runs /back upon dying");
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event){
        if(event.packet instanceof DeathMessageS2CPacket packet){
            Entity entity = mc.world.getEntityById(packet.getEntityId());
            if(entity != mc.player){return;}
            msg("/back");
        }
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
}
