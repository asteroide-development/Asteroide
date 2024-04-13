package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;

import static java.lang.Thread.sleep;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.place;
import static spigey.asteroide.util.CommandBlock;
import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class thingycomnad extends Command {
    public thingycomnad() {
        super("fuckserver", "fucks the server usig command blocks");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            CommandBlock(Items.REPEATING_COMMAND_BLOCK, "/say hi", 1);
            // help I gotta make it wait for a few ticks somehow but how
            place(mc.player.getBlockPos().up(2));
            return SINGLE_SUCCESS;
        });
    }

    private void place(BlockPos blockPos) {
        assert mc.world != null;
        if(mc.world.getBlockState(blockPos).getBlock().asItem() != Items.REPEATING_COMMAND_BLOCK){
            BlockUtils.place(blockPos, InvUtils.findInHotbar(Items.REPEATING_COMMAND_BLOCK), 50, false);
        }
    }
}
