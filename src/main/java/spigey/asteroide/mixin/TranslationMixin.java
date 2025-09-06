package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.Shadow;
import spigey.asteroide.modules.BetterAntiCrashModule;

@Mixin(TranslatableTextContent.class)
public class TranslationMixin {
    @Shadow
    private String key;

    @Inject(method = "getArg", at = @At("HEAD"), cancellable = true)
    private void onGetArg(int index, CallbackInfoReturnable<Object> cir) {
        try{
            final BetterAntiCrashModule ecm = Modules.get().get(BetterAntiCrashModule.class);
            if(!ecm.isActive() || !ecm.translationCrash.get()) return;
            if(this.key.matches("%[0-9]+\\$s")) cir.setReturnValue(Text.literal("§c[Translation Blocked]"));
            for(String keyy : ecm.translations.get()) if(this.key.contains(keyy)) { cir.setReturnValue(Text.literal("§c[Translation Blocked]")); break; }
        }catch(Exception e){ /* dear fuck */ }
    }
}

