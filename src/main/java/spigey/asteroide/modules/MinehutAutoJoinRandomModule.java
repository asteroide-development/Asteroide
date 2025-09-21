package spigey.asteroide.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.PacketListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.network.PacketUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import spigey.asteroide.AsteroideAddon;

import java.util.Objects;
import java.util.Set;

public class MinehutAutoJoinRandomModule extends Module {
    public MinehutAutoJoinRandomModule() {
        super(AsteroideAddon.CATEGORY, "Minehut-Auto-Join", "Automatically joins random minehut servers when in the lobby");
    }
    private final SettingGroup sgGeneral = settings.createGroup("General", true);
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay when trying to join")
        .defaultValue(5)
        .min(0)
        .sliderMax(30)
        .build()
    );
    private final Setting<Category> category = sgGeneral.add(new meteordevelopment.meteorclient.settings.EnumSetting.Builder<Category>()
        .name("Category")
        .description("The category to join")
        .defaultValue(Category.Random)
        .build()
    );
    private final SettingGroup sgPackets = settings.createGroup("Packets", true);
    private final Setting<Boolean> cancelPackets = sgPackets.add(new meteordevelopment.meteorclient.settings.BoolSetting.Builder()
        .name("Cancel Packets")
        .description("Cancels chunk packets while in the minehut lobby to reduce lag")
        .defaultValue(true)
        .build()
    );
    private final Setting<Set<Class<? extends Packet<?>>>> s2cPackets = sgPackets.add(new PacketListSetting.Builder()
        .name("S2C Packets")
        .description("Packets to cancel")
        .filter(aClass -> PacketUtils.getS2CPackets().contains(aClass))
        .visible(cancelPackets::get)
            .defaultValue(Set.of(
                ChunkDataS2CPacket.class,
                ChunkLoadDistanceS2CPacket.class,
                EntityS2CPacket.class,
                EntitySpawnS2CPacket.class
            ))
        .build()
    );
    private final Setting<Set<Class<? extends Packet<?>>>> c2sPackets = sgPackets.add(new PacketListSetting.Builder()
        .name("C2S Packets")
        .description("Packets to cancel")
        .filter(aClass -> PacketUtils.getC2SPackets().contains(aClass))
        .visible(cancelPackets::get)
        .defaultValue(Set.of(
            PlayerMoveC2SPacket.class
        ))
        .build()
    );
    private int tick = 0;

    @Override
    public void onActivate() {
        tick = delay.get();
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(tick > 0) {tick--; return;}
        if(mc.isInSingleplayer()) return;
        if(!(Objects.requireNonNull(mc.getCurrentServerEntry()).address).toLowerCase().contains("minehut.")) return;
        if(!Objects.equals(mc.player.getInventory().getMainHandStack().getName().getString(), "Find a Server (Right-Click)")) return;
        if(!(mc.currentScreen instanceof GenericContainerScreen)) Utils.rightClick();
        if(!(mc.currentScreen instanceof GenericContainerScreen)) return;
        DefaultedList<Slot> slots = ((GenericContainerScreen) mc.currentScreen).getScreenHandler().slots;
        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(1, 69, category.get().get(), 1, SlotActionType.PICKUP, slots.get(0).getStack(), Int2ObjectMaps.singleton(0, ItemStack.EMPTY));
        mc.getNetworkHandler().sendPacket(packet);
        tick = delay.get();
    }

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event){
        if(!isActive() || !cancelPackets.get() || mc.isInSingleplayer()) return;
        if(!(Objects.requireNonNull(mc.getCurrentServerEntry()).address).toLowerCase().contains("minehut.")) return;
        if(!Objects.equals(mc.player.getInventory().getMainHandStack().getName().getString(), "Find a Server (Right-Click)")) return;
        if(s2cPackets.get().contains(event.packet.getClass())) event.cancel();
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Send event){
        if(!isActive() || !cancelPackets.get() || mc.isInSingleplayer()) return;
        if(!(Objects.requireNonNull(mc.getCurrentServerEntry()).address).toLowerCase().contains("minehut.")) return;
        if(!Objects.equals(mc.player.getInventory().getMainHandStack().getName().getString(), "Find a Server (Right-Click)")) return;
        if(c2sPackets.get().contains(event.packet.getClass())) event.cancel();
    }

    private enum Category {
        Ranked(20),
        Puzzle(21),
        Box(22),
        PvP(23),
        SMP(24),
        Gen(29),
        Farming(30),
        Prison(31),
        RPG(32),
        Minigame(33),
        Skyblock(38),
        Parkour(39),
        Meme(40),
        Lifesteal(41),
        Roleplay(42),
        Factions(47),
        Modded(48),
        Random(49),
        Creative(50),
        PvP_1_8(51);

        private final int slot;
        Category(int slot){ this.slot = slot; }
        public int get(){ return slot; }
    }
}


// SLOT     49 | 26
// REVISION 55 | 58
// SYNC ID  1  | 1?
