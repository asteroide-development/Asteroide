package spigey.asteroide.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.misc.text.MeteorClickEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.item.Items;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import static java.lang.Thread.sleep;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.place;
import static spigey.asteroide.util.CommandBlock;
import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class FuckServerCommand extends Command {
    public FuckServerCommand() {
        super("fuckserver", "Fucks the server using command blocks. Only do this if you are completely sure!");
    }
    private int tick;
    private boolean enabled;
    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            assert mc.player != null;
            if(!mc.player.getAbilities().creativeMode){error("You need to be in creative mode to use this command"); return SINGLE_SUCCESS;}
            CommandBlock(Items.REPEATING_COMMAND_BLOCK, "/say hi", 1);
            this.tick = 5;
            error("Please note that this does not work if command blocks are disabled on this server");
            MeteorClient.EVENT_BUS.subscribe(this);
            return SINGLE_SUCCESS;
        });
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(this.tick > 0){this.tick--; return;} // don't execute when it's not done waiting
        if(this.tick == -1){return;} // disable when on -1
        ChatUtils.sendMsg(Text.literal("Are you sure you want to fuck the server using command blocks?\n"));
        ChatUtils.sendMsg(ConfirmButton());
        this.tick = -1; // -1 when done
    }

    private MutableText ConfirmButton(){
        MutableText Button = Text.literal("[YES]");
        MutableText Message = Text.literal("This will repeatedly kill every entity including you.");
        Message.setStyle(Message.getStyle().withFormatting(Formatting.GRAY));
        Button.setStyle(Button.getStyle()
            .withFormatting(Formatting.GREEN)
            .withClickEvent(new MeteorClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                Commands.get("thiswillfucktheserveritspartofanothercommand").toString()
            ))
            .withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Message
            )));
        return Button;
    }
}
