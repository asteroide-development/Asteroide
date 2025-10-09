package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.utils.RandUtils;

import java.util.List;
import java.util.Set;

public class SilentSwapModule extends Module {
    public SilentSwapModule() { super(AsteroideAddon.CATEGORY, "Silent-Swap", "Silently swaps to an item and back."); }

    private final SettingGroup sgGeneral = settings.createGroup("General", true);
    private final SettingGroup sgDelay = settings.createGroup("Delay", true);
    private final Setting<Trigger> trigger = sgGeneral.add(new EnumSetting.Builder<Trigger>()
        .name("Swap when")
        .description("What action to trigger the silent swap.")
        .defaultValue(Trigger.Attacking)
        .build()
    );
    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Entities that trigger the silent swap.")
        .defaultValue(Set.of(EntityType.PLAYER))
        .build()
    );

    private final Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("items")
        .description("Items to silently swap to.")
        .defaultValue(List.of(
            Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD, Items.NETHERITE_SWORD,
            Items.WOODEN_AXE, Items.STONE_AXE, Items.IRON_AXE, Items.GOLDEN_AXE, Items.DIAMOND_AXE, Items.NETHERITE_AXE
        ))
        .build()
    );

    private final Setting<AutoChatGame.Mode> mode = sgDelay.add(new EnumSetting.Builder<AutoChatGame.Mode>().name("delay type").description("Whether it waits for a random or precise amount of time").defaultValue(AutoChatGame.Mode.Precise).build());
    private final Setting<Integer> delay = sgDelay.add(new IntSetting.Builder().name("delay").description("The delay in ticks").defaultValue(2).min(0).sliderMax(200).build());
    private final Setting<Integer> minoffset = sgDelay.add(new IntSetting.Builder().name("delay min offset").description("Minimum offset from the delay in ticks").defaultValue(0).min(0).sliderMax(40).visible(() -> mode.get() == AutoChatGame.Mode.Random).build());
    private final Setting<Integer> maxoffset = sgDelay.add(new IntSetting.Builder().name("delay max offset").description("Maximum offset from the delay in ticks").defaultValue(1).min(0).sliderMax(40).visible(() -> mode.get() == AutoChatGame.Mode.Random).build());

    private int tick = -1;
    private int lastSlot = 0;
    private Action action = Action.NONE;


    @EventHandler
    private void onClick(MouseButtonEvent event){
        if(this.tick > 0 || this.action != Action.NONE) return;
        if(event.button != (trigger.get() == Trigger.Attacking ? 0 : 1) || mc.currentScreen != null || mc.targetedEntity == null || !isActive()) return;
        if(!entities.get().contains(mc.targetedEntity.getType())) return;
        for(int i = 0; i < 9; i++){
            if(!items.get().contains(mc.player.getInventory().getStack(i).getItem())) continue;
            event.cancel();
            this.tick = calculateDelay();
            this.lastSlot = mc.player.getInventory().selectedSlot;
            this.action = Action.ATTACK;
            mc.player.getInventory().setSelectedSlot(i);
            break;
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(this.tick == -1 || !isActive() || mc.currentScreen != null) return;
        if(this.tick > 0) { this.tick--; return; }
        if(this.action == Action.ATTACK) {
            if(mc.targetedEntity != null) {
                if(trigger.get() == Trigger.Attacking) mc.interactionManager.attackEntity(mc.player, mc.targetedEntity);
                else mc.interactionManager.interactEntity(mc.player, mc.targetedEntity, mc.player.getActiveHand());
                mc.player.swingHand(mc.player.getActiveHand()); this.action = Action.SWAP_BACK; this.tick = calculateDelay(); }
            else { mc.player.getInventory().setSelectedSlot(this.lastSlot); this.tick = -1; this.action = Action.NONE; }
        }
        else if(this.action == Action.SWAP_BACK) {
            mc.player.getInventory().setSelectedSlot(this.lastSlot);
            this.tick = -1;
            this.action = Action.NONE;
        }
    }

    private int calculateDelay(){
        if(mode.get() == AutoChatGame.Mode.Precise) return delay.get();
        else return RandUtils.withOffset(delay.get(), minoffset.get(), maxoffset.get());
    }

    private enum Action {
        SWAP_BACK,
        ATTACK,
        NONE
    }

    private enum Trigger {
        Attacking,
        Interacting
    }
}
