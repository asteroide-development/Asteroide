package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.util.Hand;

import java.util.Objects;

public class CopyCommand extends Command {
    public CopyCommand() {
        super("copyitem", "Copy someone's items.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("player", PlayerListEntryArgumentType.create())
            .then(literal("MAIN_HAND").executes(context -> {
                execute(PlayerListEntryArgumentType.get(context), Hand.MAIN_HAND);
                return SINGLE_SUCCESS;
            }))
            .then(literal("OFF_HAND").executes(context -> {
                execute(PlayerListEntryArgumentType.get(context), Hand.OFF_HAND);
                return SINGLE_SUCCESS;
            }))
            .executes(context -> {
                execute(PlayerListEntryArgumentType.get(context), Hand.MAIN_HAND);
                return SINGLE_SUCCESS;
            })
        );
    }

    private void execute(PlayerListEntry player, Hand hand){
        for(Entity entity : mc.world.getEntities()){
            if(entity instanceof PlayerEntity plr && Objects.equals(entity.getName().getString(), player.getProfile().getName())){ // There definitely is a better way.
                Hand ph = getHand();
                mc.player.setStackInHand(ph, plr.getStackInHand(hand).copy());
                mc.getNetworkHandler().sendPacket(new CreativeInventoryActionC2SPacket(ph == Hand.OFF_HAND ? 45 : 36 + mc.player.getInventory().selectedSlot, plr.getStackInHand(hand).copy()));
                info(String.format("Copied §f%s§7 from §f%s§7.", plr.getStackInHand(hand).getItem().getName().getString(), plr.getName().getString()));
                return;
            }
        }
    }

    private Hand getHand(){
        if(mc.player.getMainHandStack().isEmpty()) return Hand.MAIN_HAND;
        else if(mc.player.getOffHandStack().isEmpty()) return Hand.OFF_HAND;
        else return Hand.MAIN_HAND;
    }
}
