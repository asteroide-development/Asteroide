package spigey.asteroide.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

public class TrackerCommand extends Command {
    public TrackerCommand() {
        super("track", "Tracks a specified player. Use with Tracker module.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("player", PlayerListEntryArgumentType.create()).executes(context -> {
            GameProfile profile = PlayerListEntryArgumentType.get(context).getProfile();
            if((profile == null) || mc.world == null) {error("Player not found."); return SINGLE_SUCCESS;}
            for(Entity entity : mc.world.getEntities()){
                if(!(entity instanceof PlayerEntity)) continue;
                if(util.withoutStyle(entity.getName()).equals(profile.getName())){
                    ChatUtils.sendMsg(Text.of(String.format("§7Player found at §cX: %.0f§7, §aY: %.0f§7, §9Z: %.0f", entity.getX(), entity.getY(), entity.getZ())));
                    AsteroideAddon.trackedPlayer = profile.getName();
                    return SINGLE_SUCCESS;
                }
            }
            ChatUtils.sendMsg(Text.of("§cPlayer not found, is it too far away?"));
            return SINGLE_SUCCESS;
        }));
    }
}
