package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.screen.ingame.AbstractCommandBlockScreen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spigey.asteroide.modules.BetterAntiCrashModule;

@Mixin(CommandBlockScreen.class)
public class CommandBlockScreenMixin {
    @Inject(method = "updateCommandBlock", at = @At("TAIL"), cancellable = true)
    private void init(CallbackInfo ci) {
        BetterAntiCrashModule bac = Modules.get().get(BetterAntiCrashModule.class);
        TextFieldWidget command = ((CommandAccessor) this).getCommand();
        if(!bac.isActive() || !bac.commandBlockCrash.get()) return;
        command.setText(command.getText().substring(0, Math.min(bac.commandBlockThreshold.get(), command.getText().length())));
        ci.cancel();
    }
}

