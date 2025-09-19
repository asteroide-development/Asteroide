package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.util.PlayerDir;

public class PhaseCommand extends Command {
    public PhaseCommand() {
        super("phase", "Vclip, but sideways");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("blocks", DoubleArgumentType.doubleArg()).executes(context -> {

            double blocks = context.getArgument("blocks", Double.class);

            // Implementation of "PaperClip" aka "TPX" aka "VaultClip" into vclip
            // Allows you to teleport up to 200 blocks in one go (as you can send 20 move packets per tick)
            // Paper allows you to teleport 10 blocks for each move packet you send in that tick
            // Video explanation by LiveOverflow: https://www.youtube.com/watch?v=3HSnDsfkJT8
            int packetsRequired = (int) Math.ceil(Math.abs(blocks / 10));

            if (packetsRequired > 20) {
                // Wouldn't work on paper anyway.
                // Some servers don't have a vertical limit, so if it is more than 200 blocks, just use a "normal" tp
                // This makes it, so you don't get kicked for sending too many packets
                packetsRequired = 1;
            }

            if (mc.player.hasVehicle()) return SINGLE_SUCCESS;
            // No vehicle version
            // For each 10 blocks, send a player move packet with no delta
            for (int packetNumber = 0; packetNumber < (packetsRequired - 1); packetNumber++) {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.OnGroundOnly(true, mc.player.horizontalCollision));
            }
            // Now send the final player move packet
            String owo = PlayerDir(mc.player.getYaw());
            double x = mc.player.getX();
            double y = mc.player.getY();
            double z = mc.player.getZ();
            if(owo.equals("east")) x += blocks;
            if(owo.equals("south")) z += blocks;
            if(owo.equals("west")) x -= blocks;
            if(owo.equals("north")) z -= blocks;
            mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(x, y, z, true, mc.player.horizontalCollision));
            mc.player.setPosition(x, y, z);

            return SINGLE_SUCCESS;
        }));
    }
}
