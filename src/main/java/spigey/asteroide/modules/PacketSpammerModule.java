package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import spigey.asteroide.AsteroideAddon;

public class PacketSpammerModule extends Module {
    public PacketSpammerModule() {
        super(AsteroideAddon.CATEGORY, "Packet-Kick", "Kicks you for sending too many packets.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> amount = sgGeneral.add(new IntSetting.Builder()
        .name("amount")
        .description("Amount of packets to send.")
        .defaultValue(10000)
        .min(-1)
        .sliderMin(1)
        .sliderMax(20000)
        .build()
    );

    @Override
    public void onActivate() {
        new Thread(this::spam).start();
        info("Sent " + amount.get() + " packets.");
        toggle();
    }

    private void spam(){
        for(int i = 0; i < amount.get(); i++) {
            if(mc.player == null || mc.world == null) { error(mc.player == null ? "Player is null" : "World is null"); break; }
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), mc.player.isOnGround(), true));
        }
    }
}
