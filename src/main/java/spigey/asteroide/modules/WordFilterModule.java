package spigey.asteroide.modules;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

import static spigey.asteroide.util.banstuff;
import static spigey.asteroide.util.msg;

public class WordFilterModule extends Module {
    public WordFilterModule() {
        super(AsteroideAddon.CATEGORY, "word-filter", "Filters words you send in the chat to prevent getting banned");
    }

    final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<List<String>> messages = sgGeneral.add(new StringListSetting.Builder()
        .name("messages to filter")
        .description("Filter these messages")
        .defaultValue("cum", "sex", "dick", "nigga", "nigger", "retard", "hitler")
        .build()
    );
    private final Setting<Boolean> woblox = sgGeneral.add(new BoolSetting.Builder()
        .name("Roblox-like Replacement")
        .description("Filters the message to look more like roblox filtering")
        .defaultValue(false)
        .build()
    );
    private final Setting<String> replacement = sgGeneral.add(new StringSetting.Builder()
        .name("filter replacement")
        .description("String to replace filtered messages with")
        .defaultValue("@$#!?&")
        .visible(() -> !woblox.get())
        .build()
    );
    private final Setting<String> roblock = sgGeneral.add(new StringSetting.Builder()
        .name("roblox-like filter replacement")
        .description("String to replace filtered messages with")
        .defaultValue("#")
        .visible(() -> woblox.get())
        .build()
    );
    private boolean activated = false;

    @Override
    public void onActivate() {
        banstuff();
        activated = false;
        if (activated) {
            info("Already activated");
            return;
        }
        MeteorClient.EVENT_BUS.subscribe(this);
        info("Subscribed! Hit that bell too.");
        activated = true;
    }

    private int delay = 1;
    String message = "";
    boolean pleasekillme = false;

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if (!(event.packet instanceof ChatMessageC2SPacket)) {
            return;
        }
        if (!isActive()) {
            return;
        }
        String content = ((ChatMessageC2SPacket) event.packet).chatMessage();
        String[] datshit = content.split(" ");
        for (int i = 0; i < datshit.length; i++) {
            for (int j = 0; j < messages.get().size(); j++) {
                if (datshit[i].toLowerCase().contains(messages.get().get(j).toLowerCase())) {
                    pleasekillme = true;
                    if (woblox.get()) {
                        String temp = "";
                        for (int k = 0; k < datshit[i].length(); k++) {
                            temp += roblock.get();
                        }
                        datshit[i] = temp;
                    } else {
                        datshit[i] = replacement.get();
                    }
                }
            }
        }
        for (int i = 0; i < datshit.length; i++) {
            message += datshit[i] + " ";
        }
        if (content.trim().toLowerCase().equals(message.trim().toLowerCase())) {
            return;
        }
        if (!pleasekillme) {
            return;
        }
        /* ChatMessageC2SPacket packet = new ChatMessageC2SPacket(new PacketByteBuf(Unpooled.buffer()).writeString(message.trim())); // wtf is this??
        mc.player.networkHandler.sendPacket(packet); // I took this shit from meteor client */
        // ↑ that code sends something so fucked up that it crashes your client
        event.cancel();
        this.delay = 1;
        if (!activated) {
            MeteorClient.EVENT_BUS.subscribe(this);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!isActive()) {return;}
        if (this.delay == -1) {return;}
        msg(this.message.trim());
        this.delay = -1;
    }
}
