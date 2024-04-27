package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import spigey.asteroide.AsteroideAddon;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static meteordevelopment.meteorclient.utils.player.ChatUtils.sendMsg;
import static spigey.asteroide.util.banstuff;
import static spigey.asteroide.util.msg;

public class AutoKys extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Randomly takes the message from the list and sends on each death.").defaultValue("kys", "that's fucking luck").build());

    public AutoKys() {
        super(AsteroideAddon.CATEGORY, "auto-kys", "Sends a message when you die");
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event){
        banstuff();
        if(event.packet instanceof DeathMessageS2CPacket packet){
            Entity entity = mc.world.getEntityById(packet.getEntityId());
            if(entity != mc.player){return;}
            msg(messages.get().isEmpty() ? "kill yourself you fucking wimp" : messages.get().get(randomNum(0, messages.get().size() - 1)));
        }
    }
    private static final Random random = new Random();
    public static int randomNum(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
