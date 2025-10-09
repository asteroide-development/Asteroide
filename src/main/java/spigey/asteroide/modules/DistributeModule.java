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

public class DistributeModule extends Module {
    public DistributeModule() {
        super(AsteroideAddon.CATEGORY, "Distribute", "Automatically distributes duped items.");
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
    private List<PlayerListEntry> players = new ArrayList<>();

    @Override
    public void onActivate() {
        if(mc.getCurrentServerEntry() == null) return;
        this.tick = 0;
        this.idx = -1;
        this.players.addAll(mc.getNetworkHandler().getPlayerList());
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(mc.getCurrentServerEntry() == null) return;
        if(tick > 0){tick--; return;}
        if(tick < 0) return;
        if(this.players.isEmpty()) return;
        idx++;
        if(idx >= this.players.size()) idx = 0;
        PlayerListEntry player = this.players.get(idx);
        for (String s : ranks.get()) if (displayName(player).toLowerCase().contains(s.toLowerCase())) return;
        for (String s : users.get()) if (player.getProfile().getName().toLowerCase().contains(s.toLowerCase()) || player.getProfile().getName().contains("§")) return;
        ChatUtils.sendPlayerMsg(String.format("/%s", command.get().get(new Random().nextInt(command.get().size())).replace("{name}", player.getProfile().getName()).replace("/", "")));
        idx++;
        tick = delay.get();
    }

    private String displayName(PlayerListEntry player){ return player.getDisplayName() == null ? player.getProfile().getName() : player.getDisplayName().getString(); }
}
