package spigey.asteroide.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.hud.BossBarHud;
import net.minecraft.client.gui.hud.ClientBossBar;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import spigey.asteroide.modules.BetterAntiCrashModule;

@Mixin(BossBarHud.class)
public class BossBarHudMixin {
    @ModifyExpressionValue(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/ClientBossBar;getName()Lnet/minecraft/text/Text;"))
    public Text modifyBossBarName(Text original, @Local ClientBossBar clientBossBar) {
        BetterAntiCrashModule bac = Modules.get().get(BetterAntiCrashModule.class);
        return bac.isActive() && bac.bossBarLimit.get() && bac.ThresholdLength.get() < original.getString().length() ? Text.of(String.format("§c[Bossbar with length %d blocked]", original.getString().length())) : original;
    }
}
