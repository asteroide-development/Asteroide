package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.config.Config;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import spigey.asteroide.AsteroideAddon;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

import java.util.Comparator;
import java.util.Random;
import java.util.Set;

public class AimbotModule extends Module {
    public AimbotModule() {
        super(AsteroideAddon.CATEGORY, "Aimbot", "Self explanatory.");
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("Range in which entities have to be to be targeted.")
        .defaultValue(5)
        .min(0.1)
        .max(500)
        .sliderMax(5)
        .build()
    );
    private final Setting<Set<EntityType<?>>> entities = sgGeneral.add(new EntityTypeListSetting.Builder()
        .name("entities")
        .description("Entities to target.")
        .onlyAttackable()
        .defaultValue(EntityType.PLAYER)
        .build()
    );
    private final Setting<Weapon> weapon = sgGeneral.add(new EnumSetting.Builder<Weapon>()
        .name("weapon")
        .description("Only targets an entity when a specified weapon is in your hand.")
        .defaultValue(Weapon.Both)
        .build()
    );
    private final Setting<Boolean> allowUp = sgGeneral.add(new BoolSetting.Builder()
        .name("Allow changing Y")
        .description("Allows you to look up/down")
        .defaultValue(false)
        .build()
    );
    private final Setting<Integer> percent = sgGeneral.add(new IntSetting.Builder()
        .name("Height Percent")
        .description("Entity Height to Focus at in Percent")
        .defaultValue(75)
        .sliderMin(1)
        .sliderMax(100)
        .visible(() -> !allowUp.get())
        .build()
    );

    private final Setting<Boolean> targetFriends = sgGeneral.add(new BoolSetting.Builder().name("target friends").description("Also targets friends when enabled.").defaultValue(false).build());
    // this nigga shit so fucking skidded 💔


    @EventHandler
    public void onRender(Render3DEvent event) {
        Entity entity = getEntity();
        if (entity == null) return;
        if(!itemInHand()) return;
        double[] geminiwtf = {entity.getX(), entity.getY(), entity.getZ()};
        assert mc.player != null;
        if(allowUp.get()) {
            float nigger = MathHelper.wrapDegrees((float) (-Math.toDegrees(Math.atan2(geminiwtf[0] - mc.player.getX(), geminiwtf[2] - mc.player.getZ()))));
            mc.player.setHeadYaw(nigger);
            mc.player.setYaw(nigger);
        } else {
            mc.player.lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(geminiwtf[0], entity.getY() + (entity.getHeight() * (percent.get() / 100.0)), geminiwtf[2]));
        }
    }

    private Entity getEntity(){ // AI generated cuz suicidal thoughts
        Entity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;
        for (Entity e : mc.world.getEntities()) {
            if (e != mc.player && e.isAlive() && mc.player.distanceTo(e) <= range.get() && entities.get().contains(e.getType()) && (!(e instanceof PlayerEntity) || (Friends.get().shouldAttack((PlayerEntity) e) || targetFriends.get()))) {
                double distance = mc.player.distanceTo(e);
                if (distance < closestDistance) {
                    closestEntity = e;
                    closestDistance = distance;
                }
            }
        }
        return closestEntity;
    }

    private enum Weapon {
        Sword,
        Axe,
        Both,
        All
    }

    private boolean itemInHand() {
        return switch (weapon.get()) {
            case Axe -> mc.player.getMainHandStack().getItem() instanceof AxeItem;
            case Sword -> mc.player.getMainHandStack().getItem() instanceof SwordItem;
            case Both -> mc.player.getMainHandStack().getItem() instanceof AxeItem || mc.player.getMainHandStack().getItem() instanceof SwordItem;
            case All -> true;
        };
    }
}
