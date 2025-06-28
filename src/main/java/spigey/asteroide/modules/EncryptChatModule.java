package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.s2c.play.ChatMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.ProfilelessChatMessageS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

public class EncryptChatModule extends Module {
    public EncryptChatModule() {
        super(AsteroideAddon.CATEGORY, "encrypt-chat", "Encrypts your chat messages so only asteroide users can read them");
    }

    final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<String> encryptionKey = sgGeneral.add(new StringSetting.Builder()
        .name("encryption key")
        .description("Key to encrypt chat messages with")
        //.defaultValue("SF98lhNAIzsd3U8s")
        .defaultValue("asteroide")
        .build()
    );

    public final Setting<Boolean> encrypt = sgGeneral.add(new BoolSetting.Builder()
        .name("encrypt messages")
        .description("Encrypts your messages when enabled")
        .defaultValue(true)
        .build()
    );

    /*@EventHandler
    private void onPacketReceive(PacketEvent.Receive event) throws Exception {
        if (!isActive()) return;
        if (event.packet instanceof GameMessageS2CPacket packet) {
            String content = packet.content().getString();
            if (content.contains("STRT\"")) {
                handlePacket(packet.content(), packet.overlay());
            }
        } else if (event.packet instanceof ChatMessageS2CPacket packet) {
            String content = packet.body().content();
            if (content.contains("STRT\"")) {
                handlePacket(packet.body().content(), false);
                info(mc.world.getPlayerByUuid(packet.sender()).getName().getString());
            }
        } else if (event.packet instanceof ProfilelessChatMessageS2CPacket packet) {
            String content = packet.message().getString();
            if (content.contains("STRT\"")) {
                handlePacket(packet.message(), false);
            }
        }
    }


    private void handlePacket(Text content, boolean overlay) throws Exception {
        String message = content.getString();
        String[] split = message.split("\"");
        int start = message.indexOf("STRT\"") + 5;
        if (start >= 0 && (message.indexOf("\"", start)) >= 0) {
            try{mc.getNetworkHandler().onGameMessage(new GameMessageS2CPacket(Text.literal(String.format("§e§l%s§r§e%s §7(Decrypted)", message.substring(0, start - 5), util.decrypt(split[1], encryptionKey.get()))), overlay));}
            catch(javax.crypto.AEADBadTagException L){System.out.println(L); mc.getNetworkHandler().onGameMessage(new GameMessageS2CPacket(Text.literal(String.format("§c§l%s§r§cDecryption Failed", message.substring(0, start - 5))), overlay));}
        }
    }

    private void handlePacket(String content, boolean overlay) throws Exception {
        String[] split = content.split("\"");
        int start = content.indexOf("STRT\"") + 5;
        String message = content; // ???
        if (start >= 0 && (content.indexOf("\"", start)) >= 0) {
            try{mc.getNetworkHandler().onGameMessage(new GameMessageS2CPacket(Text.literal(String.format("§e§l%s§r§e%s §7(Decrypted)", message.substring(0, start - 5), util.decrypt(split[1], encryptionKey.get()))), overlay));}
            catch(javax.crypto.AEADBadTagException L){System.out.println(L); mc.getNetworkHandler().onGameMessage(new GameMessageS2CPacket(Text.literal(String.format("§c§l%s§r§cDecryption Failed", message.substring(0, start - 5))), overlay));}
        }
    }*/

    @EventHandler
    private void onMessageSend(SendMessageEvent event) throws Exception {
        if (encrypt.get() && String.format("STRT\"%s\"", util.encrypt(event.message, encryptionKey.get())).length() <= 256) event.message = String.format("STRT\"%s\"", util.encrypt(event.message, encryptionKey.get()));
    }

    /*@EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) throws Exception {
        String content = event.getMessage().getString();
        int start = content.indexOf("STRT\"") + 5;
        if (start > 4 && content.indexOf("\"", start) > start) {
            int remaining = start - 5;
            Text prefix = Text.empty();
            for (Text part : event.getMessage().getSiblings()) {
                String s = part.getString();
                if (remaining <= 0) break;
                if (s.length() <= remaining) {
                    prefix = ((MutableText) prefix).append(part.copy());
                    remaining -= s.length();
                } else {
                    prefix = ((MutableText) prefix).append(Text.literal(s.substring(0, remaining)).setStyle(part.getStyle()));
                    break;
                }
            }
            try { event.setMessage(((MutableText) prefix).append(Text.literal(util.decrypt(content.substring(start, content.indexOf("\"", start)), encryptionKey.get()))).append(Text.literal(" §r§c§l(Decrypted)")));}
            catch (javax.crypto.AEADBadTagException L) { event.setMessage(((MutableText) prefix).append(Text.literal("§cDecryption Failed"))); }
        }
    }*/

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) throws Exception {
        String content = event.getMessage().getString();
        String[] split = content.split("\"");
        int start = content.indexOf("STRT\"") + 5;
        String message = content; // ???
        if (start >= 0 && (content.indexOf("\"", start)) >= 0) {
            try{event.setMessage(Text.literal(String.format("§e§l%s§r§e%s §r§c§l(Decrypted)", message.substring(0, start - 5), util.decrypt(split[1], encryptionKey.get()))));}
            catch(Exception L){System.out.println(L); event.setMessage(Text.literal(String.format("§c§l%s§r§cDecryption Failed", message.substring(0, start - 5))));}
        }
    }
}
