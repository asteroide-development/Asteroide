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

    /**
     * @author Spigey
     * @reason Border Noclip
     */
    @Overwrite
    public boolean canCollide(Entity entity, Box box) {
        return !Modules.get().get(BorderNoclipModule.class).isActive();
    }
    /**
     * @author Spigey
     * @reason Border Noclip
     */
    @Overwrite
    public double getDamagePerBlock(){
        return Modules.get().get(BorderNoclipModule.class).isActive() ? Modules.get().get(BorderNoclipModule.class).damage.get() : 0.2; // why the fuck isn't super.getDamagePerBlock() working
    }
}
