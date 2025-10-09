package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.CommandSource;

public class DelayCommand extends Command {
    public DelayCommand() {
        super("delay", "Sends the provided message after a specific delay.");
    }

    private int tick = -1;
    private String message = "";

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("delay", IntegerArgumentType.integer(0)).then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            this.tick = IntegerArgumentType.getInteger(context, "delay");
            this.message = StringArgumentType.getString(context, "message");
            MeteorClient.EVENT_BUS.subscribe(this);
            info(String.format("Command will be sent in %.1f seconds.", this.tick / 20.0));
            return SINGLE_SUCCESS;
        })));
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(this.tick == -1 || this.message.isEmpty()){ MeteorClient.EVENT_BUS.unsubscribe(this); return; }
        if(this.tick > 0){ this.tick--; return; }
        ChatUtils.sendPlayerMsg(this.message);
        this.tick = -1;
        MeteorClient.EVENT_BUS.unsubscribe(this);
    }
}
