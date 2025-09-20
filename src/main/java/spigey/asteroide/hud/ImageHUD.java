package spigey.asteroide.hud;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.gui.utils.StarscriptTextBoxRenderer;
import meteordevelopment.meteorclient.renderer.Renderer2D;
import meteordevelopment.meteorclient.renderer.GL;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;

import meteordevelopment.starscript.Script;
import meteordevelopment.starscript.compiler.Compiler;
import meteordevelopment.starscript.compiler.Parser;
import meteordevelopment.starscript.utils.StarscriptError;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import spigey.asteroide.AsteroideAddon;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ImageHUD extends HudElement {

    // All skidded from https://github.com/AntiCope/meteor-e621-integration/blob/master/src/main/java/anticope/esixtwoone/ImageHUD.java

    public static final HudElementInfo<ImageHUD> INFO = new HudElementInfo<>(AsteroideAddon.HUD, "Image", "Displays an image from the provided URL", ImageHUD::create);

    private boolean locked = false;
    private boolean empty = true;
    private int ticks = 0;

    private static final Identifier TEXID = Identifier.of("asteroide", "tex");
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> dynamicSize = sgGeneral.add(new BoolSetting.Builder()
        .name("Auto-adjust size")
        .description("Whether to dynamically set the size of the image to its actual size.")
        .defaultValue(true)
        .onChanged((val) -> updateSize())
        .build()
    );
    private final Setting<Double> imgX = sgGeneral.add(new DoubleSetting.Builder()
        .name("Size Multiplier")
        .description("Image size multiplier.")
        .defaultValue(1)
        .sliderMax(3)
        .sliderMin(0.1)
        .visible(dynamicSize::get)
        .onChanged((v) -> updateSize())
        .build()
    );

    private final Setting<Double> imgWidth = sgGeneral.add(new DoubleSetting.Builder()
        .name("width")
        .description("The scale of the image.")
        .defaultValue(100)
        .min(10)
        .sliderRange(70, 1000)
        .onChanged(o -> updateSize())
        .visible(() -> !dynamicSize.get())
        .build()
    );

    private final Setting<Double> imgHeight = sgGeneral.add(new DoubleSetting.Builder()
        .name("height")
        .description("The scale of the image.")
        .defaultValue(100)
        .min(10)
        .sliderRange(70, 1000)
        .onChanged(o -> updateSize())
        .visible(() -> !dynamicSize.get())
        .build()
    );

    private final Setting<String> url = sgGeneral.add(new StringSetting.Builder()
        .name("url")
        .description("Image URL.")
        .defaultValue("https://raw.githubusercontent.com/asteroide-development/Asteroide/refs/heads/master/icon.png")
        .renderer(StarscriptTextBoxRenderer.class)
        .onChanged((v) -> loadImage())
        .build()
    );

    private final Setting<Integer> refreshRate = sgGeneral.add(new IntSetting.Builder()
        .name("refresh-rate")
        .description("How often to refresh (ticks).")
        .defaultValue(6000)
        .sliderMax(3000)
        .sliderMin(20)
        .build()
    );

    public ImageHUD() {
        super(INFO);
        loadImage();
        MeteorClient.EVENT_BUS.subscribe(this);
    }

    @Override
    public void remove() {
        super.remove();
        MeteorClient.EVENT_BUS.unsubscribe(this);
    }

    private static ImageHUD create() { return new ImageHUD(); }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        ticks++;
        if (ticks >= refreshRate.get()) { ticks = 0; loadImage(); }
    }

    private NativeImage image = null;

    private double[] getSize(){ try{
        if(dynamicSize.get() && this.image != null) return new double[]{ this.image.getWidth() * imgX.get(), this.image.getHeight() * imgX.get() };
        else return new double[]{ imgWidth.get(), imgHeight.get() };
    }catch(Exception e){ return new double[]{100, 100}; } }

    @Override
    public void render(HudRenderer renderer) {
        if (empty) { loadImage(); return; }
        GL.bindTexture(TEXID);
        Renderer2D.TEXTURE.begin();
        double[] size = getSize();
        Renderer2D.TEXTURE.texQuad(x, y, size[0], size[1], Color.WHITE);
        Renderer2D.TEXTURE.render(null);
    }

    private void updateSize() {
        double[] iFuckingHateJava = getSize();
        setSize(iFuckingHateJava[0], iFuckingHateJava[1]);
    }

    private void loadImage() {
        if (locked || !isActive()) return;
        new Thread(() -> {
            try {
                locked = true;
                if (url.get() == null) { locked = false; return; }
                String compiled = compile(url.get()).isEmpty() ? url.get() : compile(url.get());
                var tempImage = NativeImage.read(Http.get(compiled).sendInputStream());
                mc.getTextureManager().registerTexture(TEXID, new NativeImageBackedTexture(tempImage));
                this.image = tempImage;
                empty = false;
            } catch (Exception ex) { mc.player.sendMessage(Text.of(String.format("§8[§cAsteroide§8] §cCould not load image from URL §7%s§c! %s", url.get(), ex)), false); }
            locked = false;
        }).start();
        updateSize();
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
