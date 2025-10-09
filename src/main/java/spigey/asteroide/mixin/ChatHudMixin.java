package spigey.asteroide.mixin;

import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import spigey.asteroide.modules.ClickEventsModule;

@Mixin(ChatHud.class)
public class ChatHudMixin {
    @ModifyVariable( method = "addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;Lnet/minecraft/client/gui/hud/MessageIndicator;)V", at = @At("HEAD"), index = 1, argsOnly = true )
    private Text commandInspect(Text value) { try{
        ClickEventsModule ce = Modules.get().get(ClickEventsModule.class);
        if(!ce.isActive()) return value;

        MutableText copied = value.copy();
        if (value.getStyle().getClickEvent() != null) {
            HoverEvent old = value.getStyle().getHoverEvent();
            MutableText tooltip = Text.empty();
            if(old != null && !old.getValue(HoverEvent.Action.SHOW_TEXT).getString().contains(value.getStyle().getClickEvent().getValue())) if (old.getValue(HoverEvent.Action.SHOW_TEXT) instanceof MutableText t && ce.showCommand.get()) tooltip.append(t.copy()).append("\n\n");
            if (ce.showCommand.get()) tooltip.append("§7" + value.getStyle().getClickEvent().getValue());
            copied.setStyle(value.getStyle().withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltip)));
            if(ce.customColorEnabled.get()) copied.setStyle(copied.getStyle().withColor(ce.customColor.get().toTextColor()));
            if(ce.customStyleEnabled.get()) copied.setStyle(copied.getStyle().withFormatting(switch(ce.customStyle.get()){
                case Bold -> Formatting.BOLD;
                case Italic -> Formatting.ITALIC;
                case Underline -> Formatting.UNDERLINE;
                case Strike -> Formatting.STRIKETHROUGH;
                case Obfuscated -> Formatting.OBFUSCATED;
                default -> Formatting.RED; // idfk
            }));
        }
        for (int i = 0; i < value.getSiblings().size(); i++) copied.getSiblings().set(i, commandInspect(value.getSiblings().get(i)));
        return copied;
    }catch(Exception L){ return value; } }

    @Inject(method = "mouseClicked(DD)Z", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) { try{
        ClickEventsModule ce = Modules.get().get(ClickEventsModule.class);
        if(!ce.isActive() || !ce.blockCommands.get()) return;
        Style style = ((ChatHud) (Object) this).getTextStyleAt(mouseX, mouseY);
        if (style == null) return;
        ClickEvent click = style.getClickEvent();
        if (click == null) return;
        if (click.getAction() != ClickEvent.Action.RUN_COMMAND) return;


        for(String command : ce.commands.get()) if(click.getValue().toLowerCase().contains(command.toLowerCase())){
            cir.setReturnValue(true);
            ce.info(Text.of(String.format("Command execution was blocked! §7%s", click.getValue().toLowerCase().replaceAll(command.toLowerCase(), "§c" + command.toUpperCase() + "§7"))));
            return;
        }
    }catch(Exception L){/* home botnet server */}}
}
