package spigey.asteroide.modules;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IClientPlayerInteractionManager;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;
import net.minecraft.command.StorageDataObject;

import java.util.List;

public class AutoSlotSwitchModule extends Module {
    public AutoSlotSwitchModule() {
        super(AsteroideAddon.CATEGORY, "auto-hotbar", "Automatically swaps between slots in the hotbar");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay before switching slots again")
        .defaultValue(0)
        .min(0)
        .sliderMax(200)
        .build()
    );
    private final Setting<AutoSlotSwitchModule.SlotMode> slotmode = sgGeneral.add(new EnumSetting.Builder<AutoSlotSwitchModule.SlotMode>()
        .name("Slot Mode")
        .description("Mode of the slot selection")
        .defaultValue(AutoSlotSwitchModule.SlotMode.All)
        .build()
    );
    private final Setting<Boolean> slot1 = sgGeneral.add(new BoolSetting.Builder().name("Slot 1").description("Whether it should also switch to this slot").defaultValue(true).visible(() -> slotmode.get() == SlotMode.Custom).build());
    private final Setting<Boolean> slot2 = sgGeneral.add(new BoolSetting.Builder().name("Slot 2").description("Whether it should also switch to this slot").defaultValue(true).visible(() -> slotmode.get() == SlotMode.Custom).build());
    private final Setting<Boolean> slot3 = sgGeneral.add(new BoolSetting.Builder().name("Slot 3").description("Whether it should also switch to this slot").defaultValue(true).visible(() -> slotmode.get() == SlotMode.Custom).build());
    private final Setting<Boolean> slot4 = sgGeneral.add(new BoolSetting.Builder().name("Slot 4").description("Whether it should also switch to this slot").defaultValue(true).visible(() -> slotmode.get() == SlotMode.Custom).build());
    private final Setting<Boolean> slot5 = sgGeneral.add(new BoolSetting.Builder().name("Slot 5").description("Whether it should also switch to this slot").defaultValue(true).visible(() -> slotmode.get() == SlotMode.Custom).build());
    private final Setting<Boolean> slot6 = sgGeneral.add(new BoolSetting.Builder().name("Slot 6").description("Whether it should also switch to this slot").defaultValue(true).visible(() -> slotmode.get() == SlotMode.Custom).build());
    private final Setting<Boolean> slot7 = sgGeneral.add(new BoolSetting.Builder().name("Slot 7").description("Whether it should also switch to this slot").defaultValue(true).visible(() -> slotmode.get() == SlotMode.Custom).build());
    private final Setting<Boolean> slot8 = sgGeneral.add(new BoolSetting.Builder().name("Slot 8").description("Whether it should also switch to this slot").defaultValue(true).visible(() -> slotmode.get() == SlotMode.Custom).build());
    private final Setting<Boolean> slot9 = sgGeneral.add(new BoolSetting.Builder().name("Slot 9").description("Whether it should also switch to this slot").defaultValue(true).visible(() -> slotmode.get() == SlotMode.Custom).build());
    private final Setting<AutoSlotSwitchModule.SwitchMode> switchmode = sgGeneral.add(new EnumSetting.Builder<AutoSlotSwitchModule.SwitchMode>()
        .name("Switching Mode")
        .description("Mode of the slot Switching")
        .defaultValue(AutoSlotSwitchModule.SwitchMode.Next)
        .build()
    );
    // slot priority
    private boolean SlotActivated = false;
    private int remainingDelay = -1;
    @Override
    public void onActivate() {
        if(!SlotActivated){return;}
        MeteorClient.EVENT_BUS.subscribe(this);
        SlotActivated = true;
    }
    @EventHandler
    private void onTick(TickEvent.Post event){
        if(remainingDelay > 0){remainingDelay--; return;}
        int num = 9;
        if(slotmode.get() == SlotMode.Custom){
            boolean[] slots = {slot1.get(), slot2.get(), slot3.get(), slot4.get(), slot5.get(), slot6.get(), slot7.get(), slot8.get(), slot9.get(), false};
            if(!util.BoolContains(slots,true)){toggle(); error("No Slots enabled"); return;}
            while(!slots[num]){
                if(switchmode.get() == SwitchMode.Random) {
                    num = util.randomNum(0, 8);
                } else{
                    assert mc.player != null;
                    int temp = mc.player.getInventory().selectedSlot + 1;
                    for(int i = 0; i < 10; i++){
                        if(temp > 9){temp = 0;}
                        if(!slots[temp]){temp++;}
                    }
                    num = temp;
                    // Custom Switch Mode done
                }
            }
        } else{
            if(switchmode.get() == SwitchMode.Random) {
                num = util.randomNum(0, 8);
            } else{
                assert mc.player != null;
                num = mc.player.getInventory().selectedSlot + 1;
                if(num == 9){num = 0;}
            }
        }
        // Custom slot mode done
        InvUtils.swap(num, false);
        remainingDelay = delay.get();
    }

    //////////////////////////////////////////////
    //                                          //
    //             code end thingy              //
    //                                          //
    //////////////////////////////////////////////

    public enum SlotMode{
        All,
        Custom
    }
    public enum SwitchMode{
        Random,
        Next
    }
}
