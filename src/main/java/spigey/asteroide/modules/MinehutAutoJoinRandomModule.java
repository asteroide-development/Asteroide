package spigey.asteroide.modules;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
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
        super(AsteroideAddon.CATEGORY, "Minehut-Auto-Join", "Automatically joins random minehut servers when in the lobby");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay when trying to join")
        .defaultValue(5)
        .min(0)
        .sliderMax(30)
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
        assert mc.player != null;
        if(mc.isInSingleplayer()) return;
        if(!(Objects.requireNonNull(mc.getCurrentServerEntry()).address).toLowerCase().contains("minehut.")) return;
        assert mc.player != null;
        if(!Objects.equals(mc.player.getInventory().getMainHandStack().getName().getString(), "Find a Server (Right-Click)")) return;
        Utils.rightClick();
        assert mc.currentScreen != null;
        if(!(mc.currentScreen instanceof GenericContainerScreen)) return;
        DefaultedList<Slot> slots = ((GenericContainerScreen) mc.currentScreen).getScreenHandler().slots;
        ClickSlotC2SPacket packet = new ClickSlotC2SPacket(1, 55, 49, 0, SlotActionType.PICKUP, slots.get(26).getStack(), Int2ObjectMaps.singleton(26, ItemStack.EMPTY));
        mc.getNetworkHandler().sendPacket(packet);
        tick = delay.get();
    }
}


// SLOT     49 | 26
// REVISION 55 | 58
// SYNC ID  1  | 1?
