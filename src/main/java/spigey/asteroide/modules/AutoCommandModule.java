package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.settings.Vector3dSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.compiler.Compiler;
import meteordevelopment.starscript.compiler.Parser;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3d;
import spigey.asteroide.AsteroideAddon;

import static spigey.asteroide.util.msg;
import meteordevelopment.starscript.utils.StarscriptError;

public class AutoCommandModule extends Module {
    public AutoCommandModule() { super(AsteroideAddon.CATEGORY, "Auto-Command", "Automatically runs a command when you are in a specific area."); }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Vector3d> start = sgGeneral.add(new Vector3dSetting.Builder()
        .name("Start Position  ")
        .description("Start position of the area.")
        .defaultValue(new Vector3d(-20, -60, -20))
        .noSlider()
        .build()
    );

    private final Setting<Vector3d> end = sgGeneral.add(new Vector3dSetting.Builder()
        .name("End Position  ")
        .description("End position of the area.")
        .defaultValue(new Vector3d(20, 100, 20))
        .noSlider()
        .build()
    );

    private final Setting<String> command = sgGeneral.add(new StringSetting.Builder()
        .name("Command")
        .description("The command to run.")
        .renderer(StarscriptTextBoxRenderer.class)
        .defaultValue("/home")
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new meteordevelopment.meteorclient.settings.IntSetting.Builder()
        .name("Delay")
        .description("Delay between command executions in ticks.")
        .defaultValue(20)
        .min(1)
        .sliderMin(1)
        .sliderMax(100)
        .build()
    );

    private int tick = delay.get();

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(tick > 0){ tick--; return; }
        if(mc.player == null || mc.world == null) return;
        Vec3d pos = mc.player.getPos();
        // Spaghetti below
        if ((int)Math.floor(pos.x) < Math.min(start.get().x, end.get().x) || (int)Math.floor(pos.x) > Math.max(start.get().x, end.get().x) || (int)Math.floor(pos.y) < Math.min(start.get().y, end.get().y) || (int)Math.floor(pos.y) > Math.max(start.get().y, end.get().y) || (int)Math.floor(pos.z) < Math.min(start.get().z, end.get().z) || (int)Math.floor(pos.z) > Math.max(start.get().z, end.get().z)) return;
        String output = compile(command.get());
        if(output != null) msg(output);
        tick = delay.get();
    }

    private static String compile(String script) { // Partly from meteor rejects https://github.com/AntiCope/meteor-rejects/blob/master/src/main/java/anticope/rejects/modules/ChatBot.java
        if (script == null) return null;
        Parser.Result result = Parser.parse(script);
        if (result.hasErrors()) {
            MeteorStarscript.printChatError(result.errors.get(0));
            return null;
        }
        Script compiled = Compiler.compile(result);
        if(compiled == null){ return null; }
        try { return MeteorStarscript.ss.run(compiled).text; }
        catch(StarscriptError e){ MeteorStarscript.printChatError(e); return null; }
    }
}


