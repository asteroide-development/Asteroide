package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.LookAtS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import spigey.asteroide.AsteroideAddon;

public class NoRecoilModule extends Module {
    public NoRecoilModule() { super(AsteroideAddon.CATEGORY, "No-Recoil", "Attempts to get rid of recoil on guns."); }
    private final SettingGroup sgGeneral = settings.createGroup("General");
    private final SettingGroup sgThresholds = settings.createGroup("Thresholds");

    private final Setting<Boolean> debugMode = sgGeneral.add(new BoolSetting.Builder()
        .name("Debug Mode")
        .description("Log rotations from the server to use in the settings below")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> multiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("Anti-Recoil Multiplier")
        .description("Ex. 0.5x will revert the recoil by 0.5x")
        .defaultValue(1.0d)
        .sliderRange(0.0d, 2.0d)
        .build()
    );

    private final Setting<Double> minYaw = sgThresholds.add(new DoubleSetting.Builder()
        .name("Min Yaw Recoil")
        .description("Minimum yaw recoil to revert. Sign-insensitive.").defaultValue(0.05d).sliderRange(0.0d, 10.0d).build());
    private final Setting<Double> maxYaw = sgThresholds.add(new DoubleSetting.Builder()
        .name("Max Yaw Recoil")
        .description("Maximum yaw recoil to revert. Sign-insensitive.").defaultValue(10.0d).sliderRange(3.0d, 20.0d).build());
    private final Setting<Double> minPitch = sgThresholds.add(new DoubleSetting.Builder()
        .name("Min Pitch Recoil")
        .description("Minimum pitch recoil to revert. Sign-insensitive.").defaultValue(0.02d).sliderRange(0.0d, 10.0d).build());
    private final Setting<Double> maxPitch = sgThresholds.add(new DoubleSetting.Builder()
        .name("Max Pitch Recoil")
        .description("Maximum pitch recoil to revert. Sign-insensitive.").defaultValue(3.0d).sliderRange(3.0d, 20.0d).build());

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        float yaw;
        float pitch;
        double multiplierVal = multiplier.get();

        if(event.packet instanceof LookAtS2CPacket packet) {
            Vec3d target = packet.getTargetPosition(mc.world);

            // God damn mess below
            double dx = target.x - mc.player.getX();
            double dz = target.z - mc.player.getZ();

            float targetYaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            float targetPitch = (float) -Math.toDegrees(Math.atan2(target.y - mc.player.getEyeY(), Math.sqrt(dx * dx + dz * dz)));

            yaw = MathHelper.wrapDegrees(targetYaw - mc.player.getYaw());
            pitch = MathHelper.wrapDegrees(targetPitch - mc.player.getPitch());

            multiplierVal--;
            event.cancel();
        } else if(event.packet instanceof PlayerPositionLookS2CPacket packet){
            yaw = packet.change().yaw();
            pitch = packet.change().pitch();
        } else return;
        if(debugMode.get()) info(String.format("Yaw %.2f | Pitch %.2f", yaw, pitch));

        if(isInRange(pitch, RotationType.Pitch)) mc.player.setPitch((float) (mc.player.getPitch() - (pitch * multiplierVal)));
        if(isInRange(yaw, RotationType.Yaw)) mc.player.setYaw((float) (mc.player.getYaw() - (yaw * multiplierVal)));
        if(!isInRange(pitch, RotationType.Pitch) && !isInRange(yaw, RotationType.Yaw)) return;
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
            // mc.player.getX(), mc.player.getY(), mc.player.getZ(),
            (float) (mc.player.getYaw() - (yaw * multiplierVal)),
            (float) (mc.player.getPitch() - (pitch * multiplierVal)),
            mc.player.isOnGround(),
            true
        ));
    }

    private boolean isInRange(float rotation, RotationType type){
        rotation = Math.abs(rotation);
        if(type == RotationType.Yaw) return rotation >= Math.abs(minYaw.get()) && rotation <= Math.abs(maxYaw.get());
        if(type == RotationType.Pitch) return rotation >= Math.abs(minPitch.get()) && rotation <= Math.abs(maxPitch.get());
        return false;
    }
    private enum RotationType { Yaw, Pitch }
}
