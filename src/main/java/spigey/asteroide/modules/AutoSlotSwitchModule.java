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
import net.minecraft.stat.StatHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoSlotSwitchModule extends Module {
    public AutoSlotSwitchModule() {
        super(AsteroideAddon.CATEGORY, "Auto-Hotbar", "Automatically swaps between slots in the hotbar");
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
    private final Setting<Boolean> PriorityEnabled = sgGeneral.add(new BoolSetting.Builder()
        .name("Enable Slot Priority")
        .description("Whether it should also switch to this slot")
        .defaultValue(true)
        .build()
    );
    private final Setting<Integer> slot1p = sgGeneral.add(new IntSetting.Builder().name("Slot 1 Priority").description("Priority to switch to slot 1").defaultValue(1).min(0).sliderMax(10).visible(() -> PriorityEnabled.get()).build());
    private final Setting<Integer> slot2p = sgGeneral.add(new IntSetting.Builder().name("Slot 2 Priority").description("Priority to switch to slot 2").defaultValue(1).min(0).sliderMax(10).visible(() -> PriorityEnabled.get()).build());
    private final Setting<Integer> slot3p = sgGeneral.add(new IntSetting.Builder().name("Slot 3 Priority").description("Priority to switch to slot 3").defaultValue(1).min(0).sliderMax(10).visible(() -> PriorityEnabled.get()).build());
    private final Setting<Integer> slot4p = sgGeneral.add(new IntSetting.Builder().name("Slot 4 Priority").description("Priority to switch to slot 4").defaultValue(1).min(0).sliderMax(10).visible(() -> PriorityEnabled.get()).build());
    private final Setting<Integer> slot5p = sgGeneral.add(new IntSetting.Builder().name("Slot 5 Priority").description("Priority to switch to slot 5").defaultValue(1).min(0).sliderMax(10).visible(() -> PriorityEnabled.get()).build());
    private final Setting<Integer> slot6p = sgGeneral.add(new IntSetting.Builder().name("Slot 6 Priority").description("Priority to switch to slot 6").defaultValue(1).min(0).sliderMax(10).visible(() -> PriorityEnabled.get()).build());
    private final Setting<Integer> slot7p = sgGeneral.add(new IntSetting.Builder().name("Slot 7 Priority").description("Priority to switch to slot 7").defaultValue(1).min(0).sliderMax(10).visible(() -> PriorityEnabled.get()).build());
    private final Setting<Integer> slot8p = sgGeneral.add(new IntSetting.Builder().name("Slot 8 Priority").description("Priority to switch to slot 8").defaultValue(1).min(0).sliderMax(10).visible(() -> PriorityEnabled.get()).build());
    private final Setting<Integer> slot9p = sgGeneral.add(new IntSetting.Builder().name("Slot 9 Priority").description("Priority to switch to slot 9").defaultValue(1).min(0).sliderMax(10).visible(() -> PriorityEnabled.get()).build());
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
    private void onTick(TickEvent.Post event) {
        if (remainingDelay > 0) {
            remainingDelay--;
            return;
        }
        int num = 9;
        if (slotmode.get() == SlotMode.Custom) {
            boolean[] slots = {slot1.get(), slot2.get(), slot3.get(), slot4.get(), slot5.get(), slot6.get(), slot7.get(), slot8.get(), slot9.get(), false};
            if (!util.BoolContains(slots, true)) {
                toggle();
                error("No Slots enabled");
                return;
            }
            while (!slots[num]) {
                if (switchmode.get() == SwitchMode.Random) {
                    num = util.randomNum(0, 8);
                } else {
                    assert mc.player != null;
                    int temp = mc.player.getInventory().selectedSlot + 1;
                    for (int i = 0; i < 10; i++) {
                        if (temp > 9) {
                            temp = 0;
                        }
                        if (!slots[temp]) {
                            temp++;
                        }
                    }
                    num = temp;
                    // Custom Switch Mode done
                }
            }
        } else {
            if (switchmode.get() == SwitchMode.Random) {
                num = util.randomNum(0, 8);
            } else {
                assert mc.player != null;
                num = mc.player.getInventory().selectedSlot + 1;
                if (num == 9) {
                    num = 0;
                }
            }
        }
        if (PriorityEnabled.get()) { // what the actual fuck am i doing
            List<Integer> priorityList = new ArrayList<>();
            for (int i = 1; i <= 10; i++) {
                int slotPriority = switch (i) {
                    case 1 -> slot1p.get();
                    case 2 -> slot2p.get();
                    case 3 -> slot3p.get();
                    case 4 -> slot4p.get();
                    case 5 -> slot5p.get();
                    case 6 -> slot6p.get();
                    case 7 -> slot7p.get();
                    case 8 -> slot8p.get();
                    case 9 -> slot9p.get();
                    default -> 0;
                };
                for (int j = 0; j < slotPriority && j < 10; j++) {
                    priorityList.add(i);
                }
            }
            int[] priorityArray = priorityList.stream().mapToInt(Integer::intValue).toArray();
            // no like literally what the literal fucking fuck is this fucking ass spaghetti code please just fucking kill me
            assert priorityArray.length > 0;
            num = priorityArray[util.randomNum(0, priorityArray.length - 1)] - 1;
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
