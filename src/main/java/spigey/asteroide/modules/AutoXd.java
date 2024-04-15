package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.*;
import spigey.asteroide.AsteroideAddon;


import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.msg;

public class AutoXd extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Randomly takes the message from the list and sends on each death.").defaultValue("xd", "skill issue").build());

    public AutoXd() {
        super(AsteroideAddon.CATEGORY, "auto-xd", "Sends a message when someone in your render distance dies");
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
                msg(messages.get().isEmpty() ? "LMAOOO" : messages.get().get(randomNum(0, messages.get().size() - 1)));
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void PacketReceive(PacketEvent.Receive event){
        if(!(event.packet instanceof GameMessageS2CPacket)){return;}
        if(!((GameMessageS2CPacket) event.packet).content().toString().equals("translation{key='chat.disabled.options', args=[]}[style={color=red}]")){return;}
        event.cancel();
    }
    private static final Random random = new Random();
    public static int randomNum(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
