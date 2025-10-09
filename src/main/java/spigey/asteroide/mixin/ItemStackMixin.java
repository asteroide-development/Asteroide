package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.item.tooltip.TooltipType;
import spigey.asteroide.modules.BetterAntiCrashModule;
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
        if(tooltip.contains("§c[Translation Blocked],§c[Translation Blocked]") || tooltip.contains("§c[Translation Blocked]§c[Translation Blocked]§c[Translation Blocked]") && bac.translationCrash.get()) cir.setReturnValue(Arrays.stream(new Text[]{cir.getReturnValue().get(0), Text.of("§c[Translation Blocked]")}).toList());
        else cir.setReturnValue(tooltip.length() > bac.ThresholdLength.get() ? Arrays.stream(new Text[]{cir.getReturnValue().get(0), Text.of(String.format("§c[Tooltip with length %s blocked]", bac.getMessage(tooltip)))}).toList() : cir.getReturnValue());
    }

    @Inject(method = "getName()Lnet/minecraft/text/Text;", at = @At("RETURN"), cancellable = true)
    private void name(CallbackInfoReturnable<Text> cir) {
        BetterAntiCrashModule bac = Modules.get().get(BetterAntiCrashModule.class);
        if(!bac.isActive() || !bac.items.get()) { cir.setReturnValue(cir.getReturnValue()); return; }
        String name = cir.getReturnValue().getString();
        if(name.contains("§c[Translation Blocked],§c[Translation Blocked]") || name.contains("§c[Translation Blocked]§c[Translation Blocked]§c[Translation Blocked]") && bac.translationCrash.get()) cir.setReturnValue(Text.of("§c[Translation Blocked]"));
        else cir.setReturnValue(name.length() > bac.ThresholdLength.get() ? Text.of(String.format("§c[Name with length %s blocked]", bac.getMessage(name))) : cir.getReturnValue());
    }
}
