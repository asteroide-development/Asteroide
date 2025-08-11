package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.hit.BlockHitResult;
import org.lwjgl.glfw.GLFW;
import spigey.asteroide.AsteroideAddon;

public class ClientDeleteModule extends Module {
    public ClientDeleteModule() {
        super(AsteroideAddon.CATEGORY, "Client-Delete", "Deletes blocks on the client side");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> customInput = sgGeneral.add(new BoolSetting.Builder()
        .name("custom-keybind")
        .description("Use a number value as custom keybind.")
        .defaultValue(false)
        .build()
    );

    public Setting<String> keybind = sgGeneral.add(new StringSetting.Builder()
        .name("custom-keybind")
        .description("Custom keybind number")
        .defaultValue("1")
        .filter((text, c) -> (text + c).matches("^\\d+$"))
        .visible(customInput::get)
        .build()
    );

    private final Setting<Keybind> selectionBind = sgGeneral.add(new KeybindSetting.Builder()
        .name("selection bind")
        .description("Bind to draw selection.")
        .defaultValue(Keybind.fromButton(GLFW.GLFW_MOUSE_BUTTON_RIGHT))
        .visible(() -> !customInput.get())
        .onChanged((newValue) -> keybind.set(String.valueOf(newValue.getValue())))
        .build()
    );

    private final Setting<Boolean> keepActive = sgGeneral.add(new BoolSetting.Builder()
        .name("keep-active")
        .description("Keep the module active after deleting a block.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> allowHolding = sgGeneral.add(new BoolSetting.Builder()
        .name("allow-holding")
        .description("Allow holding the selection bind.")
        .defaultValue(false)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgGeneral.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color.")
        .defaultValue(new SettingColor(255, 255, 255, 50))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color.")
        .defaultValue(new SettingColor(255, 255, 255, 255))
        .build()
    );

    @EventHandler
    private void onKey(MouseButtonEvent event){
        int bind = customInput.get() ? Integer.parseInt(keybind.get()) : selectionBind.get().getValue();
        if(!(event.action == KeyAction.Press || (allowHolding.get() && event.action == KeyAction.Repeat)) || mc.currentScreen != null || event.button != bind) return;
        if (!(mc.crosshairTarget instanceof BlockHitResult result)) return;
        mc.world.setBlockState(result.getBlockPos(), Blocks.AIR.getDefaultState());
        if(!keepActive.get()) toggle();
        event.cancel();
    }

    @EventHandler
    private void onKey(KeyEvent event){
        int bind = customInput.get() ? Integer.parseInt(keybind.get()) : selectionBind.get().getValue();
        if(!(event.action == KeyAction.Press || (allowHolding.get() && event.action == KeyAction.Repeat)) || mc.currentScreen != null || event.key != bind) return;
        if (!(mc.crosshairTarget instanceof BlockHitResult result)) return;
        mc.world.setBlockState(result.getBlockPos(), Blocks.AIR.getDefaultState());
        if(!keepActive.get()) toggle();
        event.cancel();
    }

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (!(mc.crosshairTarget instanceof BlockHitResult result)) return;
        event.renderer.box(result.getBlockPos(), sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}


