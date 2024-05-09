package spigey.asteroide.hud;

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
        String username = mc.getSession().getUsername();
        setSize(renderer.textWidth("Minehut IP: " + MinehutIP, true), renderer.textHeight(true));
        renderer.text("Minehut IP: " + MinehutIP, x, y, Color.WHITE, true);
    }
}
