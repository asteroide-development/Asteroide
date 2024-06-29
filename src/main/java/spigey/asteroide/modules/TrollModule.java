package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import spigey.asteroide.AsteroideAddon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TrollModule extends Module {
    public TrollModule() {
        super(AsteroideAddon.CATEGORY, "troll", "Automatically msgs random people.");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
            .name("command")
            .description("Command to use.")
            .defaultValue("/msg")
            .build()
    );
    private final Setting<List<String>> quotes = sgGeneral.add(new StringListSetting.Builder()
        .name("additional quotes")
        .description("More quotes to use.")
        .defaultValue("kys")
        .build()
    );
    private final Setting<List<String>> users = sgGeneral.add(new StringListSetting.Builder()
        .name("excluded users")
        .description("Will not msg these users.")
        .build()
    );
    private final Setting<List<String>> ranks = sgGeneral.add(new StringListSetting.Builder()
        .name("excluded ranks")
        .description("Will not msg people with these ranks.")
        .defaultValue("owner", "admin", "developer", "helper", "mod", "staff")
        .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay in ticks")
        .description("How many ticks to wait before msging the next user.")
        .min(1)
        .sliderMax(100)
        .defaultValue(30)
        .build()
    );

    private int tick = 0;
    private int idx = 0;
    private List<String> display = new ArrayList<>();
    private List<String> user = new ArrayList<>();

    @Override
    public void onActivate() {
        tick = 1;
        idx = 0;
        user = new ArrayList<>();
        display = new ArrayList<>();
        for (PlayerListEntry player : mc.getNetworkHandler().getPlayerList()) display.add(player.getDisplayName() == null ? player.getProfile().getName() : player.getDisplayName().getString());
        for (PlayerListEntry player : mc.getNetworkHandler().getPlayerList()) user.add(player.getProfile().getName());
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(tick > 0){tick--; return;}
        if(tick < 0) return;
        user = new ArrayList<>();
        display = new ArrayList<>();
        for (PlayerListEntry player : mc.getNetworkHandler().getPlayerList()) display.add(player.getDisplayName() == null ? player.getProfile().getName() : player.getDisplayName().getString());
        for (PlayerListEntry player : mc.getNetworkHandler().getPlayerList()) user.add(player.getProfile().getName());
        if(display.isEmpty() || user.isEmpty()) return;
        if(idx >= display.size()) idx = 0;
        String gift = display.get(idx).toLowerCase();
        boolean yes = false;
        while (!yes) { // do not touch this code
            yes = true;
            for (String s : ranks.get()) {if (gift.toLowerCase().contains(s.toLowerCase())) yes = false;}
            for (String s : users.get()) {if (user.get(idx).toLowerCase().contains(s.toLowerCase())) yes = false;}
            if(user.get(idx).contains("§")) yes = false;
            if(!yes) {if(idx >= display.size()){idx = 0;}else{idx++;} return;}
            if(display.isEmpty()){ toggle(); return;}
        }
        List<String> troll = new ArrayList<>();
        troll.addAll(AsteroideAddon.trolls);
        troll.addAll(quotes.get());
        ChatUtils.sendPlayerMsg(String.format("/%s %s %s", command.get().replace("/", ""), user.get(idx), troll.get(new Random().nextInt(troll.size()))));
        idx++;
        tick = delay.get();
    }
}
