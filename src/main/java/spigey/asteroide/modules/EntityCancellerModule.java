package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.entity.EntityType;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;

import java.util.Set;

public class EntityCancellerModule extends Module {
    public EntityCancellerModule() { super(AsteroideAddon.CATEGORY, "entity-canceller", "Blocks entities from existing."); }
    final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Disables simulation of selected entities.")
        .build()
    );

    public boolean shouldRender(net.minecraft.entity.Entity entity) {
        if (entity == null || entity.isRemoved()) return false;
        return !entities.get().contains(entity.getType());
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onReceivePacket(PacketEvent.Receive event) {
        if(!(event.packet instanceof EntitySpawnS2CPacket packet)) return;
        if(entities.get().contains(packet.getEntityType())) event.cancel();
    }

    @Override
    public void onActivate() {
        mc.world.getEntities().forEach(entity -> {
            if (!shouldRender(entity)) entity.setRemoved(net.minecraft.entity.Entity.RemovalReason.DISCARDED);
        });
    }
}
