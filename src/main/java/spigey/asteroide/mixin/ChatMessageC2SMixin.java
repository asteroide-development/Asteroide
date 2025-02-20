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
import spigey.asteroide.modules.EncryptChatModule;
import spigey.asteroide.modules.WordFilterModule;
import spigey.asteroide.util;

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
    private void onChatMessageC2SPacket(String string, Instant timestamp, long salt, MessageSignatureData signature, LastSeenMessageList.Acknowledgment acknowledgment, CallbackInfo ci) throws Exception {
        if(mc.isInSingleplayer()) return;
        if (Modules.get().get(WordFilterModule.class).isActive()) {
            WordFilterModule filter = Modules.get().get(WordFilterModule.class);
            String[] datshit = this.chatMessage.split(" ");
            StringBuilder message = new StringBuilder();
            for (int i = 0; i < datshit.length; i++) {
                for (int j = 0; j < filter.messages.get().size(); j++) {
                    if (datshit[i].toLowerCase().contains(filter.messages.get().get(j).toLowerCase())) {
                        if (filter.woblox.get()) {
                            StringBuilder temp = new StringBuilder();
                            for (int k = 0; k < datshit[i].length(); k++) {
                                temp.append(filter.roblock.get());
                            }
                            datshit[i] = temp.toString();
                        } else {
                            datshit[i] = filter.replacement.get();
                        }
                    }
                }
            }
            for (int i = 0; i < datshit.length; i++) {
                message.append(datshit[i]).append(" ");
            }
            this.signature = new MessageSignatureData(new byte[256]);
            this.chatMessage = message.toString().trim();
        }
        if(Modules.get().get(EncryptChatModule.class).isActive() && Modules.get().get(EncryptChatModule.class).encrypt.get() && String.format("STRT\"%s\"", util.encrypt(chatMessage, Modules.get().get(EncryptChatModule.class).encryptionKey.get())).length() <= 256) this.chatMessage = String.format("STRT\"%s\"", util.encrypt(chatMessage, Modules.get().get(EncryptChatModule.class).encryptionKey.get()));
    }
}
