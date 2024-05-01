package spigey.asteroide.modules;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixininterface.IClientPlayerInteractionManager;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.util;

public class AutoSlotSwitchModule extends Module {
    public AutoSlotSwitchModule() {
        super(AsteroideAddon.CATEGORY, "", "");
    }
    // delay
    // slot range min
    // slot range max
    // slot priority
    // mode random/next
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
}


