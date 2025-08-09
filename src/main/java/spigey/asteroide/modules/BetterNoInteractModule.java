package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.entity.player.InteractBlockEvent;
import meteordevelopment.meteorclient.events.entity.player.StartBreakingBlockEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.player.NoInteract;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.Block;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

public class BetterNoInteractModule extends Module {
    public BetterNoInteractModule() {
        super(AsteroideAddon.CATEGORY, "better-no-interact", "No interact, but better");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<Block>> blockMine = sgGeneral.add(new BlockListSetting.Builder()
        .name("block-mine")
        .description("Cancels block mining.")
        .build()
    );

    private final Setting<NoInteract.ListMode> blockMineMode = sgGeneral.add(new EnumSetting.Builder<NoInteract.ListMode>()
        .name("block-mine-mode")
        .description("List mode to use for block mine.")
        .defaultValue(NoInteract.ListMode.BlackList)
        .build()
    );

    private final Setting<List<Block>> blockInteract = sgGeneral.add(new BlockListSetting.Builder()
        .name("block-interact")
        .description("Cancels block interaction.")
        .build()
    );

    private final Setting<NoInteract.ListMode> blockInteractMode = sgGeneral.add(new EnumSetting.Builder<NoInteract.ListMode>()
        .name("block-interact-mode")
        .description("List mode to use for block interact.")
        .defaultValue(NoInteract.ListMode.BlackList)
        .build()
    );

    private final Setting<Boolean> togglePositions = sgGeneral.add(new BoolSetting.Builder()
        .name("Enable Positions")
        .description("Whether to enable/disable interaction based on position.")
        .defaultValue(false)
        .build()
    );

    private final Setting<PositionMode> positionMode = sgGeneral.add(new EnumSetting.Builder<PositionMode>()
        .name("disable-interaction-mode")
        .description("When to disable interaction with blocks.")
        .defaultValue(PositionMode.Below)
        .visible(togglePositions::get)
        .build()
    );

    private final Setting<XYZ> xyzSetting = sgGeneral.add(new EnumSetting.Builder<XYZ>()
        .name("Disable Interaction X/Y/Z")
        .description("Whether to disable interaction based on X, Y, or Z coordinates.")
        .defaultValue(XYZ.Y)
        .visible(togglePositions::get)
        .build()
    );

    private final Setting<Integer> position = sgGeneral.add(new IntSetting.Builder()
        .name("position")
        .description("Position to disable interaction at.")
        .defaultValue(-59)
        .sliderMin(-10000)
        .sliderMax(10000)
        .visible(togglePositions::get)
        .build()
    );

    private enum PositionMode {
        Above,
        Below,
        Same
    }

    private enum XYZ {
        X,
        Y,
        Z
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onStartBreakingBlockEvent(StartBreakingBlockEvent event) {
        if (!shouldAttackBlock(event.blockPos)) event.cancel();
    }

    @EventHandler
    private void onInteractBlock(InteractBlockEvent event) {
        if (!shouldInteractBlock(event.result)) event.cancel();
    }

    private boolean checkCoords(BlockPos poss) {
        if (togglePositions.get()) {
            if(xyzSetting.get() == XYZ.X){
                if (positionMode.get() == PositionMode.Above && poss.getX() <= this.position.get()) return true;
                if (positionMode.get() == PositionMode.Below && poss.getX() >= this.position.get()) return true;
                if (positionMode.get() == PositionMode.Same && poss.getX() != this.position.get()) return true;
            }

            if(xyzSetting.get() == XYZ.Y){
                if (positionMode.get() == PositionMode.Above && poss.getY() <= this.position.get()) return true;
                if (positionMode.get() == PositionMode.Below && poss.getY() >= this.position.get()) return true;
                if (positionMode.get() == PositionMode.Same && poss.getY() != this.position.get()) return true;
            }

            if(xyzSetting.get() == XYZ.Z){
                if (positionMode.get() == PositionMode.Above && poss.getZ() <= this.position.get()) return true;
                if (positionMode.get() == PositionMode.Below && poss.getZ() >= this.position.get()) return true;
                return positionMode.get() == PositionMode.Same && poss.getZ() != this.position.get();
            }
            return false;
        }
        return true;
    }

    private boolean shouldAttackBlock(BlockPos pos) {
        if (checkCoords(pos)) return true;
        if(mc.world == null) return false;
        Block block = mc.world.getBlockState(pos).getBlock();
        return blockMine.get().contains(block) ^ (blockMineMode.get() == NoInteract.ListMode.BlackList);
    }

    private boolean shouldInteractBlock(BlockHitResult result) {
        if (checkCoords(result.getBlockPos())) return true;
        if(mc.world == null) return false;
        Block block = mc.world.getBlockState(result.getBlockPos()).getBlock();
        return blockInteract.get().contains(block) ^ (blockInteractMode.get() == NoInteract.ListMode.BlackList);
    }
}


