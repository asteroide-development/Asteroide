package spigey.asteroide.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.ItemListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringListSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import spigey.asteroide.AsteroideAddon;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class InvCleanerModule extends Module {
    public InvCleanerModule() {
        super(AsteroideAddon.CATEGORY, "inv-cleaner", "Automatically drops useless items in your inventory");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Items to drop.")
        .build()
    );
    private final Setting<List<String>> names = sgGeneral.add(new StringListSetting.Builder()
        .name("names")
        .description("Also drop items with these names")
        .defaultValue()
        .build()
    );
    /*private final Setting<List<String>> nbt = sgGeneral.add(new StringListSetting.Builder()
        .name("nbt")
        .description("Also drop items that contain this nbt")
        .defaultValue()
        .build()
    );*/


    @EventHandler
    public void onTick(TickEvent.Post event){
        if(mc.currentScreen == null) return;
        if (!(mc.currentScreen instanceof HandledScreen<?>)) return;
        DefaultedList<Slot> slots = ((HandledScreen<?>) mc.currentScreen).getScreenHandler().slots;
        if (slots == null || slots.isEmpty()) return;
        int[] exclude = new int[]{5, 6, 7, 8};
        for(Slot slot : slots){
            if(!(slot.inventory instanceof PlayerInventory) && !(slot.id == 0 && ((mc.currentScreen instanceof InventoryScreen) || (mc.currentScreen instanceof CraftingScreen)))) continue;
            if(Arrays.stream(exclude).anyMatch(slott -> slott == slot.id)) continue;
            ItemStack uwu = slot.getStack();
            if(!(items.get().contains(uwu.getItem()) || names.get().stream().anyMatch(name -> name.equalsIgnoreCase(uwu.getName().getString())))) continue;
            InvUtils.drop().slotId(slot.id);
        }
    }
}
