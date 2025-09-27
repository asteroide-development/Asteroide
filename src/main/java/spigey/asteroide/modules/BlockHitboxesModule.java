package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.entity.player.PlayerMoveEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.RaycastContext;
import spigey.asteroide.AsteroideAddon;
import net.minecraft.util.hit.BlockHitResult;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.block.Block;
import net.minecraft.util.shape.VoxelShapes;
import java.util.List;
import net.minecraft.world.RaycastContext;

import java.util.List;


public class BlockHitboxesModule extends Module {
    public BlockHitboxesModule() {
        super(AsteroideAddon.CATEGORY, "Block-Hitboxes", "Increases some blocks hitbox sizes for easier aiming");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("What blocks should have an increased hitbox size.")
        .build()
    );

    public final Setting<Double> miX = sgGeneral.add(new DoubleSetting.Builder()
        .name("min-size-x")
        .description("Minimum X size of the hitbox.")
        .defaultValue(0)
        .sliderMin(-2)
        .sliderMax(2)
        .build()
    );
    public final Setting<Double> miY = sgGeneral.add(new DoubleSetting.Builder()
        .name("min-size-Y")
        .description("Minimum Y size of the hitbox.")
        .defaultValue(0)
        .sliderMin(-2)
        .sliderMax(2)
        .build()
    );
    public final Setting<Double> miZ = sgGeneral.add(new DoubleSetting.Builder()
        .name("min-size-Z")
        .description("Minimum Z size of the hitbox.")
        .defaultValue(0)
        .sliderMin(-2)
        .sliderMax(2)
        .build()
    );
    public final Setting<Double> maX = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-size-x")
        .description("Maximum X size of the hitbox.")
        .defaultValue(1)
        .sliderMin(-2)
        .sliderMax(2)
        .build()
    );
    public final Setting<Double> maY = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-size-y")
        .description("Maximum Y size of the hitbox.")
        .defaultValue(1)
        .sliderMin(-2)
        .sliderMax(2)
        .build()
    );
    public final Setting<Double> maZ = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-size-z")
        .description("Maximum Z size of the hitbox.")
        .defaultValue(1)
        .sliderMin(-2)
        .sliderMax(2)
        .build()
    );
    // BlockOutlineMixin.java
}
