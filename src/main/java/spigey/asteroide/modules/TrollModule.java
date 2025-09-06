package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.utils.Regex;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class TrollModule extends Module {
    public TrollModule() {
        super(AsteroideAddon.CATEGORY, "Troll", "Automatically msgs random people.");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
            .name("message")
            .description("Command/Message to use.")
            .defaultValue("/msg {name} {troll}")
            .build()
    );
    private final Setting<List<String>> quotes = sgGeneral.add(new StringListSetting.Builder()
        .name("additional quotes")
        .description("More quotes to use.")
        .defaultValue("so sigma!!")
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

    private final Setting<Trolls> type = sgGeneral.add(new EnumSetting.Builder<Trolls>()
        .name("troll type")
        .description("Whether it should insult or annoy people.")
        .defaultValue(Trolls.Trolls)
        .build()
    );

    private final Setting<Boolean> friends = sgGeneral.add(new BoolSetting.Builder()
        .name("msg friends")
        .description("Also msgs friends when enabled.")
        .defaultValue(false)
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
        if(!friends.get() && Friends.get().isFriend(player)) return;
        List<String> troll = new ArrayList<>();
        if(type.get() == Trolls.Insults) { troll.addAll(AsteroideAddon.trolls); troll.addAll(quotes.get()); }
        if(type.get() == Trolls.Trolls) { troll.addAll(AsteroideAddon.notInsults); troll.addAll(quotes.get()); }

        try {
            Regex rgx = new Regex(Map.of(
                "NAME", player.getProfile().getName(),
                "SELF", mc.getGameProfile().getName(),
                "SERVER", mc.getCurrentServerEntry().address.split("\\.")[0],
                "FPS", mc.getCurrentFps()
            ));

            ChatUtils.sendPlayerMsg(command.get()
                .replaceAll("\\{name}", player.getProfile().getName())
                .replaceAll("\\{troll}", rgx.placeholder(troll.get(new Random().nextInt(troll.size()))))
            );
        }catch(Exception L){}

        tick = delay.get();
    }

    private String displayName(PlayerListEntry player){ return player.getDisplayName() == null ? player.getProfile().getName() : player.getDisplayName().getString(); }

    private enum Trolls{
        Insults,
        Trolls
    }
}
