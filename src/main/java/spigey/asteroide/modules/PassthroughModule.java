package spigey.asteroide.modules;

import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

public class PassthroughModule extends Module {
    public PassthroughModule() {
        super(AsteroideAddon.CATEGORY, "Passthrough", "Allows you to interact through certain blocks.");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("blocks")
        .description("What blocks should have an increased hitbox size.")
        .defaultValue(List.of(
            Blocks.TALL_GRASS, Blocks.TALL_SEAGRASS, Blocks.SHORT_GRASS, Blocks.SEAGRASS, Blocks.COBWEB
        ))
        .build()
    );
    // BlockOutlineMixin.java
}
