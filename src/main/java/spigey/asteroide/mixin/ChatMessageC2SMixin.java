package spigey.asteroide.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.utils.player.ChatUtils.info;

@Mixin(ChatMessageC2SPacket.class)
public class ChatMessageC2SMixin {
    @Inject(method = "write", at = @At("HEAD"))
    private void onWrite(CallbackInfo info) {
        info("doen");
    }
}
