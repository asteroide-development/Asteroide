package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DistributeModule extends Module {
    public DistributeModule() {
        super(AsteroideAddon.CATEGORY, "distribute", "Automatically distributes duped items.");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<List<String>> command = sgGeneral.add(new StringListSetting.Builder()
            .name("random command")
            .description("Chooses a random command from the list, replacing {name} with the username.")
            .defaultValue("/gift {name}")
            .build()
    );
    private final Setting<List<String>> users = sgGeneral.add(new StringListSetting.Builder()
        .name("excluded users")
        .description("Will not distribute to these users.")
        .build()
    );
    private final Setting<List<String>> ranks = sgGeneral.add(new StringListSetting.Builder()
        .name("excluded ranks")
        .description("Will not distribute to people with these ranks.")
        .defaultValue("owner", "admin", "developer", "helper", "mod", "staff")
        .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay in ticks")
        .description("How many ticks to wait before distributing to the next user.")
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
        if(display.isEmpty() || user.isEmpty()){
            for (PlayerListEntry player : mc.getNetworkHandler().getPlayerList()) display.add(player.getDisplayName() == null ? player.getProfile().getName() : player.getDisplayName().getString());
            for (PlayerListEntry player : mc.getNetworkHandler().getPlayerList()) user.add(player.getProfile().getName());
            if(display.isEmpty() || user.isEmpty()) return;
        }
        if(idx >= display.size()) idx = 0;
        String gift = display.get(idx).toLowerCase();
        boolean yes = false;
        while (!yes) { // do not touch this code
            yes = true;
            for (String s : ranks.get()) {if (gift.toLowerCase().contains(s.toLowerCase())) yes = false;}
            for (String s : users.get()) {if (user.get(idx).toLowerCase().contains(s.toLowerCase())) yes = false;}
            if(!yes) {if(idx >= display.size()){idx = 0;}else{idx++;} return;}
            if(display.isEmpty() || user.isEmpty()){ toggle(); return;}
        }
        ChatUtils.sendPlayerMsg(String.format("/%s", command.get().get(new Random().nextInt(command.get().size())).replace("{name}", user.get(idx)).replace("/", "")));
        idx++;
        tick = delay.get();
    }
}
