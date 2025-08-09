package spigey.asteroide.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;

public class UUIDCommand extends Command {
    public UUIDCommand() {
        super("uuid", "Shows you a players UUID.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            info("Your UUID is " + mc.player.getUuid().toString());

            return SINGLE_SUCCESS;
        });

        builder.then(argument("player", PlayerListEntryArgumentType.create()).executes(context -> {
            GameProfile player = PlayerListEntryArgumentType.get(context).getProfile();

            if (player != null) info(player.getName() + "'s UUID is " + player.getId().toString());
            return SINGLE_SUCCESS;
        }));
    }
}
