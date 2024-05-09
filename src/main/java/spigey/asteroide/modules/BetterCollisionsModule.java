package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.mixininterface.IVec3d;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.*;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.shape.VoxelShapes;

import java.util.List;
import spigey.asteroide.AsteroideAddon;


public class BetterCollisionsModule extends Module {
    public BetterCollisionsModule() {
        super(AsteroideAddon.CATEGORY, "better-collisions", "Meteor Client's Collisions, but better");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("What blocks should be added collision box.")
        .build()
    );

    private final Setting<BetterCollisionsModule.BlockCollisionShapes> mode = sgGeneral.add(new EnumSetting.Builder<BetterCollisionsModule.BlockCollisionShapes>()
        .name("Collision Shape")
        .description("Shape of the block collisions")
        .defaultValue(BlockCollisionShapes.FullCube)
        .build()
    );
    private final Setting<Double> boxsize = sgGeneral.add(new DoubleSetting.Builder()
        .name("Collision Height IN DEVELOPMENT")
        .description("Height of the collision box")
        .defaultValue(1)
        .min(0)
        .sliderMax(3)
        .visible(() -> mode.get() == BlockCollisionShapes.CuboidINDEVELOPMENT)
        .build()
    );

    private final Setting<Boolean> magma = sgGeneral.add(new BoolSetting.Builder()
        .name("magma")
        .description("Prevents you from walking over magma blocks.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> unloadedChunks = sgGeneral.add(new BoolSetting.Builder()
        .name("unloaded-chunks")
        .description("Stops you from going into unloaded chunks.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> ignoreBorder = sgGeneral.add(new BoolSetting.Builder()
        .name("ignore-border")
        .description("Removes world border collision.")
        .defaultValue(false)
        .build()
    );

    @EventHandler
    private void onCollisionShape(CollisionShapeEvent event) {
        if (mc.world == null || mc.player == null) return;
        if (!event.state.getFluidState().isEmpty()) return;
        if (blocks.get().contains(event.state.getBlock())) {
            if(mode.get() == BlockCollisionShapes.FullCube) event.shape = VoxelShapes.fullCube();
            if(mode.get() == BlockCollisionShapes.CuboidINDEVELOPMENT) event.shape = VoxelShapes.cuboid(1.0, 1.0, 1.0, 16.0, boxsize.get() * 16, 16.0);
        } else if (magma.get() && !mc.player.isSneaking()
            && event.state.isAir()
            && mc.world.getBlockState(event.pos.down()).getBlock() == Blocks.MAGMA_BLOCK) {
            event.shape = VoxelShapes.fullCube();
        }
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        int x = (int) (mc.player.getX() + event.movement.x) >> 4;
        int z = (int) (mc.player.getZ() + event.movement.z) >> 4;
        if (unloadedChunks.get() && !mc.world.getChunkManager().isChunkLoaded(x, z)) {
            ((IVec3d) event.movement).set(0, event.movement.y, 0);
        }
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (!unloadedChunks.get()) return;
        if (event.packet instanceof VehicleMoveC2SPacket packet) {
            if (!mc.world.getChunkManager().isChunkLoaded((int) packet.getX() >> 4, (int) packet.getZ() >> 4)) {
                mc.player.getVehicle().updatePosition(mc.player.getVehicle().prevX, mc.player.getVehicle().prevY, mc.player.getVehicle().prevZ);
                event.cancel();
            }
        } else if (event.packet instanceof PlayerMoveC2SPacket packet) {
            if (!mc.world.getChunkManager().isChunkLoaded((int) packet.getX(mc.player.getX()) >> 4, (int) packet.getZ(mc.player.getZ()) >> 4)) {
                event.cancel();
            }
        }
    }

    private enum BlockCollisionShapes {
        FullCube,
        CuboidINDEVELOPMENT
    }
    public boolean ignoreBorder() {
        return  isActive() && ignoreBorder.get();
    }
}