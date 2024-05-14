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
            HitResult hitResult = mc.player.raycast(20 * 16, 1.0f, false);

            // Check for entity collision
            if (hitResult.getType() == HitResult.Type.ENTITY) {
                Entity entity = ((EntityHitResult) hitResult).getEntity();
                System.out.println("Raycast hit entity: " + entity);
                return SINGLE_SUCCESS;
            }

            if (hitResult.getType() != HitResult.Type.BLOCK) return SINGLE_SUCCESS;

            assert mc.world != null;
            BlockPos pos = BlockPos.ofFloored(hitResult.getPos());
            switch (PlayerDir(mc.player.getYaw())) {
                case "north":
                    pos = pos.north();
                    info("-1 Offset on NORTH");
                    break;
                case "west":
                    pos = pos.west();
                    info("-1 Offset on WEST");
                    break;
                default:
                    // not empty
            }

            ChatUtils.sendMsg(Text.of("Block at position " + pos + ": " + mc.world.getBlockState(pos)));
            return SINGLE_SUCCESS;
        });
    }
}
