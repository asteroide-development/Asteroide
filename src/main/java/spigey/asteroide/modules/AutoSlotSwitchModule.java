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

public class AutoSlotSwitchModule extends Module {
    public AutoSlotSwitchModule() {
        super(AsteroideAddon.CATEGORY, "", "");
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("The delay before switching slots again")
        .defaultValue(5)
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
    @Override
    public void onActivate() {
        if(!SlotActivated){return;}
        MeteorClient.EVENT_BUS.subscribe(this);
        SlotActivated = true;
    }
    @EventHandler
    private void onTick(TickEvent.Post event){
        InvUtils.swap(util.randomNum(0,8), false);
    }
    public enum SlotMode{
        All,
        Custom
    }
    public enum SwitchMode{
        Random,
        Next
    }
}


