package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spigey.asteroide.modules.BlockHitboxesModule;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class BlockOutlineMixin {
    @Inject(method = "getOutlineShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", at = @At("HEAD"), cancellable = true)
    private void enlargeOutline(BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        try {
            Modules moduless = Modules.get();
            assert moduless != null;
            BlockHitboxesModule module = moduless.get(BlockHitboxesModule.class);
            assert module != null;
            if(module.isActive()) if (module.blocks.get().contains(((BlockState) (Object) this).getBlock())) cir.setReturnValue(VoxelShapes.fullCube());
        }catch(Exception e){
            // nigga
        }
    }
}
