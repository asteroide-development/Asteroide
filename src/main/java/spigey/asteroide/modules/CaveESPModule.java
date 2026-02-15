package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import spigey.asteroide.AsteroideAddon;

import java.util.*;

public class CaveESPModule extends Module {
    public CaveESPModule() { super(AsteroideAddon.CATEGORY, "Cave-ESP", "Displays large caves below you. Useful for digging down."); }

    private SettingGroup sgGeneral = settings.getDefaultGroup();
    private Setting<Integer> radius = sgGeneral.add(new IntSetting.Builder()
        .name("radius")
        .description("Radius to look for caves in in blocks.")
        .sliderRange(0, 32)
        .defaultValue(1)
        .build()
    );
    private Setting<Integer> minY = sgGeneral.add(new IntSetting.Builder()
        .name("Min Height")
        .description("Minimum height to count a room as a cave.")
        .defaultValue(5)
        .sliderRange(1, 30)
        .build()
    );
    private Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color")
        .description("Color to render.")
        .defaultValue(new SettingColor(255, 0, 0, 100))
        .build()
    );

    private List<Map<BlockPos, Integer>> poses = new ArrayList<>();

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(!isActive()) return;
        poses.clear();
        List<String> blacklisted = new ArrayList<>();
        Entity cam = mc.getCameraEntity();
        for(int x = -radius.get(); x <= radius.get(); x++){ for(int z = -radius.get(); z <= radius.get(); z++){ for(int y = cam.getBlockY()-1;y > -65;y--){
            BlockPos pos = new BlockPos(cam.getBlockX()+x, y, cam.getBlockZ()+z);
            if(blacklisted.contains(String.format("%s,%s", pos.getX(), pos.getZ()))) continue;
            if(!mc.world.getBlockState(pos).isTransparent() || mc.world.getBlockState(pos.add(0,1,0)).isTransparent()) continue;
            int height = getHeight(pos);
            if(height < 3) continue;
            blacklisted.add(String.format("%s,%s", pos.getX(), pos.getZ()));
            poses.add(Map.of(pos, height));
        }}}
    }

    @EventHandler
    private void render3d(Render3DEvent event){
        for(int i = 0; i < poses.size(); i++){
            Map info = poses.get(i);
            BlockPos pos = (BlockPos) info.keySet().toArray()[0]; // ??????????????????
            Color c = color.get();
            event.renderer.sideHorizontal(pos.getX(), pos.getY()+1, pos.getZ(), pos.getX()+1, pos.getZ()+1, new SettingColor(c.r,c.g,c.b,
                c.a + ((int) info.values().toArray()[0]) * 5) // I'm sorry
                , null, ShapeMode.Sides);
        }
    }

    private int getHeight(BlockPos pos){ // Spaghetti, kind of
        int height = 0;
        while(mc.world.getBlockState(pos).isTransparent()){
            height++;
            pos = pos.add(0, -1, 0);
        }
        return height;
    }
}
