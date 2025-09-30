package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import org.joml.Vector3f;
import spigey.asteroide.AsteroideAddon;

import java.util.*;

public class MurderMysteryESP extends Module {
    public MurderMysteryESP() { super(AsteroideAddon.CATEGORY, "Murder-Mystery-ESP", "ESP for Hypixel Murder Mystery."); }

    private final SettingGroup sgGeneral = settings.createGroup("General", true);
    private final SettingGroup sgBow = settings.createGroup("Bow", true);
    private final SettingGroup sgMurder = settings.createGroup("Murderer", true);
    private final SettingGroup sgDetective = settings.createGroup("Detective", true);
    private final SettingGroup sgInnocent = settings.createGroup("Innocent", true);

    private final Setting<Boolean> ignoreSelf = sgGeneral.add(new BoolSetting.Builder()
        .name("Ignore Self")
        .description("Does not apply ESP to yourself.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> itemEsp = sgGeneral.add(new BoolSetting.Builder()
        .name("Item ESP")
        .description("Item ESP.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> itemColor = sgGeneral.add(new ColorSetting.Builder()
        .name("Item ESP Color")
        .description("ESP Color of the Item")
        .defaultValue(new SettingColor(252, 186, 3, 130))
        .build()
    );

    private final Setting<Boolean> bowEsp = sgBow.add(new BoolSetting.Builder()
        .name("Bow ESP")
        .description("Bow ESP.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> bowTracers = sgBow.add(new BoolSetting.Builder()
        .name("Bow Tracers")
        .description("Bow Tracers.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> bowColor = sgBow.add(new ColorSetting.Builder()
        .name("Bow ESP Color")
        .description("ESP Color of the bow")
        .defaultValue(new SettingColor(50, 168, 82, 100))
        .build()
    );

    private final Setting<Boolean> murdESP = sgMurder.add(new BoolSetting.Builder()
        .name("Murderer ESP")
        .description("Renders ESP on the murderer")
        .defaultValue(true)
        .build()
    );

    private final Setting<List<Item>> murdItems = sgMurder.add(new ItemListSetting.Builder()
        .name("Murderer Items")
        .description("Items to detect the murderer.")
        .defaultValue(List.of(
            Items.IRON_SWORD, Items.STONE_SWORD, Items.IRON_SHOVEL, Items.STICK, Items.WOODEN_AXE,
            Items.WOODEN_SWORD, Items.DEAD_BUSH, Items.SUGAR_CANE, Items.STONE_SHOVEL, Items.BLAZE_ROD, Items.DIAMOND_SHOVEL, Items.QUARTZ,
            Items.PUMPKIN_PIE, Items.GOLDEN_PICKAXE, Items.LEATHER, Items.NAME_TAG, Items.CHARCOAL, Items.FLINT, Items.BONE,
            Items.CARROT, Items.GOLDEN_CARROT, Items.COOKIE, Items.DIAMOND_AXE, Items.ROSE_BUSH, Items.PRISMARINE_SHARD, Items.COOKED_BEEF,
            Items.NETHER_BRICK, Items.COOKED_CHICKEN, Items.MUSIC_DISC_BLOCKS, Items.GOLDEN_HOE, Items.LAPIS_LAZULI, Items.GOLDEN_SWORD, Items.DIAMOND_SWORD,
            Items.DIAMOND_HOE, Items.SHEARS, Items.SALMON, Items.RED_DYE, Items.BREAD, Items.OAK_BOAT, Items.GLISTERING_MELON_SLICE,
            Items.BOOK, Items.JUNGLE_SAPLING, Items.GOLDEN_AXE, Items.DIAMOND_PICKAXE, Items.GOLDEN_SHOVEL
        ))
        .build()
    );

    private final Setting<SettingColor> murdColor = sgMurder.add(new ColorSetting.Builder()
        .name("Murderer ESP Color")
        .description("ESP Color of the murderer")
        .defaultValue(new SettingColor(201, 26, 26, 100))
        .build()
    );

    private final Setting<Boolean> murdTracers = sgMurder.add(new BoolSetting.Builder()
        .name("Murderer Tracers")
        .description("Tracers for Murderer")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> murdLog = sgMurder.add(new BoolSetting.Builder()
        .name("Murderer Log")
        .description("Log the murderer in the chat if found")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> decESP = sgDetective.add(new BoolSetting.Builder()
        .name("Detective ESP")
        .description("Renders ESP on the detective")
        .defaultValue(true)
        .build()
    );

    private final Setting<List<Item>> decItems = sgDetective.add(new ItemListSetting.Builder()
        .name("Detective Items")
        .description("Items to detect the detective.")
        .defaultValue(List.of(Items.BOW, Items.ARROW))
        .build()
    );

    private final Setting<SettingColor> decColor = sgDetective.add(new ColorSetting.Builder()
        .name("Detective ESP Color")
        .description("ESP Color of the detective")
        .defaultValue(new SettingColor(21, 128, 209, 100))
        .build()
    );

    private final Setting<Boolean> decTracers = sgDetective.add(new BoolSetting.Builder()
        .name("Detective Tracers")
        .description("Tracers for detective")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> decLog = sgDetective.add(new BoolSetting.Builder()
        .name("Detective Log")
        .description("Log the detective in the chat if found")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> innESP = sgInnocent.add(new BoolSetting.Builder()
        .name("Innocent ESP")
        .description("Renders ESP on the innocents")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> innColor = sgInnocent.add(new ColorSetting.Builder()
        .name("Innocent ESP Color")
        .description("ESP Color of the innocent")
        .defaultValue(new SettingColor(255, 255, 255, 100))
        .build()
    );

    private final Setting<Boolean> innTracers = sgInnocent.add(new BoolSetting.Builder()
        .name("Innocent Tracers")
        .description("Tracers for innocent")
        .defaultValue(false)
        .build()
    );

    private Set<String> murderers = new HashSet<>();
    private Set<String> detectives = new HashSet<>();
    private Set<String> found = new HashSet<>();

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(mc.player.getInventory().getStack(8).getItem() == Items.RED_BED) { murderers.clear(); detectives.clear(); found.clear(); } // lmao
        for(Entity entity : mc.world.getEntities()){
            if(entity == mc.player && ignoreSelf.get()) continue;
            if(!(entity instanceof PlayerEntity)) continue;
            String player = ((PlayerEntity) entity).getGameProfile().getName();
            Item main = ((PlayerEntity) entity).getMainHandStack().getItem();
            boolean murd = murdItems.get().contains(main); boolean dec = decItems.get().contains(main);
            if(murd) { murderers.add(player); detectives.remove(player); }
            if(dec) detectives.add(player);
            if(murd || dec){
                if(murd && murdLog.get() && !found.contains(player) && mc.player.getInventory().getStack(8).getItem() != Items.RED_BED) info(String.format("§c%s§7 is holding §c%s§7!", player, main.getName().getString()));
                if(dec && decLog.get() && !found.contains(player) && mc.player.getInventory().getStack(8).getItem() != Items.RED_BED) info(String.format("§b%s§7 is holding §b%s§7!", player, main.getName().getString()));
                found.add(player);
            }
        }
    }

    @EventHandler
    private void onRender3D(Render3DEvent event){
        for(Entity entity : mc.world.getEntities()){
            if(entity instanceof ArmorStandEntity && ((ArmorStandEntity)entity).getEquippedStack(EquipmentSlot.MAINHAND).getItem() instanceof BowItem && (bowEsp.get() || bowTracers.get())) bowEsp(event, entity);
            if(entity instanceof ItemEntity && ((ItemEntity) entity).getStack().getItem() == Items.GOLD_INGOT && itemEsp.get()) itemEsp(event, entity);
            if(entity == mc.player && ignoreSelf.get()) continue;
            if(!(entity instanceof PlayerEntity)) continue;
            String player = ((PlayerEntity) entity).getGameProfile().getName();
            tracers(event, entity);
            drawBoundingBox(event, entity);
        }
    }

    private void itemEsp(Render3DEvent event, Entity entity) {
        double x = entity.getX() - 0.2;
        double y = entity.getY();
        double z = entity.getZ() - 0.2;
        event.renderer.box(x, y, z, x + 0.4, y + 0.4, z + 0.4, itemColor.get(), null, ShapeMode.Sides, 0);
    }

    private void bowEsp(Render3DEvent event, Entity entity) {
        double x = entity.getX() - 0.32;
        double y = entity.getY() + 0.3;
        double z = entity.getZ() - 0.25;
        if(bowEsp.get()) event.renderer.box(x, y, z, x + 1, y + 1, z + 1, bowColor.get(), null, ShapeMode.Sides, 0);
        if(bowTracers.get()) event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, x, y, z, new Color(bowColor.get()).a(255));
    }

    private void drawBoundingBox(Render3DEvent event, Entity entity) {
        Role role = getRole(((PlayerEntity) entity).getGameProfile().getName());
        if(role == Role.Murderer && !murdESP.get()) return;
        if(role == Role.Detective && !decESP.get()) return;
        if(role == Role.Innocent && !innESP.get()) return;

        Color color = getColor(role);
        if(color == null) return;

        double x = MathHelper.lerp(event.tickDelta, entity.lastRenderX, entity.getX()) - entity.getX();
        double y = MathHelper.lerp(event.tickDelta, entity.lastRenderY, entity.getY()) - entity.getY();
        double z = MathHelper.lerp(event.tickDelta, entity.lastRenderZ, entity.getZ()) - entity.getZ();

        Box box = entity.getBoundingBox();
        event.renderer.box(x + box.minX, y + box.minY, z + box.minZ, x + box.maxX, y + box.maxY, z + box.maxZ, color, null, ShapeMode.Sides, 0);
    }

    private void tracers(Render3DEvent event, Entity entity){
        Role role = getRole(((PlayerEntity) entity).getGameProfile().getName());
        if(role == Role.Murderer && !murdTracers.get()) return;
        if(role == Role.Detective && !decTracers.get()) return;
        if(role == Role.Innocent && !innTracers.get()) return;

        Color color = getColor(role);
        if(color == null) return;

        double x = entity.prevX + (entity.getX() - entity.prevX) * event.tickDelta;
        double y = entity.prevY + (entity.getY() - entity.prevY) * event.tickDelta;
        double z = entity.prevZ + (entity.getZ() - entity.prevZ) * event.tickDelta;

        event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, x, y, z, new Color(color).a(255));
    }

    private Role getRole(String name){
        if(murderers.contains(name)) return Role.Murderer;
        else if(detectives.contains(name)) return Role.Detective;
        else return Role.Innocent;
    }

    private Color getColor(Role role){
        return
            role == Role.Murderer ? murdColor.get() :
            role == Role.Detective ? decColor.get() :
            role == Role.Innocent ? innColor.get() :
            null;
    }

    private enum Role{
        Murderer,
        Detective,
        Innocent
    }

    private enum Action {
        Slot_0,
        Attack,
        Slot_1
    }
}
