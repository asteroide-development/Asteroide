package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import static spigey.asteroide.util.getPermissionLevel;
import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class PermLevel extends Command {
    public PermLevel() {
        super("perm", "Tells you your permission level on the current server");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Your permission level on this server is " + getPermissionLevel() + ".");
            return SINGLE_SUCCESS;
        });
    }
}
