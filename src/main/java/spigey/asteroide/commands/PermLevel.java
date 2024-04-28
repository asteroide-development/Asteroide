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
            info(switch(getPermissionLevel()){
                case 0 -> "§cYou do not have any permission on this server.";
                case 1 -> "§6Your permission level on this server is 1.";
                case 2 -> "§aYou have some permissions on this server.";
                case 3 -> "§9Your permission level on this server is 3.";
                case 4 -> "§eYou are opped on this server!";
                default -> "§4" + getPermissionLevel() + "???";
            });
            return SINGLE_SUCCESS;
        });
    }
}
