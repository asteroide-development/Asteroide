package spigey.asteroide.hud;

import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import spigey.asteroide.AsteroideAddon;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.AsteroideAddon.MinehutIP;
import static spigey.asteroide.AsteroideAddon.spoofedIP;

public class MinehutIPHud extends HudElement {
    public static final HudElementInfo<MinehutIPHud> INFO = new HudElementInfo<>(AsteroideAddon.HUD, "Minehut IP", "The IP of the minehut server you're currently playing on", MinehutIPHud::new);

    public MinehutIPHud() {
        super(INFO);
    }
    @Override
    public void render(HudRenderer renderer) {
        String text = String.format("Minehut IP: %s", MinehutIP);
        setSize(renderer.textWidth(text, true), renderer.textHeight(true));
        renderer.text(MinehutIP, renderer.text("Minehut IP: ", x, y, Hud.get().textColors.get().get(0), true), y, Hud.get().textColors.get().get(1), true);
    }
}
