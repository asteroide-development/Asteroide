package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

public class ClickEventsModule extends Module {
    public ClickEventsModule() { super(AsteroideAddon.CATEGORY, "Click-Events", "ClickEvent Utils."); }
    private final SettingGroup sgCommands = settings.createGroup("Commands", true);
    private final SettingGroup sgStyle = settings.createGroup("Style", true);

    public final Setting<Boolean> showCommand = sgCommands.add(new BoolSetting.Builder()
        .name("Show commands")
        .description("Shows commands of click events as tooltip.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Boolean> blockCommands = sgCommands.add(new BoolSetting.Builder()
        .name("Block commands")
        .description("Blocks potentially harmful commands.")
        .defaultValue(true)
        .build()
    );

    public final Setting<List<String>> commands = sgCommands.add(new StringListSetting.Builder()
        .name("Commands")
        .description("Commands containing these phrases will be blocked.")
        .defaultValue("op", "gamemode", "lp", "*")
        .visible(blockCommands::get)
        .build()
    );

    public final Setting<Boolean> customColorEnabled = sgStyle.add(new BoolSetting.Builder()
        .name("Custom Color")
        .description("Adds a custom color to click events.")
        .defaultValue(true)
        .build()
    );

    public final Setting<SettingColor> customColor = sgStyle.add(new ColorSetting.Builder()
        .name("Color")
        .description("Custom color for click events.")
        .defaultValue(new SettingColor(36, 209, 240))
        .visible(customColorEnabled::get)
        .build()
    );

    public final Setting<Boolean> customStyleEnabled = sgStyle.add(new BoolSetting.Builder()
        .name("Custom Style")
        .description("Adds a custom style to click events.")
        .defaultValue(true)
        .build()
    );

    public final Setting<Style> customStyle = sgStyle.add(new EnumSetting.Builder<Style>()
        .name("Custom Style")
        .description("Custom style for click events.")
        .defaultValue(Style.Underline)
        .visible(customStyleEnabled::get)
        .build()
    );

    public enum Style {
        Bold,
        Italic,
        Underline,
        Strike,
        Obfuscated
    }
}
