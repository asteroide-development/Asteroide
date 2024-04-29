package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class MeCommand extends Command {
    public MeCommand() {
        super("me", "Basic information for devs");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("§f------ Info ------");
            info("§fUsername: §7" + mc.getSession().getUsername());
            info("§fUUID: §7" + mc.getSession().getUuidOrNull());
            info("§fAccountType: §7" + mc.getSession().getAccountType());
            info("§fServer: §7" + mc.getServer());
            return SINGLE_SUCCESS;
        });
    }
}
