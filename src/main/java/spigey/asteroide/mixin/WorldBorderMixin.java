package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.AutoRespawn;
import net.minecraft.entity.Entity;
import net.minecraft.network.NetworkState;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.border.WorldBorder;

import org.spongepowered.asm.mixin.*;
import spigey.asteroide.modules.BorderNoclipModule;

@Mixin(WorldBorder.class)
public abstract class WorldBorderMixin {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean canCollide(Entity entity, Box box) {
        return !Modules.get().get(BorderNoclipModule.class).isActive();
    }
}
