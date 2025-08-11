package spigey.asteroide.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.HopperScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.collection.DefaultedList;
import spigey.asteroide.AsteroideAddon;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ChestStealerModule extends Module {
    public ChestStealerModule() {
        super(AsteroideAddon.CATEGORY, "Chest-Stealer", "Takes all items from Inventories");
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
        .description("Only take items with these names")
        .defaultValue()
        .build()
    );
    private final Setting<List<String>> contain = sgGeneral.add(new StringListSetting.Builder()
        .name("must-contain-name")
        .description("Only take items whose names contain these phrases")
        .defaultValue()
        .build()
    );
    public final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Which items to steal")
        .build()
    );
    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("Whether to use the new or classic method of stealing items")
        .defaultValue(Mode.Classic)
        .build()
    );

    private enum Mode {
        New,
        Classic
    }
    private int tick;
    private int i = -1;
    DefaultedList<Slot> slots;
    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(mode.get() == Mode.Classic) {
            if ((mc.currentScreen instanceof GenericContainerScreen) || (mc.currentScreen instanceof ShulkerBoxScreen) || (mc.currentScreen instanceof HopperScreen)) {
                if (mc.currentScreen instanceof ShulkerBoxScreen) {
                    slots = ((ShulkerBoxScreen) mc.currentScreen).getScreenHandler().slots;
                } else if (mc.currentScreen instanceof HopperScreen) {
                    slots = ((HopperScreen) mc.currentScreen).getScreenHandler().slots;
                } else {
                    try {
                        slots = ((GenericContainerScreen) mc.currentScreen).getScreenHandler().slots;
                    } catch (Exception ignored) {
                    }
                }

                if (tick > 0) {
                    tick--;
                    return;
                }

                while ((i + 1) < (slots.size() - 36)) {
                    i++;
                    ItemStack uwu = slots.get(i).getStack();
                    boolean yes = name.get().isEmpty() && contain.get().isEmpty() && items.get().isEmpty();
                    if (!uwu.isEmpty()) for (int i = 0; i < name.get().size(); i++)
                        if (uwu.getName().getString().equalsIgnoreCase(name.get().get(i))) {
                            yes = true;
                            break;
                        }
                    if (!uwu.isEmpty()) for (int i = 0; i < contain.get().size(); i++)
                        if (uwu.getName().getString().toLowerCase().contains(contain.get().get(i).toLowerCase())) {
                            yes = true;
                            break;
                        }
                    if (!uwu.isEmpty()) for (int i = 0; i < items.get().size(); i++)
                        if (uwu.getItem().getDefaultStack().getName().equals(items.get().get(i).getDefaultStack().getName())) {
                            yes = true;
                            break;
                        }
                    if (!(uwu.isEmpty()) && yes) {
                        ClickSlotC2SPacket packet = getPacket(uwu);
                        assert mc.player != null;
                        mc.player.networkHandler.sendPacket(packet);
                        tick = delay.get();
                        if (delay.get() != 0) return;
                    }
                }

                if (!((i + 1) < (slots.size() - 36))) {
                    i = -1;
                }
            }
        } else {
            if (!(mc.currentScreen instanceof HandledScreen<?>)) return;
            DefaultedList<Slot> slots = ((HandledScreen<?>) mc.currentScreen).getScreenHandler().slots;
            if (slots == null || slots.isEmpty()) return;

            if (tick > 0) { tick--; return; }

            for(Slot slot : slots){
                if(slot.inventory instanceof PlayerInventory) continue;
                ItemStack uwu = slot.getStack();
                boolean yes = name.get().isEmpty() && contain.get().isEmpty() && items.get().isEmpty();
                if(!uwu.isEmpty()) for(int i = 0; i < name.get().size(); i++) if(uwu.getName().getString().equalsIgnoreCase(name.get().get(i))) {yes = true; break;}
                if(!uwu.isEmpty()) for(int i = 0; i < contain.get().size(); i++) if(uwu.getName().getString().toLowerCase().contains(contain.get().get(i).toLowerCase())) {yes = true; break;}
                if(!uwu.isEmpty()) for(int i = 0; i < items.get().size(); i++) if(uwu.getItem().getDefaultStack().getName().equals(items.get().get(i).getDefaultStack().getName())) {yes = true; break;}
                if (!(uwu.isEmpty()) && yes) {
                    ClickSlotC2SPacket packet = getPacket(uwu, slot);
                    assert mc.player != null;
                    mc.player.networkHandler.sendPacket(packet);
                    tick = delay.get();
                    if(delay.get() > 0) return;
                }
            }
        }
    }

    private ClickSlotC2SPacket getPacket(ItemStack uwu) { // intellij wtf
        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(0, 0, 0, 0, SlotActionType.PICKUP, ItemStack.EMPTY, Int2ObjectMaps.singleton(0, ItemStack.EMPTY));
        if(mc.currentScreen instanceof GenericContainerScreen) packet = new ClickSlotC2SPacket(((GenericContainerScreen) mc.currentScreen).getScreenHandler().syncId, 1, i, 0, SlotActionType.QUICK_MOVE, uwu, Int2ObjectMaps.singleton(i, ItemStack.EMPTY));
        if(mc.currentScreen instanceof ShulkerBoxScreen) packet = new ClickSlotC2SPacket(((ShulkerBoxScreen) mc.currentScreen).getScreenHandler().syncId, 1, i, 0, SlotActionType.QUICK_MOVE, uwu, Int2ObjectMaps.singleton(i, ItemStack.EMPTY));
        return packet;
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

