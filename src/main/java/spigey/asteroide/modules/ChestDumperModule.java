package spigey.asteroide.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import spigey.asteroide.AsteroideAddon;

import java.util.List;

public class ChestDumperModule extends Module {
    public ChestDumperModule() {
        super(AsteroideAddon.CATEGORY, "Chest-Dumper", "Dumps all items into inventories");
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
    private final Setting<List<String>> name = sgGeneral.add(new StringListSetting.Builder()
        .name("must-have-name")
        .description("Only dump items with these names")
        .defaultValue()
        .build()
    );
    private final Setting<List<String>> contain = sgGeneral.add(new StringListSetting.Builder()
        .name("must-contain-name")
        .description("Only dump items whose names contain these phrases")
        .defaultValue()
        .build()
    );
    public final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Which items to dump")
        .build()
    );
    private final Setting<StealMode> stealMode = sgGeneral.add(new EnumSetting.Builder<StealMode>()
        .name("dump-mode")
        .description("Whether to use whitelist or blacklist")
        .defaultValue(StealMode.All)
        .build()
    );
    private final Setting<Boolean> close = sgGeneral.add(new BoolSetting.Builder()
        .name("close")
        .description("Closes the screen when done")
        .defaultValue(true)
        .build()
    );
    private enum StealMode {
        Whitelist,
        Blacklist,
        All
    }

    private int tick;
    private int i = -1;
    DefaultedList<Slot> slots;
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!(mc.currentScreen instanceof HandledScreen<?> screen) || mc.currentScreen instanceof InventoryScreen) return;
        DefaultedList<Slot> slots = screen.getScreenHandler().slots;
        if (slots == null || slots.isEmpty()) return;
        if (tick > 0) { tick--; return; }

        for(Slot slot : slots){
            if(!(slot.inventory instanceof PlayerInventory)) continue;
            if (shouldDump(slot.getStack())) {
                ClickSlotC2SPacket packet = getPacket(slot.getStack(), slot);
                assert mc.player != null;
                mc.player.networkHandler.sendPacket(packet);
                tick = delay.get();
                if(delay.get() > 0) return;
            }
        }
        if(screen.getScreenHandler().slots.stream().noneMatch(slot -> slot.hasStack() && slot.inventory instanceof PlayerInventory) && close.get()) {
            screen.close();
            mc.player.networkHandler.sendPacket(new CloseHandledScreenC2SPacket(screen.getScreenHandler().syncId));
        }
    }

    private boolean shouldDump(ItemStack item){
        if(item.isEmpty()) return false;
        if(stealMode.get() == StealMode.All) return true;
        for(int i = 0; i < name.get().size(); i++) if(item.getName().getString().equalsIgnoreCase(name.get().get(i))) return stealMode.get() == StealMode.Whitelist;
        for(int i = 0; i < contain.get().size(); i++) if(item.getName().getString().toLowerCase().contains(contain.get().get(i).toLowerCase())) return stealMode.get() == StealMode.Whitelist;
        for(int i = 0; i < items.get().size(); i++) if(item.getItem().getDefaultStack().getName().equals(items.get().get(i).getDefaultStack().getName())) return stealMode.get() == StealMode.Whitelist;
        return (name.get().isEmpty() && contain.get().isEmpty() && items.get().isEmpty()) || stealMode.get() != StealMode.Whitelist;
    }
    private ClickSlotC2SPacket getPacket(ItemStack uwu, Slot slot) {
        return new ClickSlotC2SPacket(((HandledScreen<?>) mc.currentScreen).getScreenHandler().syncId, 1, slot.id, 0, SlotActionType.QUICK_MOVE, uwu, Int2ObjectMaps.singleton(slot.id, ItemStack.EMPTY));
    }

    @EventHandler
    private void onPacketSend(PacketEvent.Sent event){
        if(!(event.packet instanceof CloseHandledScreenC2SPacket)) return;
        tick = 0;
        i = -1;
    }
}

