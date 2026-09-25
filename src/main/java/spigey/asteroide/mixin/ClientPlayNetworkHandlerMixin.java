package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spigey.asteroide.modules.NoXaeroDisableModule;
import spigey.asteroide.modules.TypoModule;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @ModifyVariable(method = "sendChatCommand(Ljava/lang/String;)V", at = @At("HEAD"), argsOnly = true)
    private String modifyCommand(String command) {
        TypoModule typo = Modules.get().get(TypoModule.class);
        if(!typo.isActive() || !typo.commands.get()) return command;
        for(int i = 0; i < typo.keywords.get().size(); i++) {
            if (!command.toLowerCase().contains(typo.keywords.get().get(i).toLowerCase())) continue;
            if(typo.replacements.get().size() <= i) break;
            command = command.replace(typo.keywords.get().get(i), typo.replacements.get().get(i));
        }
        return command;
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    private void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci){
        try{
            NoXaeroDisableModule xDisable = Modules.get().get(NoXaeroDisableModule.class);
            if(!xDisable.isActive()) return;
            String content = packet.content().getString();
            if(xDisable.allowCaveMode.get() && content.contains(xDisable.caveModeString.get())) ci.cancel();
            if(xDisable.allowMinimap.get() && content.contains(xDisable.minimapString.get())) ci.cancel();
        }catch(Exception e){/**/}
    }
}
