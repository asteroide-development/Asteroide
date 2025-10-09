package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;
import spigey.asteroide.util;

public class MathCommand extends Command {
    public MathCommand() {
        super("math", "Solves math equations for you", "meth");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            ChatUtils.sendMsg(Text.of("§cYou have to specify an equation!"));
            return SINGLE_SUCCESS;
        });
        builder.then(argument("equation", StringArgumentType.greedyString()).executes(context -> {
            String farquaad = String.valueOf(StringArgumentType.getString(context, "equation"));
            try{farquaad = String.valueOf(util.meth(farquaad));} catch(Exception L){ChatUtils.sendMsg(Text.of("§c" + L)); return SINGLE_SUCCESS;}
            if(farquaad.endsWith(".0")) farquaad = farquaad.replace(".0", "");
            ChatUtils.sendMsg(Text.of("The solution is §e" + farquaad));
            ChatUtils.sendMsg(util.getSendButton(util.getCopyButton(farquaad), farquaad));
            return SINGLE_SUCCESS;
        }));
    }
}
