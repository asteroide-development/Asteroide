package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.item.tooltip.TooltipType;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.modules.BetterAntiCrashModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    @Inject(method = "getTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    private void modifyTooltip(net.minecraft.item.Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir) {
        BetterAntiCrashModule bac = Modules.get().get(BetterAntiCrashModule.class);
        if(!bac.isActive() || !bac.items.get()) { cir.setReturnValue(cir.getReturnValue()); return; }
        String tooltip = cir.getReturnValue().stream().skip(1).map(Text::getString).collect(Collectors.joining());
        cir.setReturnValue(tooltip.length() > bac.ThresholdLength.get() ? Arrays.stream(new Text[]{cir.getReturnValue().get(0), Text.of(String.format("§c[Tooltip with length >%d blocked]", bac.ThresholdLength.get()))}).toList() : cir.getReturnValue());
    }

    @Inject(method = "getName()Lnet/minecraft/text/Text;", at = @At("RETURN"), cancellable = true)
    private void name(CallbackInfoReturnable<Text> cir) {
        BetterAntiCrashModule bac = Modules.get().get(BetterAntiCrashModule.class);
        if(!bac.isActive() || !bac.items.get()) { cir.setReturnValue(cir.getReturnValue()); return; }
        cir.setReturnValue(cir.getReturnValue().getString().length() > bac.ThresholdLength.get() ? Text.of(String.format("§c[Name with length >%d blocked]", bac.ThresholdLength.get())) : cir.getReturnValue());
    }
}
