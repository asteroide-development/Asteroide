package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShapes;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

public class SwimModule extends Module {
    public SwimModule() {
        super(AsteroideAddon.CATEGORY, "swim", "Lets you swim in the air");
    }
    @EventHandler
    private void onCollisionShape(CollisionShapeEvent event){
        assert mc.player != null;
        int PlayerX = mc.player.getBlockPos().getX();
        int PlayerY = mc.player.getBlockPos().getY();
        int PlayerZ = mc.player.getBlockPos().getZ();
        BlockPos pos = event.pos;
        BlockPos lock = new BlockPos(PlayerX, PlayerY + 1, PlayerZ);
        if(lock.equals(pos)){
            event.shape = VoxelShapes.fullCube();
        }
    }
}


