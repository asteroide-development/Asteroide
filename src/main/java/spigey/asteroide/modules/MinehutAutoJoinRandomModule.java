package spigey.asteroide.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.ShulkerBoxScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import spigey.asteroide.AsteroideAddon;

import java.util.Objects;

public class MinehutAutoJoinRandomModule extends Module {
    public MinehutAutoJoinRandomModule() {
        super(AsteroideAddon.CATEGORY, "minehut-auto-join", "Automatically joins random minehut servers when in the lobby");
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        assert mc.player != null;
        if(!(Objects.requireNonNull(mc.getCurrentServerEntry()).address).toLowerCase().contains("minehut")) return;
        // if(mc.currentScreen == null) return;
        assert mc.player != null;
        ItemStack stack = mc.player.getInventory().getMainHandStack();
        NbtCompound tag = stack.getNbt();
        assert tag != null;
        if(!NbtHelper.toFormattedString(tag).contains("lobby:lobby-item")) return;
        // if(!Objects.equals(mc.currentScreen.getTitle().toString(), "literal{Where to?}")) return;
        Utils.rightClick();
        assert mc.currentScreen != null;
        if(!(mc.currentScreen instanceof GenericContainerScreen)) return;
        DefaultedList<Slot> slots = ((GenericContainerScreen) mc.currentScreen).getScreenHandler().slots;
        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(1, 55, 49, 0, SlotActionType.PICKUP, slots.get(49).getStack(), Int2ObjectMaps.singleton(49, ItemStack.EMPTY));
        mc.getNetworkHandler().sendPacket(packet);
    }
}


/* SLOT     49
/* REVISION 55
/* SYNC ID  1
*/
