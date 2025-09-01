package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.text.MeteorClickEvent;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.component.Component;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.modules.RTCSettingsModule;
import spigey.asteroide.utils.ws;

public class RTCCommand extends Command {
    public RTCCommand() {
        super("rtc", "Asteroide RTC");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            ws.sendChat(StringArgumentType.getString(context, "message").split(" "));
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("online").executes(ctx ->{
            info("§f§lOnline Users (" + AsteroideAddon.users.size() + "):");
            for(String user : AsteroideAddon.users) info(user);
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("hide").executes(ctx ->{
            RTCSettingsModule rtc = Modules.get().get(RTCSettingsModule.class);
            boolean willHide = !rtc.hideMessages.get();
            rtc.hideMessages.set(willHide);
            if(!rtc.isActive()) rtc.toggle();
            info("§fRTC messages are now " + (willHide ? "§c§lHIDDEN" : "§a§lSHOWN") + "§f. Type §7.rtc hide§f again to " + (willHide ? "show" : "hide") + "§f them.");
            AsteroideAddon.showRtc = !willHide;
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("discord").executes(ctx ->{
            mc.player.sendMessage(getButton(Text.literal("§8§l[§c§lAsteroide§8§l]§7 Join our Discord")), false);
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("keep typing to send a message").executes(ctx -> SINGLE_SUCCESS));
    }

    private static MutableText getButton(MutableText message){
        MutableText Button = Text.literal("§8[ §9HERE §8]");
        Button.setStyle(Button.getStyle()
            .withClickEvent(new MeteorClickEvent(
                ClickEvent.Action.OPEN_URL,
                "https://discord.gg/QFzE3UzdpQ"
            ))
            .withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Text.literal("§7discord.gg/QFzE3UzdpQ")
            ))
        );
        return message.append(" ").append(Button);
    }
}
