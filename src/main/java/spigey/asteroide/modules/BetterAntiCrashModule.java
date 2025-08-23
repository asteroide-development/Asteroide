package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.ParticleEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.particle.ParticleType;
import spigey.asteroide.AsteroideAddon;

import java.util.*;
import java.util.stream.StreamSupport;

public class BetterAntiCrashModule extends Module {
    public BetterAntiCrashModule() { super(AsteroideAddon.CATEGORY, "Better-Anti-Crash", "Fixes crashes from too many entities/particles/fireworks & invalid translations."); }
    final SettingGroup sgEntities = settings.createGroup("Entities", true);
    private final Setting<Set<EntityType<?>>> entities = sgEntities.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Disables spawning of selected entities.")
        .build()
    );
    private final Setting<Boolean> EntityLimit = sgEntities.add(new BoolSetting.Builder()
        .name("Entity Limit Enabled")
        .description("Cancels entity spawning at a specific treshold.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Integer> EntityTreshold = sgEntities.add(new IntSetting.Builder()
        .name("Entity Limit")
        .description("Cancels entities X from spawning when there are more than X entities in the world.")
        .defaultValue(2000)
        .min(-1)
        .sliderMin(0)
        .sliderMax(10000)
        .max(2147483647)
        .visible(EntityLimit::get)
        .build()
    );
    private final Setting<Set<EntityType<?>>> excludeFromTreshold = sgEntities.add(new EntityTypeListSetting.Builder()
        .name("Entity Limit Exclusions")
        .description("Excludes entities from the entity limit treshold.")
        .defaultValue(Set.of(EntityType.PLAYER, EntityType.ITEM))
        .build()
    );
    final SettingGroup sgParticles = settings.createGroup("Particles", true);
    private final Setting<List<ParticleType<?>>> particles = sgParticles.add(new ParticleTypeListSetting.Builder()
        .name("particles")
        .description("Particles to not block.")
        .build()
    );
    private final Setting<Boolean> DeleteParticles = sgParticles.add(new BoolSetting.Builder()
        .name("Cancel Particles")
        .description("Cancels particle spawning at a specific treshold.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Integer> Treshold = sgParticles.add(new IntSetting.Builder()
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
    final SettingGroup sgOther = settings.createGroup("Other", true);
    private final Setting<Boolean> CancelFireworks = sgOther.add(new BoolSetting.Builder()
        .name("Cancel Fireworks")
        .description("Cancels Fireworks from spawning particles.")
        .defaultValue(false)
        .build()
    );
    private final Setting<Boolean> translationCrash = sgOther.add(new BoolSetting.Builder()
        .name("Block Invalid Translations")
        .description("Cancels invalid translation strings.")
        .defaultValue(true)
        .build()
    );
    public final Setting<List<String>> translations = sgOther.add(new StringListSetting.Builder().name("translations").description("Translation strings to block").defaultValue("%1$s").visible(translationCrash::get).build());

    private Map<EntityType<?>, Integer> entityCounts = new HashMap<>();

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
        if(this.entityCounts.getOrDefault(packet.getEntityType(), 0)+1 > EntityTreshold.get() && EntityLimit.get()) event.cancel();
    }

    @EventHandler
    private void onAddParticle(ParticleEvent event) {
        if (particles.get().contains(event.particle.getType())) event.cancel();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(!EntityLimit.get()) return;
        Entity[] entities = StreamSupport.stream(mc.world.getEntities().spliterator(), false).toArray(Entity[]::new);
        if(entities.length < EntityTreshold.get()) { this.entityCounts = new HashMap<>(); return; }
        List<EntityType<?>> remove = new ArrayList<>();
        Map<EntityType<?>, Integer> thisCounts = new HashMap<>();
        for(Entity entity : entities){
            if(entity == mc.player) continue;
            EntityType<?> type = entity.getType();
            if(excludeFromTreshold.get().contains(type)) continue;
            if(remove.contains(type)) { entity.setRemoved(Entity.RemovalReason.DISCARDED); continue; }
            thisCounts.put(type, thisCounts.getOrDefault(type, 0) + 1);
            if(thisCounts.getOrDefault(type, 0) > EntityTreshold.get()) { entity.setRemoved(Entity.RemovalReason.DISCARDED); remove.add(type); }
        }
        this.entityCounts = thisCounts;
    }

    @Override
    public void onActivate() {
        mc.world.getEntities().forEach(entity -> {
            if (!shouldRender(entity)) entity.setRemoved(net.minecraft.entity.Entity.RemovalReason.DISCARDED);
        });
    }
}
