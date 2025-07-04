package spigey.asteroide.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
    private static final Identifier icon = Identifier.of("asteroide", "icon");

    @Shadow
    protected abstract List<PlayerListEntry> collectPlayerEntries();

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, int scaledWindowWidth, Scoreboard scoreboard, ScoreboardObjective objective, CallbackInfo ci) {/* This code is actually so fucking ass
        var entries = collectPlayerEntries();
        int maxRows = 80;
        for (int i = 0; i < entries.size(); i++) {
            var entry = entries.get(i);
            if (!AsteroideAddon.users.contains(entry.getProfile().getName())) continue;
            int col = i / maxRows;
            int row = i % maxRows;
            int tx = scaledWindowWidth / 2 - 91 + col * 98 - 10;
            int ty = 10 + row * 10;
            context.drawTexture(RenderLayer::getText, icon, tx, ty, 0f, 0f, 8, 8, 8, 8);
        }
    */}
}
