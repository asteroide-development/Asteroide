package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.CommandSource;

public class ChannelCommand extends Command {
    public ChannelCommand() {
        super("channel", "Channel chat messages into commands.");
    }
    private String channel = "";

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            if(this.channel.isEmpty()){ info("Usage: §8.channel <command>.§7 Will run every chat message you send on that command."); return SINGLE_SUCCESS; }
            this.channel = "";
            info("Cleared channel");
            MeteorClient.EVENT_BUS.unsubscribe(this);
            return SINGLE_SUCCESS;
        });

        builder.then(argument("command", StringArgumentType.greedyString()).executes(ctx -> {
            String command = StringArgumentType.getString(ctx, "command");
            info(String.format("Set channel to §8%s", command));
            this.channel = command;
            MeteorClient.EVENT_BUS.subscribe(this);
            return SINGLE_SUCCESS;
        }));
    }

    @EventHandler
    private void onMessage(SendMessageEvent event){
        if(this.channel.isEmpty()) return;
        if(event.message.startsWith(this.channel)) return;
        event.cancel();
        ChatUtils.sendPlayerMsg(String.format("%s %s", this.channel, event.message));
    }
}
