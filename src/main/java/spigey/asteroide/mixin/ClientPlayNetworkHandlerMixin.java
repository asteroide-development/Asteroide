package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import spigey.asteroide.modules.TypoModule;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {
    @ModifyVariable(method = "sendChatCommand(Ljava/lang/String;)V", at = @At("HEAD"), argsOnly = true)
    private String modifyCommand(String command) {
        TypoModule typo = Modules.get().get(TypoModule.class);
        if(!typo.isActive() || !typo.commands.get()) return command;
        for(int i = 0; i < typo.keywords.get().size(); i++) {
            if (!command.toLowerCase().contains(typo.keywords.get().get(i).toLowerCase())) continue;
            if(typo.replacements.get().size() < i) break;
            command = command.replace(typo.keywords.get().get(i), typo.replacements.get().get(i));
        }
        return command;
    }
}
