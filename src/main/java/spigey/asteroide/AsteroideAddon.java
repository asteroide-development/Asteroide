package spigey.asteroide;

import net.minecraft.item.Items;
import spigey.asteroide.commands.*;
import spigey.asteroide.hud.*;
import spigey.asteroide.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

import static spigey.asteroide.util.*;

public class AsteroideAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Asteroide", Items.MAGMA_BLOCK.getDefaultStack());
    public static final HudGroup HUD = new HudGroup("Asteroide");
    @Override
    public void onInitialize() {
        LOG.info("\nLoaded Asteroide v0.1.4-dev\n");
        // Modules
        addModule(new AutoKys());
        addModule(new ServerCrashModule());
        // addModule(new AutoXd());
        addModule(new AutoChatGame());
        addModule(new DeathNotifier());
        addModule(new AntiAnnouncement());
        addModule(new AutoBack());
        addModule(new ChatBot());
        addModule(new AutoMacro());
        // addModule(new WordFilterModule());
        // addModule(new AutoEz());


        // Commands
        addCommand(new CrashAll());
        addCommand(new CrashPlayer());
        addCommand(new ServerCrash());
        addCommand(new GetNbtItem());
        addCommand(new PermLevel());
        addCommand(new FuckServerCommand());

        // HUD
        addHud(Username.INFO);
    }


    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "spigey.asteroide";
    }
}
