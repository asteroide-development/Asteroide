package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.message.LastSeenMessageList;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.world.border.WorldBorder;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spigey.asteroide.modules.WordFilterModule;

import java.time.Instant;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.player.ChatUtils.info;

@Mixin(ChatMessageC2SPacket.class)
public abstract class ChatMessageC2SMixin {
    @Mutable
    @Shadow
    @Final
    private String chatMessage;
    @Mutable
    @Shadow
    @Final
    private @Nullable MessageSignatureData signature;
    String content;

    /**
     * @author Spigey
     * @reason Word Filter
     */
    @Inject(method = "<init>(Ljava/lang/String;Ljava/time/Instant;JLnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/network/message/LastSeenMessageList$Acknowledgment;)V", at = @At("RETURN"))
    private void onChatMessageC2SPacket(String string, Instant timestamp, long salt, MessageSignatureData signature, LastSeenMessageList.Acknowledgment acknowledgment, CallbackInfo ci) {
        if (Modules.get().get(WordFilterModule.class).isActive()) {
            if(!mc.isInSingleplayer()){
                mc.getCurrentServerEntry().setSecureChatEnforced(false);
            if (!mc.getCurrentServerEntry().isSecureChatEnforced()) {
                WordFilterModule filter = Modules.get().get(WordFilterModule.class);
                String[] datshit = this.chatMessage.split(" ");
                StringBuilder message = new StringBuilder();
                for (int i = 0; i < datshit.length; i++) {
                    for (int j = 0; j < filter.messages.get().size(); j++) {
                        if (datshit[i].toLowerCase().contains(filter.messages.get().get(j).toLowerCase())) {
                            if (filter.woblox.get()) {
                                String temp = "";
                                for (int k = 0; k < datshit[i].length(); k++) {
                                    temp += filter.roblock.get();
                                }
                                datshit[i] = temp;
                            } else {
                                datshit[i] = filter.replacement.get();
                            }
                        }
                    }
                }
                for (int i = 0; i < datshit.length; i++) {
                    message.append(datshit[i]).append(" ");
                }
                this.chatMessage = message.toString().trim();
            }
            }
        }
    }
}
