package spigey.asteroide.hud;

import spigey.asteroide.AsteroideAddon;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Username extends HudElement {
    public static final HudElementInfo<Username> INFO = new HudElementInfo<>(AsteroideAddon.HUD, "Username", "Shows your username, good for cracked servers", Username::new);

    public Username() {
        super(INFO);
    }
    @Override
    public void render(HudRenderer renderer) {
        Color WHITE = new Color();
        String username = mc.getSession().getUsername();
        setSize(renderer.textWidth("Username: " + username, true), renderer.textHeight(true));
        renderer.text("Username: " + username, x, y, Color.WHITE, true);
    }
}
