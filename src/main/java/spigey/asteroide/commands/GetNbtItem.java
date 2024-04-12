package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;
import spigey.asteroide.nbt.CrashBeehive;
import spigey.asteroide.nbt.GrieferKit;


import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.util.give;

public class GetNbtItem extends Command {
    public GetNbtItem() {
        super("getitem", "Gives you an nbt item");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("CrashHive").executes(ctx ->{
            assert mc.player != null;
            if(mc.player.getAbilities().creativeMode) {
                give(CrashBeehive.item, CrashBeehive.nbt);
                info("Received Crash Beehive");
            }
            if(!mc.player.getAbilities().creativeMode) { error("You need to be in creative mode to use this command");}
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("GrieferKit").executes(ctx ->{
            assert mc.player != null;
            if(mc.player.getAbilities().creativeMode){
                give(GrieferKit.item, GrieferKit.nbt);
                info("Received Spigey's Griefer kit");
            }
            if(!mc.player.getAbilities().creativeMode) { error("You need to be in creative mode to use this command");}
            return SINGLE_SUCCESS;
        }));
    }
}
