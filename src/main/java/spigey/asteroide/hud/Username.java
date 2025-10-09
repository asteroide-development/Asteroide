package spigey.asteroide.hud;

import meteordevelopment.meteorclient.systems.hud.Hud;
import spigey.asteroide.AsteroideAddon;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Username extends HudElement {
    public static final HudElementInfo<Username> INFO = new HudElementInfo<>(AsteroideAddon.HUD, "Username", "Shows your username, good for cracked servers", Username::new);

    public Username() {
        super(INFO);
    }
    @Override
    public void render(HudRenderer renderer) {
        String text = String.format("Username: %s", mc.getSession().getUsername());
        setSize(renderer.textWidth(text, true), renderer.textHeight(true));
        renderer.text(mc.getSession().getUsername(), renderer.text("Username: ", x, y, Hud.get().textColors.get().get(0), true), y, Hud.get().textColors.get().get(1), true);
    }
}
