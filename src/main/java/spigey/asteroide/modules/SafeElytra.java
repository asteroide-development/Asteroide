package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import spigey.asteroide.AsteroideAddon;

public class SafeElytra extends Module {
    public SafeElytra() { super(AsteroideAddon.CATEGORY, "Safe-Elytra", "Attempts to prevent dying from kinetic energy."); }

    private final SettingGroup sgGeneral = settings.createGroup("General");
    private final SettingGroup sgValues = settings.createGroup("Values");
    private final Setting<Boolean> verticalVelocity = sgGeneral.add(new BoolSetting.Builder()
        .name("Vertical Velocity")
        .description("Prevents high vertical velocity.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> walls = sgGeneral.add(new BoolSetting.Builder()
        .name("Walls")
        .description("Prevents high velocity when looking at walls.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> elytraDisable = sgGeneral.add(new BoolSetting.Builder()
        .name("Disable Elytra")
        .description("Disables elytra when flying at a wall too quickly.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Double> maxVelocity = sgValues.add(new DoubleSetting.Builder()
        .name("Max Fall Velocity")
        .description("The maximum velocity you can fall at.")
        .defaultValue(0.5)
        .sliderMax(3)
        .visible(verticalVelocity::get)
        .build()
    );
    private final Setting<Double> maxWallVelocity = sgValues.add(new DoubleSetting.Builder()
        .name("Max Wall Velocity")
        .description("The maximum velocity you can move at when looking at a wall.")
        .defaultValue(0.3)
        .sliderMax(3)
        .visible(walls::get)
        .build()
    );
    private final Setting<Integer> wallRange = sgValues.add(new IntSetting.Builder()
        .name("Wall Range")
        .description("Wall raycast range")
        .defaultValue(20)
        .min(0)
        .sliderMax(50)
        .visible(walls::get)
        .build()
    );

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(!mc.player.isInPose(EntityPose.GLIDING) || !isActive()) return;
        if(verticalVelocity.get() && mc.player.getVelocity().y < -maxVelocity.get()) mc.player.setVelocity(mc.player.getVelocity().x, -maxVelocity.get(), mc.player.getVelocity().z);
        if(!walls.get()) return;
        if(mc.player.raycast(wallRange.get(), 0f, false).getType() != HitResult.Type.BLOCK) return;
        Vec3d vel = mc.player.getVelocity();
        mc.player.setVelocity(
            MathHelper.clamp(vel.x, -maxWallVelocity.get(), maxWallVelocity.get()),
            MathHelper.clamp(vel.y, -maxWallVelocity.get(), maxWallVelocity.get()),
            MathHelper.clamp(vel.z, -maxWallVelocity.get(), maxWallVelocity.get())
        );
        if(!elytraDisable.get()) return;
        mc.player.fallDistance = 0;
        mc.player.setOnGround(true);
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, true));
    }
}


