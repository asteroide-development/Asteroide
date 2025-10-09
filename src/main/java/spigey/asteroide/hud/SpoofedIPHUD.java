package spigey.asteroide.hud;

import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import spigey.asteroide.AsteroideAddon;
import static spigey.asteroide.AsteroideAddon.spoofedIP;

public class SpoofedIPHUD extends HudElement {
    public static final HudElementInfo<SpoofedIPHUD> INFO = new HudElementInfo<>(AsteroideAddon.HUD, "Spoofed IP", "Shows your IP that's spoofed from BungeeSpoof", SpoofedIPHUD::new);

    public SpoofedIPHUD() {
        super(INFO);
    }
    @Override
    public void render(HudRenderer renderer) {
        String text = String.format("IP: %s", spoofedIP);
        setSize(renderer.textWidth(text, true), renderer.textHeight(true));
        renderer.text(spoofedIP, renderer.text("IP: ", x, y, Hud.get().textColors.get().get(0), true), y, Hud.get().textColors.get().get(1), true);
    }
}
