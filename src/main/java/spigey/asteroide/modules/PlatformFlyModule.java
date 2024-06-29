package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoRespawn;
import meteordevelopment.meteorclient.systems.modules.movement.AirJump;
import meteordevelopment.meteorclient.utils.misc.input.Input;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import org.lwjgl.glfw.GLFW;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

import static meteordevelopment.meteorclient.MeteorClient.mc;


public class PlatformFlyModule extends Module {
    public PlatformFlyModule() {
        super(AsteroideAddon.CATEGORY, "platform-fly", "Lets you walk on air");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> allowJumping = sgGeneral.add(new BoolSetting.Builder()
        .name("Ease Jumping (Buggy)")
        .description("Makes getting up easier")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> allowSneaking = sgGeneral.add(new BoolSetting.Builder()
        .name("Allow Sneaking")
        .description("Allows sneaking while in the air")
        .defaultValue(false)
        .build()
    );
    @EventHandler
    private void onCollisionShape(CollisionShapeEvent event){
        assert mc.player != null;
        boolean sneaking = mc.options.sneakKey.isPressed();
        boolean jumping = mc.options.jumpKey.isPressed();
        if(sneaking && !allowSneaking.get()){return;}
        if(jumping && allowJumping.get() && (util.randomNum(1,5) == 3)){mc.player.jump();} // I was too lazy to add a proper delay
        int PlayerX = mc.player.getBlockPos().getX();
        int PlayerY = mc.player.getBlockPos().getY();
        int PlayerZ = mc.player.getBlockPos().getZ();
        BlockPos pos = event.pos;
        BlockPos lock = new BlockPos(PlayerX, PlayerY - 1, PlayerZ);
        if(lock.equals(pos)){
            event.shape = VoxelShapes.fullCube();
        }
    }
}


