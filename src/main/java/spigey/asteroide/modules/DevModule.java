package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.game.SendMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

public class DevModule extends Module {
    public DevModule() {
        super(AsteroideAddon.CATEGORY, "Dev", "Dev tools you shouldn't be able to see.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> slots = sgGeneral.add(new BoolSetting.Builder()
        .name("Test Slot Packets")
        .description("Tests Slot Packets")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> testEvents = sgGeneral.add(new BoolSetting.Builder()
        .name("Test Events")
        .description("Tests SendMessageEvent")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> testMsgs = sgGeneral.add(new BoolSetting.Builder()
        .name("Test AutoChatGame")
        .description("Tests AutoChatGame")
        .defaultValue(false)
        .build()
    );

    private final Setting<Boolean> testBrand = sgGeneral.add(new BoolSetting.Builder()
        .name("Brand Test")
        .description("Tests Client Brand")
        .defaultValue(false)
        .build()
    );

    @EventHandler
    private void onPacketSend(PacketEvent.Send event) {
        if(event.packet instanceof CustomPayloadC2SPacket && testBrand.get()){
            info("§cClient Brand: §a" + ((CustomPayloadC2SPacket) event.packet).payload().getId().id());
            System.out.print(((CustomPayloadC2SPacket) event.packet).payload().getId().id());
        }
        if (!(event.packet instanceof ClickSlotC2SPacket) || !slots.get()) return;
        ChatUtils.sendMsg(Text.of("§cSLOT " + ((ClickSlotC2SPacket) event.packet).getSlot()));
        ChatUtils.sendMsg(Text.of("§aREVISION " + ((ClickSlotC2SPacket) event.packet).getRevision()));
        ChatUtils.sendMsg(Text.of("§9SYNC ID " + ((ClickSlotC2SPacket) event.packet).getSyncId()));
        ChatUtils.sendMsg(Text.of("§7ACTION " + ((ClickSlotC2SPacket) event.packet).getActionType().name()));
        event.cancel();
    }

    @EventHandler
    private void onMessageSend(SendMessageEvent event) throws Exception {
        if(!testEvents.get()) return;
        event.message = event.message.replaceAll("a", "а")
            .replaceAll("c", "с")
            .replaceAll("e", "е")
            .replaceAll("h", "һ")
            .replaceAll("i", "і")
            .replaceAll("j", "ј")
            .replaceAll("n", "ո")
            .replaceAll("o", "о")
            .replaceAll("p", "р")
            .replaceAll("u", "ս")
            .replaceAll("v", "ν")
            .replaceAll("x", "х")
            .replaceAll("y", "у") + " h";
    }

    @EventHandler
    private void onGameJoin(GameJoinedEvent event){
        if(!testEvents.get()) return;
        AsteroideAddon.LOG.info(event.toString());
        info(event.toString());
    }

    boolean Received = false;
    int ticks = 0;
    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event){
        if(!testMsgs.get() || !isActive()) return;
        if(event.getMessage().getString().contains("[Dev]")) return;
        info(event.getMessage().toString());
        this.Received = !this.Received;
        if(this.Received) this.ticks = 0;
        else info(String.format("Auto Chatgame took %d ticks (%.2f seconds)", this.ticks, this.ticks / 20f));
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(!testMsgs.get() || !isActive() || !this.Received) return;
        this.ticks++;
    }
}
