package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import spigey.asteroide.AsteroideAddon;

import java.util.Objects;

import static java.awt.Color.getColor;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class TrackerModule extends Module {
    private final double[] last = new double[3];
    private long lastTickTime = 0;
    private double partialTicks;

    public TrackerModule() {
        super(AsteroideAddon.CATEGORY, "tracker", "Tracks a player by always looking at it. Must be loaded!");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Double> interpolation = sgGeneral.add(new DoubleSetting.Builder()
        .name("interpolation")
        .description("Smoothing factor to prevent motion sickness")
        .defaultValue(0.2)
        .min(0)
        .max(100)
        .sliderMax(3)
        .build()
    );

    private final Setting<Boolean> render = sgGeneral.add(new BoolSetting.Builder()
        .name("render")
        .description("Renders a yellow box on the tracked player when enabled.")
        .defaultValue(true)
        .build()
    );

    @Override
    public void onActivate() {
        lastTickTime = System.currentTimeMillis();
        if(AsteroideAddon.trackedPlayer != null && mc.getNetworkHandler() != null &&
            mc.getNetworkHandler().getPlayerList().stream().noneMatch(player -> player.getProfile().getName().equals(AsteroideAddon.trackedPlayer))) {
            ChatUtils.sendMsg(Text.of("§7Tracker enabled, use " + Config.get().prefix.get() + "track <player> to start tracking someone. §cThe tracked Player must be loaded!"));
        }
    }

    @EventHandler
    public void onTick(TickEvent.Post event) {
        if(mc.player == null) return;
        long currentTickTime = System.currentTimeMillis();
        partialTicks = (currentTickTime - lastTickTime) / 50.0;
        lastTickTime = currentTickTime;
    }

    @EventHandler
    public void onRender(Render3DEvent event) {
        if (AsteroideAddon.trackedPlayer == null || mc.player == null) return;
        PlayerEntity entity = null;
        for (AbstractClientPlayerEntity player : mc.world.getPlayers()) {
            if (player.getGameProfile().getName().equals(AsteroideAddon.trackedPlayer)) {
                entity = player;
                break;
            }
        }
        if (entity == null) {
            mc.inGameHud.setOverlayMessage(Text.of("§cPlayer not found, pointing to last known location"), false);
            mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(AsteroideAddon.lastPos[0], AsteroideAddon.lastPos[1], AsteroideAddon.lastPos[2]));
            return;
        }

        AsteroideAddon.lastPos = new double[]{entity.getX(), entity.getY(), entity.getZ()};
        mc.inGameHud.setOverlayMessage(Text.of(String.format("§7Tracking %s at §cX: %.0f§7, §aY: %.0f§7, §9Z: %.0f", AsteroideAddon.trackedPlayer, entity.getX(), entity.getY(), entity.getZ())), false);
        double[] geminiwtf = {entity.getX(), entity.getY(), entity.getZ()};
        for (int i = 0; i < 3; i++) {
            last[i] = MathHelper.lerp(partialTicks * interpolation.get(), last[i], i == 1 ? entity.getY() + 1.62 : geminiwtf[i]);
        }
        if(render.get()) draw(event, entity);
        mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(last[0], last[1], last[2]));
    }


    private void draw(Render3DEvent event, Entity entity) {
        double x = MathHelper.lerp(event.tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
        double y = MathHelper.lerp(event.tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
        double z = MathHelper.lerp(event.tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();

        Box box = entity.getBoundingBox();
        event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, new Color(255, 255, 0, 128), new Color(255, 255, 0, 255), ShapeMode.Both, 0);
    }
}
