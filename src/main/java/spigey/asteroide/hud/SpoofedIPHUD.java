package spigey.asteroide.hud;

import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import spigey.asteroide.AsteroideAddon;
import static spigey.asteroide.AsteroideAddon.spoofedIP;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class SpoofedIPHUD extends HudElement {
    public static final HudElementInfo<SpoofedIPHUD> INFO = new HudElementInfo<>(AsteroideAddon.HUD, "Spoofed IP", "Shows your IP that's spoofed from BungeeSpoof", SpoofedIPHUD::new);

    public SpoofedIPHUD() {
        super(INFO);
    }
    @Override
    public void render(HudRenderer renderer) {
        String username = mc.getSession().getUsername();
        setSize(renderer.textWidth("IP: " + spoofedIP, true), renderer.textHeight(true));
        renderer.text("IP: " + spoofedIP, x, y, Color.WHITE, true);
    }
}
