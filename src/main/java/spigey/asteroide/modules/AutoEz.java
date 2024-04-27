package spigey.asteroide.modules;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.events.PlayerDeathEvent;

import java.util.List;
import java.util.Random;

import static spigey.asteroide.util.banstuff;
import static spigey.asteroide.util.msg;

public class AutoEz extends Module {
    private boolean active = false;
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Randomly takes the message from the list and sends on each kill.").defaultValue("ez", "skill issue", "get ez'd", "get better you fucking wimp").build());
    public AutoEz() {
        super(AsteroideAddon.CATEGORY, "auto-ez", "Automatically sends a message when you kill someone");
    }

    @Override
    public void onActivate() {
        banstuff();
        if(active){return;}
        MeteorClient.EVENT_BUS.subscribe(this);
        active = true;
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent event){
        if(!isActive()){return;}
        if(!event.isTarget()){return;}
        PlayerEntity victim = event.getPlayer();
        msg(messages.get().get(randomNum(0,messages.get().size() - 1))
            .replace("{player}", victim.getGameProfile().getName())
        );
    }
    private static final Random random = new Random();
    public static int randomNum(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}
