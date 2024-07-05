package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.StairsBlock;
import net.minecraft.util.math.Direction;
import spigey.asteroide.AsteroideAddon;
import net.minecraft.registry.Registries;

public class FastStaircaseModule extends Module {
    public FastStaircaseModule() {super(AsteroideAddon.CATEGORY, "fast-staircase", "Makes you walk up stairs quickly");}

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(!isActive()){return;}
        if(!Registries.BLOCK.getId(mc.world.getBlockState(mc.player.getBlockPos()).getBlock()).toString().endsWith("_stairs")) return;
        if(mc.player.getHorizontalFacing() == mc.world.getBlockState(mc.player.getBlockPos()).get(StairsBlock.FACING) && mc.player.input.movementForward > 0) mc.player.jump();
    }
}
