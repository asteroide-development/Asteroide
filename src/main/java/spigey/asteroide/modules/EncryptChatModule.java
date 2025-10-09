package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

public class EncryptChatModule extends Module {
    public EncryptChatModule() {
        super(AsteroideAddon.CATEGORY, "Encrypt-Chat", "Encrypts your chat messages so only asteroide users can read them");
    }

    final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<String> encryptionKey = sgGeneral.add(new StringSetting.Builder()
        .name("encryption key")
        .description("Key to encrypt chat messages with")
        .defaultValue("asteroide")
        .build()
    );

    public final Setting<Boolean> encrypt = sgGeneral.add(new BoolSetting.Builder()
        .name("encrypt messages")
        .description("Encrypts your messages when enabled")
        .defaultValue(true)
        .build()
    );

    @EventHandler
    private void onMessageSend(SendMessageEvent event) throws Exception {
        if (encrypt.get() && String.format("STRT\"%s\"", util.encrypt(event.message, encryptionKey.get())).length() <= 256) event.message = String.format("STRT\"%s\"", util.encrypt(event.message, encryptionKey.get()));
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event) throws Exception {
        try{String content = event.getMessage().getString();
        String[] split = content.split("\"");
        int start = content.indexOf("STRT\"") + 5;
        String message = content; // ???
        if (start >= 0 && (content.indexOf("\"", start)) >= 0) {
            try{event.setMessage(Text.literal(String.format("§e§l%s§r§e%s §r§c§l(Decrypted)", message.substring(0, start - 5), util.decrypt(split[1], encryptionKey.get()))));}
            catch(Exception L){System.out.println(L); event.setMessage(Text.literal(String.format("§c§l%s§r§cDecryption Failed", message.substring(0, start - 5))));}
        }}catch(Exception L){ /* Aw man */ }
    }
}
