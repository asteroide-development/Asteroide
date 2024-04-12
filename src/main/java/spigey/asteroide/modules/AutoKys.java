package spigey.asteroide.modules;

import spigey.asteroide.AsteroideAddon;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import static meteordevelopment.meteorclient.utils.player.ChatUtils.sendMsg;

public class AutoKys extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder().name("messages").description("Randomly takes the message from the list and sends on each death.").defaultValue("kys", "that's fucking luck").build());
    private boolean lock = false;
    private int i = 15;

    public AutoKys() {
        super(AsteroideAddon.CATEGORY, "auto-kys", "Sends a message when you die");
    }

    @EventHandler
    private void onOpenScreenEvent(OpenScreenEvent event) {
        if (!(event.screen instanceof DeathScreen)) return;
        lock = true;
        i = 15;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.currentScreen instanceof DeathScreen) return;
        if (lock) i--;
        if (lock && i <= 0) {
            String message = getMessage();
            assert mc.player != null;
            Entity attacker = getAttacker(mc.player);
            if (attacker instanceof PlayerEntity) {
                message = "GG! " + Objects.requireNonNull(((PlayerEntity) attacker).getDisplayName()).getString() + " got you.";
            }
            // ChatUtils.sendMsg(Text.of(Config.get().prefix + "say " + message));
            ChatUtils.sendPlayerMsg(String.valueOf(message));
            lock = false;
            i = 15;
            return;
        }
    }

    private String getMessage() {
        return messages.get().isEmpty() ? "kill yourself you fucking wimp" : messages.get().get(randomNum(0, messages.get().size() - 1));
    }

    private static final Random random = new Random();


    public static int randomNum(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private Entity getAttacker(PlayerEntity player) {
        DamageSource damageSource = player.getRecentDamageSource();
        if (damageSource != null && damageSource.getAttacker() != null) {
            return damageSource.getAttacker();
        }
        return null;
    }
}
