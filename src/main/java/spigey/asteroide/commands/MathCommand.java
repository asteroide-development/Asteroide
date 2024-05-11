package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import spigey.asteroide.util;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class MathCommand extends Command {
    public MathCommand() {
        super("math", "Solves math equations for you");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            error("You have to specify an equation!");
            return SINGLE_SUCCESS;
        });
        builder.then(argument("equation", StringArgumentType.greedyString()).executes(context -> {
            String farquaad = StringArgumentType.getString(context, "equation");
            ChatUtils.sendMsg(Text.literal(String.valueOf(util.meth(farquaad))));
            return SINGLE_SUCCESS;
        }));
    }
}
