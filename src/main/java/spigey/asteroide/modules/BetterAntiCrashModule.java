package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.ParticleEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.client.render.VertexFormatElement;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.particle.ParticleType;
import net.minecraft.text.Text;
import spigey.asteroide.AsteroideAddon;

import java.util.*;
import java.util.stream.StreamSupport;

public class BetterAntiCrashModule extends Module {
    public BetterAntiCrashModule() { super(AsteroideAddon.CATEGORY, "Better-Anti-Crash", "Fixes various exploits and crashes."); }
    final SettingGroup sgEntities = settings.createGroup("Entities", true);
    private final Setting<Set<EntityType<?>>> entities = sgEntities.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Disables spawning of selected entities.")
        .build()
    );
    private final Setting<Boolean> EntityLimit = sgEntities.add(new BoolSetting.Builder()
        .name("Entity Limit Enabled")
        .description("Cancels entity spawning at a specific threshold.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Integer> EntityThreshold = sgEntities.add(new IntSetting.Builder()
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
    private final Setting<Set<EntityType<?>>> excludeFromThreshold = sgEntities.add(new EntityTypeListSetting.Builder()
        .name("Entity Limit Exclusions")
        .description("Excludes entities from the entity limit threshold.")
        .defaultValue(Set.of(EntityType.PLAYER, EntityType.ITEM))
        .build()
    );
    private final Setting<List<Item>> itemEntities = sgEntities.add(new ItemListSetting.Builder()
        .name("items")
        .description("Disables spawning of selected items on the ground.")
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
        .description("Cancels particle spawning at a specific threshold.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Integer> Threshold = sgParticles.add(new IntSetting.Builder()
        .name("Particle Threshold")
        .description("Minimum threshold to start cancelling particles.")
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
    public final Setting<Boolean> translationCrash = sgOther.add(new BoolSetting.Builder()
        .name("Block Invalid Translations")
        .description("Cancels invalid translation strings.")
        .defaultValue(true)
        .build()
    );
    public final Setting<List<String>> translations = sgOther.add(new StringListSetting.Builder().name("translations").description("Translation strings to block").defaultValue("%1$s").visible(translationCrash::get).build());
    final SettingGroup sgLength = settings.createGroup("Length", true);
    public final Setting<Integer> ThresholdLength = sgLength.add(new IntSetting.Builder()
        .name("Length Threshold")
        .description("Cancels things that are longer than X characters.")
        .defaultValue(750)
        .min(-1)
        .sliderMin(100)
        .sliderMax(10000)
        .max(2147483647)
        .build()
    );
    private final Setting<Boolean> chatLimit = sgLength.add(new BoolSetting.Builder()
        .name("Chat Messages")
        .description("Cancels chat messages that are too long.")
        .defaultValue(true)
        .build()
    );
    private final Setting<Boolean> entityLengthLimit = sgLength.add(new BoolSetting.Builder()
        .name("Entities")
        .description("Cancels entities whose names are too long.")
        .defaultValue(true)
        .build()
    );
    public final Setting<Boolean> bossBarLimit = sgLength.add(new BoolSetting.Builder()
        .name("Bossbars")
        .description("Cancels bossbars whose names are too long.")
        .defaultValue(true)
        .build()
    );
    public final Setting<Boolean> items = sgLength.add(new BoolSetting.Builder()
        .name("Items")
        .description("Cancels items whose names are too long.")
        .defaultValue(true)
        .build()
    );

    public final Setting<LengthMode> lengthMode = sgLength.add(new EnumSetting.Builder<LengthMode>()
        .name("Length Mode")
        .description("Whether to display the length of something that's blocked or not")
        .defaultValue(LengthMode.Performance)
        .build()
    );

    final SettingGroup sgPackets = settings.createGroup("Packets", true);
    public final Setting<Boolean> packets = sgPackets.add(new BoolSetting.Builder()
        .name("Block Large Packets")
        .description("Block large packets.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Set<Class<? extends Packet<?>>>> blockPackets = sgPackets.add(new PacketListSetting.Builder()
        .name("Packets")
        .description("Packets to whitelist/blacklist")
        .visible(packets::get)
        .build()
    );

    public final Setting<Integer> packetThreshold = sgPackets.add(new IntSetting.Builder()
        .name("Packet Threshold")
        .description("Blocks too large packets.")
        .defaultValue(10000)
        .min(-1)
        .sliderMin(1000)
        .sliderMax(100000)
        .max(2147483647)
        .visible(packets::get)
        .build()
    );

    private final Setting<PacketMode> packetMode = sgPackets.add(new EnumSetting.Builder<PacketMode>()
        .name("Mode")
        .description("Whitelisted packets will not be blocked.")
        .defaultValue(PacketMode.Whitelist)
        .visible(packets::get)
        .build()
    );

    private int messages = 0;
    private int tick = -1;

    final SettingGroup sgChat = settings.createGroup("Chat Limit", true);
    private final Setting<Boolean> chatLimitEnabled = sgChat.add(new BoolSetting.Builder()
        .name("Block Chat Message Spam")
        .description("Cancels chat messages when a lot of messages are sent in a short amount of time.")
        .defaultValue(true)
        .onChanged((value) -> { this.messages = 0; this.tick = -1; })
        .build()
    );
    private final Setting<Integer> messagesLimit = sgChat.add(new IntSetting.Builder()
        .name("Block if exceeds")
        .description("Number of messages that can be sent in the time limit before chat messages are blocked.")
        .defaultValue(100)
        .min(0)
        .sliderMin(1)
        .sliderMax(1000)
        .max(2147483647)
        .visible(chatLimitEnabled::get)
        .onChanged((value) -> { this.messages = 0; this.tick = -1; })
        .build()
    );
    private final Setting<Integer> timeLimit = sgChat.add(new IntSetting.Builder()
        .name("Messages in")
        .description("Time frame of messages before chat messages are blocked.")
        .defaultValue(1)
        .min(0)
        .sliderMin(1)
        .sliderMax(20)
        .max(2147483647)
        .visible(chatLimitEnabled::get)
        .onChanged((value) -> { this.messages = 0; this.tick = -1; })
        .build()
    );
    private final Setting<LengthType> timeUnit = sgChat.add(new EnumSetting.Builder<LengthType>()
        .name("time")
        .description("The time unit to use for the chat threshold.")
        .defaultValue(LengthType.Seconds)
        .visible(chatLimitEnabled::get)
        .onChanged((value) -> { this.messages = 0; this.tick = -1; })
        .build()
    );

    private Map<EntityType<?>, Integer> entityCounts = new HashMap<>();

    public boolean shouldRender(net.minecraft.entity.Entity entity) {
        if(!isActive()) return true;
        if (entity == mc.player) return true;
        if (entity == null || entity.isRemoved()) return false;
        if(entity instanceof ItemEntity) if(itemEntities.get().contains(((ItemEntity) entity).getStack().getItem())) return false;
        return !entities.get().contains(entity.getType());
    }

    @EventHandler(priority = EventPriority.HIGHEST + 1)
    private void onReceivePacket(PacketEvent.Receive event) {
        if(!isActive()) return;
        if(packets.get() && shouldCheck(event.packet)){
            if(packetThreshold.get() < event.packet.toString().length()){
                event.cancel();
                info(String.format("Blocked large %s packet with length %s!", event.packet.getClass().getSimpleName(), getMessage(event.packet.toString())));
            }
        }
        if(event.packet instanceof EntityTrackerUpdateS2CPacket && entityLengthLimit.get()) { try{
            String name = "";
            for(var entry : ((EntityTrackerUpdateS2CPacket) event.packet).trackedValues()) { if(entry.id() == 2) { name = ((Optional<Text>) entry.value()).isPresent() ? ((Optional<Text>) entry.value()).get().getString() : ""; break; }}
            if(name.length() > ThresholdLength.get()) event.cancel();
        }catch(Exception L){/**/}}
        if(event.packet instanceof ParticleS2CPacket) if(DeleteParticles.get() && ((ParticleS2CPacket) event.packet).getCount() >= Threshold.get()) event.cancel();
        if(event.packet instanceof EntityStatusS2CPacket) if((((EntityStatusS2CPacket) event.packet).getEntity(mc.world) instanceof FireworkRocketEntity) && CancelFireworks.get()) event.cancel();
        if(!(event.packet instanceof EntitySpawnS2CPacket packet)) return;
        if(entities.get().contains(packet.getEntityType())) event.cancel();
        if(this.entityCounts.getOrDefault(packet.getEntityType(), 0)+1 > EntityThreshold.get() && EntityLimit.get()) event.cancel();
    }

    private boolean shouldCheck(Packet<?> packet){
        if(packetMode.get() == PacketMode.Whitelist && blockPackets.get().contains(packet.getClass())) return false;
        return packetMode.get() != PacketMode.Blacklist || blockPackets.get().contains(packet.getClass());
    }

    @EventHandler
    private void onAddParticle(ParticleEvent event) {
        if(!isActive()) return;
        if (particles.get().contains(event.particle.getType())) event.cancel();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(!isActive()) { this.tick = -1; this.messages = 0; return; }
        if(chatLimitEnabled.get()){
            if(this.tick > 0) { this.tick--; }
            else {
                this.messages = 0;
                this.tick = switch (timeUnit.get()) {
                    case Ticks -> timeLimit.get();
                    case Seconds -> timeLimit.get() * 20;
                    case Minutes -> timeLimit.get() * 20 * 60;
                    case Hours -> timeLimit.get() * 20 * 3600;
                };
            }
        }
        if(entityLengthLimit.get()){try{
            Entity[] entities = StreamSupport.stream(mc.world.getEntities().spliterator(), false).toArray(Entity[]::new);
            for(Entity entity : entities){ if(entity.getName().getString().length() > ThresholdLength.get() && entityLengthLimit.get()){ entity.setCustomName(Text.of(String.format("§c[Entity with length %s blocked]", getMessage(entity.getName().getString()))));} }
        }catch(Exception L){/**/}}
        if(!EntityLimit.get()) return;
        Entity[] entities = StreamSupport.stream(mc.world.getEntities().spliterator(), false).toArray(Entity[]::new);
        if(entities.length < EntityThreshold.get()) { this.entityCounts = new HashMap<>(); return; }
        List<EntityType<?>> remove = new ArrayList<>();
        Map<EntityType<?>, Integer> thisCounts = new HashMap<>();
        for(Entity entity : entities){
            if(entity == mc.player) continue;
            EntityType<?> type = entity.getType();
            if(excludeFromThreshold.get().contains(type)) continue;
            if(remove.contains(type)) { entity.setRemoved(Entity.RemovalReason.DISCARDED); continue; }
            thisCounts.put(type, thisCounts.getOrDefault(type, 0) + 1);
            if(thisCounts.getOrDefault(type, 0) > EntityThreshold.get()) { entity.setRemoved(Entity.RemovalReason.DISCARDED); remove.add(type); }
        }
        this.entityCounts = thisCounts;
    }

    @EventHandler
    private void onMessageReceive(ReceiveMessageEvent event){
        if(!isActive()) return;
        if(chatLimitEnabled.get() && this.messages < messagesLimit.get()){ this.messages++; return; }
        else if(chatLimitEnabled.get()){ event.cancel(); return; }
        if(!chatLimit.get()) return;
        int length = event.getMessage().getString().length();
        if(chatLimit.get() && length <= ThresholdLength.get()) return;
        event.setMessage(Text.of("§c[Message with length " + getMessage(event.getMessage().getString()) + " blocked]"));
    }

    @Override
    public void onActivate() {
        try {
            mc.world.getEntities().forEach(entity -> {
                try{if (!shouldRender(entity)) entity.setRemoved(net.minecraft.entity.Entity.RemovalReason.DISCARDED);}
                catch(Exception L){/**/}
            });
        }catch(Exception L){/**/}
    }

    public String getMessage(String text){ return lengthMode.get() == LengthMode.Performance ? ">"+ThresholdLength.get() : String.valueOf(text.length()); }

    private enum LengthMode {
        Details,
        Performance
    }

    private enum PacketMode {
        Whitelist,
        Blacklist
    }

    private enum LengthType {
        Ticks,
        Seconds,
        Minutes,
        Hours
    }
}
