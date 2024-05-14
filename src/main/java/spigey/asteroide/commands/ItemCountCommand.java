package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import spigey.asteroide.util;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.util.PlayerDir;

public class ItemCountCommand extends Command {
    public ItemCountCommand() {
        super("dev", "placholder");
    }

    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            assert mc.player != null;
            BlockPos pos = util.raycast(6);
            assert mc.world != null;
            ChatUtils.sendMsg(Text.of("Block at position " + pos + ": " + mc.world.getBlockState(pos)));
            return SINGLE_SUCCESS;
        });
    }
}
