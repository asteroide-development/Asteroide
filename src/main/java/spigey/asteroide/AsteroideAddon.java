package spigey.asteroide;

import spigey.asteroide.commands.*;
import spigey.asteroide.hud.*;
import spigey.asteroide.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class AsteroideAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Asteroide");
    public static final HudGroup HUD_GROUP = new HudGroup("Asteroide");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Asteoride");

        // Modules
        Modules.get().add(new AutoKys());
        Modules.get().add(new ServerCrashModule());
        // Modules.get().add(new WordFilterModule());


        // Commands
        Commands.add(new CrashAll());
        Commands.add(new CrashPlayer());
        Commands.add(new ServerCrash());
        Commands.add(new GetNbtItem());
        Commands.add(new PermLevel());

        // HUD
        Hud.get().register(Username.INFO);
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
