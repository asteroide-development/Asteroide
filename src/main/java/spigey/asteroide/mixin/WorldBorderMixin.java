package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spigey.asteroide.modules.BorderNoclipModule;

@Mixin(WorldBorder.class)
public abstract class WorldBorderMixin {
    @Inject(method = "canCollide", at = @At("HEAD"), cancellable = true)
    private void canCollide(CallbackInfoReturnable<Boolean> info) {
        if (Modules.get().get(BorderNoclipModule.class).isActive()) info.setReturnValue(false);
    }

    /**
     * @author Spigey
     * @reason because i can
     */
    @Overwrite
    public double getDamagePerBlock(){
        try{
            return Modules.get().get(BorderNoclipModule.class).isActive() ? Modules.get().get(BorderNoclipModule.class).damage.get() : 0.2; // why the fuck isn't super.getDamagePerBlock() working
        }catch(Exception L){ /**/ }
        return 0;
    }
}
