package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

public class CloseCommand extends Command {
    public CloseCommand() {
        super("close", "Closes the currently open GUI.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            try{mc.player.closeScreen();}catch(Exception L){/**/}
            return SINGLE_SUCCESS;
        });
    }
}
