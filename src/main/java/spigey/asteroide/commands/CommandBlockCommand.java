package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

public class CommandBlockCommand extends Command {
    public CommandBlockCommand() {
        super("cmdblock", "Gives you a command block with the specified command inside");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            error("You have to specify a command! f.e: " + Config.get().prefix.get() + "cmdblock /say Asteroide on Crack!"); // .cmdblock /say Asteroide on Crack!
            return SINGLE_SUCCESS;
        });
        builder.then(argument("command", StringArgumentType.greedyString()).executes(context -> {
            assert mc.player != null;
            if(!mc.player.getAbilities().creativeMode){error("You need to be in creative mode to use this command!"); return SINGLE_SUCCESS;}
            if(!mc.player.hasPermissionLevel(4)){error("You're missing the permission level '§f4§c', you can most likely not place the command block!");}
            String command = StringArgumentType.getString(context, "command");
            // ChatUtils.sendMsg(Text.of("§fReceiving command block with command '§7" + command.substring(0, Math.min(command.length(), 15)) + "§f'."));
            ChatUtils.sendMsg(Text.of("This command only works on Asteroide 1.20.4"));
            // CommandBlock(Items.COMMAND_BLOCK, command, 1, command.substring(0, Math.min(command.length(), 30)), true);
            return SINGLE_SUCCESS;
        }));
    }
}
