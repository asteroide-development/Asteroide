package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;

import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.msg;

public class DeathNotifier extends Module {
    public DeathNotifier() {
        super(AsteroideAddon.CATEGORY, "death-notifier", "Tells you when someone dies including their coordinates");
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event){
        if(event.packet instanceof EntitiesDestroyS2CPacket packet){
            List<Integer> entityIds = packet.getEntityIds();
            for(int entityId : entityIds){
                assert mc.world != null;
                Entity entity = mc.world.getEntityById(entityId);
                assert entity != null;
                if(!(entity instanceof PlayerEntity) && !(entity instanceof OtherClientPlayerEntity)){return;}
                if(entity == mc.player){return;}
                String[] EntityString = entity.toString().split(",");
                /* for(int i = 0; i < EntityString.length; i++){
                    if(!roundValues.get()){return;}
                    EntityString[i] = EntityString[i].replaceAll("...$", "");
                } */
                info("Player " + entity.toString().split("'")[1] + " died at X:" + EntityString[2].replace("x=", "") + ", Y:" + EntityString[3].replace("y=", "") + ", Z:" + EntityString[4].replace("z=", "").substring(0, EntityString[4].indexOf("]") - 2));
            }
        }
    } //        OtherClientPlayerEntity['NocturnalNext_'/4317439, l='ClientLevel', x=-786.23, y=61.00, z=-2211.42]
}
