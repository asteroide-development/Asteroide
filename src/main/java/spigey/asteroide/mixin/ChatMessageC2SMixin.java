package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.world.border.WorldBorder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import spigey.asteroide.modules.WordFilterModule;

import static meteordevelopment.meteorclient.utils.player.ChatUtils.info;

@Mixin(ChatMessageC2SPacket.class)
public abstract class ChatMessageC2SMixin {
    /**
     * @author Spigey
     * @reason Word Filter
     *
    @Overwrite
    public String chatMessage(){
        if(Modules.get().get(WordFilterModule.class).isActive()) return "WOAH DUDE!!! IT WORKED!!!111";
        return "ermm what the sigma.";
    } */
}
