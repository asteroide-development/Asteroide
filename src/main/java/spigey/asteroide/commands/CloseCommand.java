package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;

public class CloseCommand extends Command {
    public CloseCommand() {
        super("close", "Closes the currently open GUI.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            try{
                int syncId = mc.player.currentScreenHandler.syncId;
                mc.player.closeScreen();
                mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(syncId));
            }catch(Exception L){/**/}
            return SINGLE_SUCCESS;
        });
    }
}
