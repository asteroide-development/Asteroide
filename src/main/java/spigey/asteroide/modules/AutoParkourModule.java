package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import spigey.asteroide.AsteroideAddon;

import java.util.ArrayList;
import java.util.List;

import static com.nimbusds.openid.connect.sdk.assurance.claims.ISO3166_1Alpha2CountryCode.GL;

public class AutoParkourModule extends Module {
    public AutoParkourModule() {
        super(AsteroideAddon.CATEGORY, "Parkour-Bot", "Automatically does parkour for you by teleporting.");
    }

    private final SettingGroup sgGeneral = settings.createGroup("General");
    private final SettingGroup sgRange = settings.createGroup("Range");
    private final SettingGroup sgOffset = settings.createGroup("Offset");
    private final Setting<Boolean> flightKick = sgGeneral.add(new BoolSetting.Builder()
        .name("Anti Flight Kick")
        .description("Attempts to prevent getting kicked for flying.")
        .defaultValue(true)
        .build()
    );
    private final Setting<List<Block>> blocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("Blocks")
        .description("Blocks to teleport to.")
        .defaultValue(List.of(Blocks.ANDESITE))
        .build()
    );
    private final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("Mode")
        .description("Whitelist, Blacklist or All blocks.")
        .defaultValue(Mode.Whitelist)
        .build()
    );
    private final Setting<SortMode> sortMode = sgGeneral.add(new EnumSetting.Builder<SortMode>()
        .name("Sort Mode")
        .description("Whitelist, Blacklist or All blocks.")
        .defaultValue(SortMode.Nearest)
        .build()
    );
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("Delay")
        .description("Delay between teleports in ticks.")
        .defaultValue(5)
        .min(0)
        .sliderMax(20)
        .build()
    );

    private final Setting<Integer> rangeX = sgRange.add(new IntSetting.Builder()
        .name("Range XZ")
        .description("Range in the X and Z direction.")
        .defaultValue(3)
        .min(0)
        .sliderMax(20)
        .build()
    );
    private final Setting<Integer> minY = sgRange.add(new IntSetting.Builder()
        .name("Min Y")
        .description("Minimum Y height difference.")
        .defaultValue(-3)
        .sliderMin(-5)
        .sliderMax(3)
        .build()
    );
    private final Setting<Integer> maxY = sgRange.add(new IntSetting.Builder()
        .name("Max Y")
        .description("Maximum Y height difference.")
        .defaultValue(1)
        .sliderMin(-4)
        .sliderMax(4)
        .build()
    );


    private final Setting<Double> offsetX = sgOffset.add(new DoubleSetting.Builder()
        .name("X")
        .description("X Offset")
        .defaultValue(0.5)
        .sliderMin(-2)
        .sliderMax(2)
        .build()
    );
    private final Setting<Double> offsetY = sgOffset.add(new DoubleSetting.Builder()
        .name("Y")
        .description("Y Offset")
        .defaultValue(1)
        .sliderMin(-2)
        .sliderMax(2)
        .build()
    );
    private final Setting<Double> offsetZ = sgOffset.add(new DoubleSetting.Builder()
        .name("Z")
        .description("Z Offset")
        .defaultValue(0.5)
        .sliderMin(-2)
        .sliderMax(2)
        .build()
    );

    private int tick = 0;
    private List<BlockPos> blocked = new ArrayList<>();
    private enum Mode { Whitelist, Blacklist, All }
    private enum SortMode { Nearest, Furthest, Any, Random }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(flightKick.get() && !mc.player.isOnGround()){
            BlockPos pos = BlockPos.findClosest(mc.player.getBlockPos(), rangeX.get()*2, rangeX.get()*2, blockPos -> mc.world.getBlockState(blockPos).isSolid()).orElse(mc.player.getBlockPos());
            mc.player.setPosition(pos.getX()+0.5, pos.getY()+1, pos.getZ()+0.5);
            blocked.clear();
        }
        if(this.tick > 0){ this.tick--; return; }
        if(maxY.get() < minY.get()) maxY.set(minY.get());
        List<BlockPos> candidates = new ArrayList<>();
        for(int x = -rangeX.get(); x <= rangeX.get(); x++){ for(int z = -rangeX.get(); z <= rangeX.get(); z++){ for(int y = maxY.get(); y >= minY.get(); y--){
            int[] pos = {(int) Math.floor(mc.player.getX())+x, (int) Math.floor(mc.player.getY())+y, (int) Math.floor(mc.player.getZ())+z};
            BlockPos posi = new BlockPos(pos[0], pos[1], pos[2]);
            if((x == 0 && z == 0) || !canMove(posi)) continue;
            candidates.add(posi);
        }}}
        if(candidates.isEmpty()) return;
        if(sortMode.get() == SortMode.Any) { candidates = new ArrayList<>(List.of(candidates.get(0))); }
        else if(sortMode.get() == SortMode.Random){ java.util.Collections.shuffle(candidates); }
        candidates.sort((a, b) -> {
            Vec3d p = mc.player.getPos();
            return Double.compare(p.squaredDistanceTo(new Vec3d(a.getX(), a.getY(), a.getZ())), p.squaredDistanceTo(new Vec3d(b.getX(), b.getY(), b.getZ())));
        });
        if(sortMode.get() == SortMode.Furthest) java.util.Collections.reverse(candidates);

        blocked.clear();
        blocked.add(mc.player.getBlockPos());
        blocked.add(candidates.get(0));
        mc.player.setPosition(candidates.get(0).getX() + offsetX.get(), candidates.get(0).getY() + offsetY.get(), candidates.get(0).getZ() + offsetZ.get());
        this.tick = delay.get();
    }

    private boolean canMove(BlockPos to){
        BlockState block = mc.world.getBlockState(to);
        if(blocks.get().contains(block.getBlock()) && mode.get() == Mode.Blacklist) return false;
        for(BlockPos pos : blocked) if(pos.getX() == to.getX() && pos.getZ() == to.getZ()) return false;
        return (mode.get() == Mode.All || (mode.get() == Mode.Whitelist) == blocks.get().contains(block.getBlock())) && !block.isAir();
    }
}
