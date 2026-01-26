package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import spigey.asteroide.AsteroideAddon;

import java.util.Optional;
import java.util.Set;

public class AntiAimModule extends Module {
    public AntiAimModule() { super(AsteroideAddon.CATEGORY, "Anti-Aim", "Attempts to avoid players looking at you"); }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("Entities")
        .description("Entities to run AntiAim on")
        .defaultValue(Set.of(EntityType.PLAYER))
        .build()
    );
    private Setting<Double> checkRange = sgGeneral.add(new DoubleSetting.Builder()
        .name("Check Range")
        .description("Entities must be in this range to be checked")
        .defaultValue(7)
        .sliderRange(0, 20)
        .build()
    );
    private Setting<Integer> attackRange = sgGeneral.add(new IntSetting.Builder()
        .name("Attack Reach")
        .description("Player will teleport away if the entity is looking at it in this range")
        .defaultValue(4)
        .sliderRange(0, 20)
        .build()
    );
    // Delay
    // Min teleport range
    // Max teleport range
    // Look at after teleporting

    @EventHandler
    private void onTick(TickEvent.Post event) {
        for(Entity entity : mc.world.getEntities()){
            if(entity == mc.player) continue;
            if(!entity.isInRange(mc.player, checkRange.get())) continue;
            if(!entities.get().contains(entity.getType())) continue;
            for(int i = 2; i < attackRange.get(); i++){
                HitResult result = entity.raycast(i, 0, false);
                if(result == null) continue;
                if(!result.getPos().isInRange(mc.player.getPos().add(0, 1, 0), 2.5)) continue;
                /*Optional<BlockPos> newPos = BlockPos.findClosest(mc.player.getBlockPos(), 3, 3, blockPos ->
                    !mc.world.getBlockState(blockPos).isSolid() &&
                    !mc.world.getBlockState(blockPos.add(0,1,0)).isSolid() &&
                    !blockPos.add(0,1,0).isWithinDistance(result.getPos(), 2.5)
                );*/

                /*Optional<BlockPos> newPos = BlockPos.findClosest(
                    mc.player.getBlockPos(), 3, 3, blockPos -> {
                    Vec3d offset = Vec3d.ofCenter(blockPos).subtract(mc.player.getPos()).normalize();
                    return offset.dotProduct(result.getPos().subtract(mc.player.getEyePos()).normalize()) > 0.3 &&
                        !mc.world.getBlockState(blockPos).isSolid() &&
                        !mc.world.getBlockState(blockPos.up()).isSolid();
                    }
                );*/

                //if(!newPos.isPresent()) continue;
                //mc.player.setPosition(newPos.get().getX()+0.5, newPos.get().getY(), newPos.get().getZ()+0.5);
                mc.player.setPosition(Vec3d.of(entity.getBlockPos().offset(entity.getHorizontalFacing().getOpposite())));
                //mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, entity.getPos().add(0,1.65,0));
            }
        }
    }
}
