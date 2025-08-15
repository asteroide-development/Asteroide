package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Shadow;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.modules.EntityCancellerModule;
import spigey.asteroide.modules.RTCSettingsModule;

@Mixin(TranslatableTextContent.class)
public class TranslationMixin {
    @Shadow
    private String key;

    @Inject(method = "getArg", at = @At("HEAD"), cancellable = true)
    private void onGetArg(int index, CallbackInfoReturnable<Object> cir) {
        try{
            final EntityCancellerModule ecm = Modules.get().get(EntityCancellerModule.class);
            if(!ecm.isActive()) return;
            for(String keyy : ecm.translations.get()) if(this.key.contains(keyy)) { cir.setReturnValue(Text.literal("§c[Translation Blocked]")); break; }
        }catch(Exception e){ /* dear fuck */ }
    }
}

