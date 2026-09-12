package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.util.profiling.jfr.event.PacketReceivedEvent;
import spigey.asteroide.AsteroideAddon;

public class NoXaeroDisableModule extends Module {
    public NoXaeroDisableModule() { super(AsteroideAddon.CATEGORY, "Xaero-Bypass", "Attempts to bypass plugins disabling Features from Xaero's Minimap. Rejoin required"); }

    private SettingGroup sgGeneral = settings.getDefaultGroup();
    public Setting<Boolean> allowMinimap = sgGeneral.add(new BoolSetting.Builder()
        .name("Minimap")
        .description("Blocks packets to disable the minimap completely")
        .defaultValue(true)
        .build()
    );

    public Setting<String> minimapString = sgGeneral.add(new StringSetting.Builder()
        .name("Minimap String")
        .description("String to disable minimap completely")
        .defaultValue("§n§o§m§i§n§i§m§a§p")
        .visible(allowMinimap::get)
        .build()
    );

    public Setting<Boolean> allowCaveMode = sgGeneral.add(new BoolSetting.Builder()
        .name("Cave Mode & Radar")
        .description("Blocks packets to disable Cave Mode and Entity Radar")
        .defaultValue(true)
        .build()
    );

    public Setting<String> caveModeString = sgGeneral.add(new StringSetting.Builder()
        .name("Cave Mode & Radar String")
        .description("String to disable Cave Mode and Entity Radar")
        .defaultValue("§f§a§i§r§x§a§e§r§o")
        .visible(allowCaveMode::get)
        .build()
    );
}
