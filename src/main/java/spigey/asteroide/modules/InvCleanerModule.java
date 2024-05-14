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
import net.minecraft.util.collection.DefaultedList;
import spigey.asteroide.AsteroideAddon;

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
    private final Setting<List<String>> nbt = sgGeneral.add(new StringListSetting.Builder()
        .name("nbt")
        .description("Also drop items that contain this nbt")
        .defaultValue()
        .build()
    );


    @EventHandler
    public void onTick(TickEvent.Post event){
        if(mc.currentScreen == null) return;
        if(!(mc.currentScreen instanceof InventoryScreen)) return;
        DefaultedList<Slot> slots = ((InventoryScreen) mc.currentScreen).getScreenHandler().slots;
        for(int i = 0; i < mc.player.getInventory().size(); i++){
            ItemStack uwu = slots.get(i).getStack();
            assert uwu != null;
            if(uwu.getNbt() != null){
                if(!(items.get().contains(uwu.getItem()) || names.get().stream().anyMatch(name -> name.equalsIgnoreCase(uwu.getName().getString())) || nbt.get().contains(NbtHelper.toFormattedString(uwu.getNbt()).trim()))) continue;
                InvUtils.drop().slot(i);
            } else{
                if(!(items.get().contains(uwu.getItem()) || names.get().stream().anyMatch(name -> name.equalsIgnoreCase(uwu.getName().getString())))) continue;
                InvUtils.drop().slot(i);
            }
        }
    }
}
