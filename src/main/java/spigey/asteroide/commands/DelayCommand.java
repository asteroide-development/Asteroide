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
import net.minecraft.text.Text;
import spigey.asteroide.utils.RandUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DelayCommand extends Command {
    public DelayCommand() {
        super("delay", "Sends the provided message after a specific delay.");
    }

    private Map<String, Map<Integer, String>> delays = new HashMap<>();
    private boolean isSubscribed = false;

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("delay", IntegerArgumentType.integer(0)).then(argument("message", StringArgumentType.greedyString()).executes(context -> {
            int tick = IntegerArgumentType.getInteger(context, "delay");
            Map<Integer, String> inner = new HashMap<>();
            inner.put(tick, StringArgumentType.getString(context, "message"));
            this.delays.put(RandUtils.string(5), inner);
            if(!this.isSubscribed) { MeteorClient.EVENT_BUS.subscribe(this); this.isSubscribed = true; }
            info(String.format("Command will be sent in %.1f seconds.", tick / 20.0));
            return SINGLE_SUCCESS;
        })));
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        List<String> removals = new ArrayList<>();
        for (Map.Entry<String, Map<Integer, String>> entryWrapper : this.delays.entrySet()) {
            int tick = 0; String message = "";
            for(Map.Entry<Integer, String> e : entryWrapper.getValue().entrySet()) { tick = e.getKey(); message = e.getValue(); } // I hate this so much
            if(tick == -1 || message.isEmpty()) continue;
            if(tick > 0){
                Map<Integer, String> entry = entryWrapper.getValue();
                entry.put(tick - 1, message);
                entry.remove(tick);
                continue;
            }
            try{ ChatUtils.sendPlayerMsg(message); }
            catch(Exception e){ /* */ }
            removals.add(entryWrapper.getKey());
        }

        removals.forEach(this.delays::remove);
        if(this.delays.isEmpty()){ MeteorClient.EVENT_BUS.unsubscribe(this); this.isSubscribed = false; }
    }
}
