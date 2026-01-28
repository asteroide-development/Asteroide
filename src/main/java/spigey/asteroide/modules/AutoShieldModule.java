package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.ProjectileEntitySimulator;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import spigey.asteroide.AsteroideAddon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AutoShieldModule extends Module {
    public AutoShieldModule() { super(AsteroideAddon.CATEGORY, "Auto-Shield", "Automatically blocks when an arrow is about to hit you"); }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private Setting<Integer> checkRange = sgGeneral.add(new IntSetting.Builder()
        .name("Check Range")
        .description("Range to check for projectiles")
        .defaultValue(10)
        .sliderRange(0, 20)
        .build()
    );
    private Setting<List<Item>> items = sgGeneral.add(new ItemListSetting.Builder()
        .name("Shield Items")
        .description("Items to consider as shield")
        .defaultValue(List.of(Items.SHIELD))
        .build()
    );
    private Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("Entities")
        .description("Entities to consider as projectiles")
        .defaultValue(Set.of(
            EntityType.TRIDENT,
            EntityType.ARROW,
            EntityType.SPECTRAL_ARROW,
            EntityType.EGG,
            EntityType.FIREBALL,
            EntityType.SMALL_FIREBALL,
            EntityType.WIND_CHARGE,
            EntityType.TNT
        ))
        .build()
    );
    private Setting<Boolean> directionCheck = sgGeneral.add(new BoolSetting.Builder()
        .name("Direction Check")
        .description("Whether to check whether the projectile is traveling towards you")
        .defaultValue(true)
        .build()
    );
    private Setting<Boolean> pointToProjectile = sgGeneral.add(new BoolSetting.Builder()
        .name("Point to Entity")
        .description("Look at the entity while it's shooting to make sure the shield actually blocks it")
        .defaultValue(false)
        .build()
    );

    private boolean blocking = false;
    private int tick = -1;

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if(this.tick > 0) { this.tick--; return; }
        if(this.tick == -1) return;
        if(!this.blocking) return;
        mc.options.useKey.setPressed(false);
        this.blocking = false;
        this.tick = -1;
    }

    @EventHandler
    private void onRender(Render3DEvent event){
        for(Entity entity : mc.world.getEntities()){
            if(!entities.get().contains(entity.getType())) continue;
            if(mc.player.distanceTo(entity) > checkRange.get()) continue;
            if(!isHoldingShield()) continue;
            if(entity.getVelocity().lengthSquared() <= 0) continue;
            if(directionCheck.get() && !isLookingAtUs(entity)) continue;
            mc.options.useKey.setPressed(true);
            this.blocking = true;
            if(pointToProjectile.get()) mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getPos());
            this.tick = 5;
            return;
        }
    }

    private boolean isLookingAtUs(Entity entity) {
        ProjectileEntitySimulator sim = new ProjectileEntitySimulator();
        if(entity instanceof ProjectileEntity) sim.set(entity, true);
        else sim.set(entity, 0.05, 0.6, true);
        for(int i = 0; i < 100; i++){
            HitResult result = sim.tick();
            if(result == null) continue;
            if(result.getType() == HitResult.Type.ENTITY && ((EntityHitResult) result).getEntity() == mc.player) return true;
        }
        return false;
    }

    private boolean isHoldingShield() { return items.get().contains(mc.player.getOffHandStack().getItem()) || items.get().contains(mc.player.getMainHandStack().getItem()); }
}
