package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import spigey.asteroide.AsteroideAddon;
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
        builder.then(literal("keep typing to send a message").executes(ctx -> SINGLE_SUCCESS));
    }
}
