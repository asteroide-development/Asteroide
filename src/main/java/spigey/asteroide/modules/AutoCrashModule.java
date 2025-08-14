package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.PlayerListEntry;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

public class AutoCrashModule extends Module {
    public AutoCrashModule() { super(AsteroideAddon.CATEGORY, "Auto-Crash", "Automatically kicks/bans/crashes a player when they join the server"); }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<List<String>> players = sgGeneral.add(new StringListSetting.Builder().name("players").description("Players to automatically crash").defaultValue("Fire93").build());
    private final Setting<PlayerMode> PlayerModeSetting = sgGeneral.add(new EnumSetting.Builder<PlayerMode>()
        .name("Player Mode")
        .description("Blacklist or Whitelist mode for the players.")
        .defaultValue(PlayerMode.Whitelist)
        .build()
    );
    private final Setting<Mode> ModeSetting = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("Action")
        .description("Which action to use on the player.")
        .defaultValue(Mode.Crash)
        .build()
    );
    public Setting<String> killMessage = sgGeneral.add(new StringSetting.Builder()
        .name("Kill Command")
        .description("The command to use to kill the player.")
        .defaultValue("kill {name}")
        .visible(() -> { return ModeSetting.get() == Mode.Kill; })
        .build()
    );
    public Setting<String> kickMessage = sgGeneral.add(new StringSetting.Builder()
        .name("Kick Command")
        .description("The command to use to kick the player.")
        .defaultValue("kick {name} www.asteroide.cc")
        .visible(() -> { return ModeSetting.get() == Mode.Kick; })
        .build()
    );
    public Setting<String> crashMessage = sgGeneral.add(new StringSetting.Builder()
        .name("Crash Command")
        .description("The command to use to crash the player.")
        .defaultValue("execute at {name} run particle ash ~ ~ ~ 1 1 1 1 2147483647 force {name}")
        .visible(() -> { return ModeSetting.get() == Mode.Crash; })
        .build()
    );
    public Setting<String> banMessage = sgGeneral.add(new StringSetting.Builder()
        .name("Ban Command")
        .description("The command to use to ban the player.")
        .defaultValue("minecraft:ban {name} www.asteroide.cc")
        .visible(() -> { return ModeSetting.get() == Mode.Ban; })
        .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay between command runs.")
        .defaultValue(30)
        .min(0)
        .sliderMax(200)
        .build()
    );

    private int tick = delay.get();

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(!isActive()) return;
        if(this.tick > 0) { this.tick--; return; }
        for (PlayerListEntry player : mc.getNetworkHandler().getPlayerList()) {
            if (player.getProfile() == null || player.getProfile().getName() == null) continue;

            String name = player.getProfile().getName();
            //if (name.equals(mc.player.getName().getString())) continue;
            if (PlayerModeSetting.get() == PlayerMode.Whitelist && !players.get().contains(name)) continue;
            if (PlayerModeSetting.get() == PlayerMode.Blacklist && players.get().contains(name)) continue;

            switch (ModeSetting.get()) {
                case Kill -> mc.player.networkHandler.sendCommand(killMessage.get().replace("{name}", name).replace("/", ""));
                case Kick -> mc.player.networkHandler.sendCommand(kickMessage.get().replace("{name}", name).replace("/", ""));
                case Crash -> mc.player.networkHandler.sendCommand(crashMessage .get().replace("{name}", name).replace("/", ""));
                case Ban -> mc.player.networkHandler.sendCommand(banMessage.get().replace("{name}", name).replace("/", ""));
            }
        }
        this.tick = delay.get();
    }

    private enum Mode {
        Kill,
        Kick,
        Crash,
        Ban
    }

    private enum PlayerMode{
        Whitelist,
        Blacklist
    }
}
