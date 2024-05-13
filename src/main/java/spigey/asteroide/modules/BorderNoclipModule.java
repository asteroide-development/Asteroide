package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import spigey.asteroide.AsteroideAddon;

public class BorderNoclipModule extends Module {
    public BorderNoclipModule() {
        super(AsteroideAddon.CATEGORY, "border-noclip", "Removes the world border collision");
    }
}


