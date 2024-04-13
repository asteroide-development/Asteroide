package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class thiswillfucktheserveritspartofanothercommand extends Command {
    public thiswillfucktheserveritspartofanothercommand(){
        super("thiswillfucktheserveritspartofanothercommand", "this is part of another command, don't use it.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        assert mc.world != null;
        assert mc.player != null;
        if(mc.world.getBlockState(mc.player.getBlockPos()).getBlock().asItem() != Items.REPEATING_COMMAND_BLOCK){
            BlockUtils.place(mc.player.getBlockPos(), InvUtils.findInHotbar(Items.REPEATING_COMMAND_BLOCK), 50, false);
        }
    }
}
