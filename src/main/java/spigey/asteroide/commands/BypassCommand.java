package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import spigey.asteroide.util;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.util.CommandBlock;

public class BypassCommand extends Command {
    public BypassCommand() {
        super("cmdblock", "Gives you a command block with the specified command inside");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            error("You have to specify a message!"); // .cmdblock /say Asteroide on Crack!
            return SINGLE_SUCCESS;
        });
        builder.then(argument("text", StringArgumentType.greedyString()).executes(context -> {
            String copy = "placeholder";
            ChatUtils.sendMsg(util.getCopyButton(copy));
            return SINGLE_SUCCESS;
        }));
    }
}
