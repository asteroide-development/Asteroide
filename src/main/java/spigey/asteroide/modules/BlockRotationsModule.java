package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.entity.player.PlaceBlockEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import spigey.asteroide.AsteroideAddon;

public class BlockRotationsModule extends Module {
    public BlockRotationsModule() { super(AsteroideAddon.CATEGORY, "Block-Rotations", "Changes the rotation of blocks you place"); }
    private SettingGroup sgGeneral = settings.getDefaultGroup();
    private Setting<Direction> direction = sgGeneral.add(new EnumSetting.Builder<Direction>()
        .name("Direction")
        .description("Direction to face blocks in")
        .defaultValue(Direction.Down)
        .build()
    );

    @EventHandler
    private void onBlockPlace(PlaceBlockEvent event) {
        if(!isActive()) return;
        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(
            direction.get().getYaw(),
            direction.get().getPitch(),
            mc.player.isOnGround(),
            mc.player.horizontalCollision
        ));
    }

    private enum Direction {
        Up(0f, 90f),
        Down(0f, -90f),
        North(0f, 0f),
        East(90f, 0f),
        West(-90f, 0f),
        South(180f, 0f);

        private final float yaw;
        private final float pitch;
        Direction(float yaw, float pitch){ this.yaw = yaw; this.pitch = pitch; }

        public float getYaw(){ return yaw; }
        public float getPitch(){ return pitch; }
    }
}
