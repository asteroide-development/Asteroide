package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
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
    private final Setting<Boolean> DeleteParticles = sgGeneral.add(new BoolSetting.Builder()
        .name("Cancel Particles")
        .description("Cancels particle spawning at a specific treshold.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Integer> Treshold = sgGeneral.add(new IntSetting.Builder()
        .name("Particle Treshold")
        .description("Minimum treshold to start cancelling particles.")
        .defaultValue(2000)
        .min(-1)
        .sliderMin(0)
        .sliderMax(10000)
        .max(2147483647)
        .visible(DeleteParticles::get)
        .build()
    );
    private final Setting<Boolean> CancelFireworks = sgGeneral.add(new BoolSetting.Builder()
        .name("Cancel Fireworks")
        .description("Cancels Fireworks from spawning particles.")
        .defaultValue(false)
        .build()
    );
    public boolean shouldRender(net.minecraft.entity.Entity entity) {
        if (entity == mc.player) return true;
        if (entity == null || entity.isRemoved()) return false;
        return !entities.get().contains(entity.getType());
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onReceivePacket(PacketEvent.Receive event) {
        if(event.packet instanceof ParticleS2CPacket) if(DeleteParticles.get() && ((ParticleS2CPacket) event.packet).getCount() >= Treshold.get()) event.cancel();
        if(event.packet instanceof EntityStatusS2CPacket) if((((EntityStatusS2CPacket) event.packet).getEntity(mc.world) instanceof FireworkRocketEntity) && CancelFireworks.get()) event.cancel();
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
