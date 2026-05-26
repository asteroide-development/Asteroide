package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import net.minecraft.command.CommandSource;

public class SlotCommand extends Command {
    public SlotCommand() {
        super("slot", "Switches your equipped slot. Range 1 - 9");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ctx -> {
            info(String.format("Selected slot %d.", mc.player.getInventory().selectedSlot+1));
            info("Usage: §8.slot {slot}§7. Do note that the range is 1 - 9 instead of 0 - 8. Invalid slot will kick you!");
            return SINGLE_SUCCESS;
        });
        builder.then(argument("slot", IntegerArgumentType.integer()).executes(context -> {
            // YES; No restrictions
            mc.player.getInventory().setSelectedSlot(IntegerArgumentType.getInteger(context, "slot") - 1);
            return SINGLE_SUCCESS;
        }));
    }
}
