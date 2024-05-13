package spigey.asteroide.mixin;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.time.Instant;

import static meteordevelopment.meteorclient.utils.player.ChatUtils.info;

@Mixin(ChatMessageC2SPacket.class)
public class ChatMessageC2SMixin {
    @Unique
    private String content;

    @Inject(method = "<init>(Ljava/lang/String;Ljava/time/Instant;JLnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/network/message/LastSeenMessageList$Acknowledgment;)V", at = @At("TAIL"), cancellable = true)
    private void onChatMessagePacketConstructed(String content, Instant timestamp, long salt, MessageSignatureData signature, LastSeenMessageList.Acknowledgment acknowledgment, CallbackInfo ci) {
        this.content = "ermm what the sigma";
        System.out.println("ermm what the sigma");
        info("ermm what the flip");
        ci.cancel();
    }
}
