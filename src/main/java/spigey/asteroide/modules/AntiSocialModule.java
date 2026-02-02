package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.world.CollisionShapeEvent;
import meteordevelopment.meteorclient.settings.EntityTypeListSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;
import spigey.asteroide.AsteroideAddon;

import java.util.Set;

public class AntiSocialModule extends Module {
    public AntiSocialModule() { super(AsteroideAddon.CATEGORY, "Antisocial", "Prevents you from going near specified entities."); }
    private SettingGroup sgGeneral = settings.getDefaultGroup();
    private Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("Entities")
        .description("Entities to prevent the player getting close to")
        .defaultValue(Set.of(net.minecraft.entity.EntityType.PLAYER))
        .build()
    );
    private Setting<Integer> distance = sgGeneral.add(new IntSetting.Builder()
        .name("Distance")
        .description("Distance in blocks to block movement in")
        .defaultValue(5)
        .build()
    );

    @EventHandler
    private void onCollisionShape(CollisionShapeEvent event) {
        for(Entity entity : mc.world.getEntities()){
            if(!entities.get().contains(entity.getType())) continue;
            if(mc.player == entity) continue;
            if(entity.squaredDistanceTo(Vec3d.ofCenter(event.pos)) > distance.get() * distance.get()) continue;
            event.shape = VoxelShapes.fullCube();
            //mc.world.setBlockState(event.pos, Blocks.RED_STAINED_GLASS.getDefaultState());
        }
    }
}
