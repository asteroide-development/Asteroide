package spigey.asteroide.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;
import spigey.asteroide.AsteroideAddon;

import java.util.HashMap;
import java.util.Objects;

public class ChestStealerModule extends Module {
    public ChestStealerModule() {
        super(AsteroideAddon.CATEGORY, "chest-stealer", "Takes all items from Inventories");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay before taking another item")
        .defaultValue(2)
        .min(0)
        .sliderMax(10)
        .max(100)
        .build()
    );
    private int tick;
    private int i = -1;
    DefaultedList<Slot> slots;
    @EventHandler
    private void onTick(TickEvent.Post event){
        if((mc.currentScreen instanceof GenericContainerScreen) || (mc.currentScreen instanceof ShulkerBoxScreen)) {
            if(mc.currentScreen instanceof ShulkerBoxScreen) slots = ((ShulkerBoxScreen) mc.currentScreen).getScreenHandler().slots;
            try{if(mc.currentScreen instanceof GenericContainerScreen) slots = ((GenericContainerScreen) mc.currentScreen).getScreenHandler().slots;} catch(Exception L){/* not empty */}
            if(tick > 0){tick--; return;}
            if(!((i + 1) < (slots.size() - 36))){i = -1; return;}
            i++;
            ItemStack uwu = slots.get(i).getStack();
            ClickSlotC2SPacket packet = getPacket(uwu);

            assert mc.player != null;
            if(uwu.getCount() < 1){return;}
            mc.player.networkHandler.sendPacket(packet);
            tick = delay.get();
        }
    }

    private @NotNull ClickSlotC2SPacket getPacket(ItemStack uwu) { // intellij wtf
        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(0, 0, 0, 0, SlotActionType.PICKUP, ItemStack.EMPTY, Int2ObjectMaps.singleton(0, ItemStack.EMPTY));
        if(mc.currentScreen instanceof GenericContainerScreen) packet = new ClickSlotC2SPacket(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, 1, i, 0, SlotActionType.QUICK_MOVE, uwu, Int2ObjectMaps.singleton(i, ItemStack.EMPTY));
        if(mc.currentScreen instanceof ShulkerBoxScreen) packet = new ClickSlotC2SPacket(((ShulkerBoxScreen) mc.currentScreen).getScreenHandler().syncId, 1, i, 0, SlotActionType.QUICK_MOVE, uwu, Int2ObjectMaps.singleton(i, ItemStack.EMPTY));
        return packet;
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Sent event){
        if(!(event.packet instanceof CloseHandledScreenC2SPacket)) return;
        tick = 0;
        i = -1;
    }
}


