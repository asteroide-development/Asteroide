package spigey.asteroide.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import spigey.asteroide.AsteroideAddon;

import java.util.HashMap;

public class ChestStealerModule extends Module {
    public ChestStealerModule() {
        super(AsteroideAddon.CATEGORY, "", "");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Boolean> experimental = sgGeneral.add(new BoolSetting.Builder()
        .name("Enable experimental features")
        .description("Enables some features that are still in development")
        .defaultValue(false)
        .build()
    );
    @EventHandler
    private void onTick(TickEvent.Post event){
        if(mc.currentScreen instanceof GenericContainerScreen) {
            DefaultedList<Slot> slots = ((GenericContainerScreen) mc.currentScreen).getScreenHandler().slots; // what the fuck am I doing rn
            for(int i = 0; i < (slots.size() - 37); i++){
                ItemStack uwu = slots.get(i).getStack();
                // Int2ObjectMap<ItemStack> modifiedStacks = new Int2ObjectArrayMap<>();
                ClickSlotC2SPacket packet = new ClickSlotC2SPacket(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, 1, i, 0, SlotActionType.QUICK_MOVE, uwu, Int2ObjectMaps.singleton(i, ItemStack.EMPTY));
                assert mc.player != null;
                mc.player.networkHandler.sendPacket(packet);
            }
        }
    }
    @EventHandler
    private void onPacketSend(PacketEvent.Send event){
        if(!(event.packet instanceof ClickSlotC2SPacket)) return;
        if(!experimental.get()) return;
        info(String.valueOf(((ClickSlotC2SPacket) event.packet).getSyncId()) + ", " + ((ClickSlotC2SPacket) event.packet).getRevision() + ", " + ((ClickSlotC2SPacket) event.packet).getSlot() + ", " + ((ClickSlotC2SPacket) event.packet).getButton() + ", " + ((ClickSlotC2SPacket) event.packet).getActionType() + ", " + ((ClickSlotC2SPacket) event.packet).getModifiedStacks());
        // 2, 1, 0, 0, QUICK_MOVE, {0=>0 air, 62=>1 chest}
    }
}


