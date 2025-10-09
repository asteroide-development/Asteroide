package spigey.asteroide.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.PlayerListEntryArgumentType;
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.UUID;

public class UUIDCommand extends Command {
    public UUIDCommand() {
        super("uuid", "Shows you a players UUID.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            display(mc.player.getUuid());
            return SINGLE_SUCCESS;
        });

        builder.then(argument("player", PlayerListEntryArgumentType.create()).executes(context -> {
            GameProfile player = PlayerListEntryArgumentType.get(context).getProfile();

            if (player != null) display(player.getId());
            else error("Player not found!");
            return SINGLE_SUCCESS;
        }));
    }

    private void display(UUID uuid){
        log("§f-------------------------------");
        mc.player.sendMessage(Text.literal("§8[§cUUID§8] §7UUID: ").append(getButton(uuid.toString())), false);
        mc.player.sendMessage(Text.literal("§8[§cUUID§8] §7Compact: ").append(getButton(uuid.toString().replaceAll("-", ""))), false);
        mc.player.sendMessage(Text.literal("§8[§cUUID§8] §7Numeric: "), false);
        mc.player.sendMessage(Text.literal("§8[§cUUID§8] §7").append(getButton(NbtHelper.fromUuid(uuid).toString())), false);
        log("§f-------------------------------");
    }

    private String random(){ return "§" + "0123456789abcdefklmnor".charAt((int)(Math.random()*22)) + "§" + "0123456789abcdefklmnor".charAt((int)(Math.random()*22)) + "§" + "0123456789abcdefklmnor".charAt((int)(Math.random()*22)); }
    private void log(String message, String... args){ mc.player.sendMessage(Text.of(String.format("§8[§cUUID§8] §7%s%s", String.format(message, args), random())), false); }

    private MutableText getButton(String uuid){
        return Text.literal(String.format("§8%s", uuid)).styled(style -> style
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,  Text.literal("§7Click to copy")))
            .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, uuid))
        );
    }
}
