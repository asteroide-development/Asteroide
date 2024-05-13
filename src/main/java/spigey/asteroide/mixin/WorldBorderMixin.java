package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.world.border.WorldBorder;

import org.spongepowered.asm.mixin.*;
import spigey.asteroide.modules.BorderNoclipModule;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(WorldBorder.class)
public abstract class WorldBorderMixin {

    @Shadow
    private double damagePerBlock;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean canCollide(Entity entity, Box box) {
        return !Modules.get().get(BorderNoclipModule.class).isActive();
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public double getDamagePerBlock(){
        if(!Modules.get().get(BorderNoclipModule.class).isActive()) {
            assert mc.world != null;
            return mc.world.getWorldBorder().getDamagePerBlock();
        }
        return Modules.get().get(BorderNoclipModule.class).damage.get();
    }
}