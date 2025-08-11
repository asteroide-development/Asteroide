package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.modules.RTCSettingsModule;

@Mixin(PlayerListHud.class)
public class PlayerListHudMixin {
    @Inject(method = "getPlayerName", at = @At("RETURN"), cancellable = true)
    private void modifyPlayerName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        final RTCSettingsModule rtc = Modules.get().get(RTCSettingsModule.class);
        if(!AsteroideAddon.users.contains(entry.getProfile().getName()) || !rtc.disableIcon.get()) return;
        cir.setReturnValue(Text.empty().append("\uE000 ").append(cir.getReturnValue()));
    }
}
