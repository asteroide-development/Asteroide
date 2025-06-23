package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import spigey.asteroide.util;
// import static spigey.asteroide.util.CommandBlock; Why was I importing this?? // Ah, because this is copied from the CommandBlock Command

public class BCommand extends Command {
    public BCommand() {
        super("b", "Lets you bypass most chat filters");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            error("You have to specify a message!");
            return SINGLE_SUCCESS;
        });
        builder.then(argument("text", StringArgumentType.greedyString()).executes(context -> {
            String copy = StringArgumentType.getString(context, "text")
                .replaceAll("a", "а")
                .replaceAll("c", "с")
                .replaceAll("e", "е")
                .replaceAll("h", "һ")
                .replaceAll("i", "і")
                .replaceAll("j", "ј")
                .replaceAll("n", "ո")
                .replaceAll("o", "о")
                .replaceAll("p", "р")
                .replaceAll("u", "ս")
                .replaceAll("v", "ν")
                .replaceAll("x", "х")
                .replaceAll("y", "у");
            util.msg(copy); // Very big change
            return SINGLE_SUCCESS;
        }));
    }
}
