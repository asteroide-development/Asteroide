package spigey.asteroide.modules;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.orbit.EventPriority;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.network.packet.s2c.play.ChunkDataS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import spigey.asteroide.AsteroideAddon;

import java.util.*;

public class AntiAntiXrayModule extends Module {
    public AntiAntiXrayModule() { super(AsteroideAddon.CATEGORY, "Anti-Anti-Xray", "Attempts to bypass Anti Xray plugins by replacing fake layers of ores. Make sure to also enable Xray."); }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<List<Block>> blocksToReplace = sgGeneral.add(new BlockListSetting.Builder()
        .name("Blocks")
        .description("Blocks to replace")
        .defaultValue(List.of(Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE, Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE, Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE, Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE, Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE, Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE, Blocks.NETHER_GOLD_ORE, Blocks.NETHER_QUARTZ_ORE, Blocks.ANCIENT_DEBRIS))
        .build()
    );

    private final Setting<Block> replacement = sgGeneral.add(new BlockSetting.Builder()
        .name("Replacement")
        .description("Block to replace the invalid xray blocks with")
        .defaultValue(Blocks.STONE)
        .build()
    );

    private final Setting<Integer> chunksPerTick = sgGeneral.add(new IntSetting.Builder()
        .name("Chunks per Tick")
        .description("Amount of chunks to process per tick")
        .defaultValue(3)
        .sliderRange(1, 10)
        .build()
    );

    private final Setting<Integer> minVeinSize = sgGeneral.add(new IntSetting.Builder()
        .name("Min vein size")
        .description("Minimum amount of blocks in a vein to consider deleting it")
        .defaultValue(4)
        .sliderRange(0, 80)
        .build()
    );

    private final Setting<Integer> veinSizeThreshold = sgGeneral.add(new IntSetting.Builder()
        .name("Max vein size")
        .description("Maximum amount of blocks in a vein before deleting it")
        .defaultValue(60)
        .sliderRange(1, 80)
        .build()
    );

    private final Setting<Boolean> heightCheck = sgGeneral.add(new BoolSetting.Builder()
        .name("Height Check")
        .description("Removes ores at heights they shouldn't be")
        .defaultValue(true)
        .build()
    );

    private List<String> chunkQueue = new ArrayList<>();
    Set<BlockPos> totalVisited = new HashSet<>();
    private int tick = 0;

    @EventHandler(priority = -2147483647)
    private void onPacketReceive(PacketEvent.Receive event) throws InterruptedException {
        if(!(event.packet instanceof ChunkDataS2CPacket packet)) return;
        this.chunkQueue.add(String.format("%d,%d", packet.getChunkX(), packet.getChunkZ())); // LMAOO
        this.tick = 3;
    }

    @EventHandler
    private void onTick(TickEvent.Post event){
        if(this.tick > 0) { this.tick--; return; }
        if(this.chunkQueue.isEmpty()) return;
        for(int i = 0; i < chunksPerTick.get(); i++){
            if(this.chunkQueue.isEmpty()) return;
            totalVisited.clear();
            String chunk = this.chunkQueue.getFirst(); this.chunkQueue.removeFirst();
            int[] coords = { Integer.parseInt(chunk.split(",")[0]), Integer.parseInt(chunk.split(",")[1]) };
            for(int y = mc.world.getBottomY(); y < mc.world.getTopYInclusive(); y++){ for(int x = 0; x < 16; x++){ for(int z = 0; z < 16; z++){
                BlockPos pos = new BlockPos(coords[0] * 16 + x, y, coords[1] * 16 + z);
                if(!mc.world.isPosLoaded(pos)) continue;
                Block block = mc.world.getBlockState(pos).getBlock();
                if(blocksToReplace.get().contains(block)) {
                    boolean changed = false;
                    if(heightCheck.get()){
                        Map<Block, List<Integer>> mappings = Map.ofEntries(
                            Map.entry(Blocks.COAL_ORE, List.of(-64, -1)),
                            Map.entry(Blocks.COPPER_ORE, List.of(-64, -17)), // (113, 320) too, copper really wants to be special
                            Map.entry(Blocks.LAPIS_ORE, List.of(65, 320)),
                            Map.entry(Blocks.IRON_ORE, List.of(73, 79)),
                            Map.entry(Blocks.GOLD_ORE, List.of(257, 320)),
                            Map.entry(Blocks.REDSTONE_ORE, List.of(16, 320)),
                            Map.entry(Blocks.DIAMOND_ORE, List.of(16, 320)),
                            Map.entry(Blocks.EMERALD_ORE, List.of(-64, -17)),
                            Map.entry(Blocks.NETHER_GOLD_ORE, List.of(117, 320)),
                            Map.entry(Blocks.ANCIENT_DEBRIS, List.of(120, 320))
                        );
                        List<Integer> map = mappings.getOrDefault(block, List.of(321, 321));
                        if(pos.getY() < -60 || ((map.get(0) <= pos.getY() && map.get(1) >= pos.getY()) || (Registries.BLOCK.getId(block).toString().endsWith("_ore") && (pos.getY() > 8 && isDeepslate(block)) || (pos.getY() < -8 && !isDeepslate(block))))) { changed = true; mc.world.setBlockState(pos, replacement.get().getDefaultState()); }
                    }
                    if(!changed) checkBlocks(pos);
                }
            }}}
        }
    }

    private boolean isDeepslate(Block block){
        String id = Registries.BLOCK.getId(block).toString();
        return id.startsWith("minecraft:deepslate_") && id.endsWith("_ore"); // Eh
    }

    private void checkBlocks(BlockPos pos){
        if(totalVisited.contains(pos)) return;
        ArrayDeque<BlockPos> stack = new ArrayDeque<>();
        BlockState state = mc.world.getBlockState(pos);
        Set<BlockPos> visited = new HashSet<>();
        stack.add(pos);

        while(!stack.isEmpty()){
            BlockPos p = stack.pop();
            if(!visited.add(p)) continue;
            //if(!totalVisited.add(p)) continue;
            if(!mc.world.getBlockState(p).getBlock().equals(state.getBlock())) { visited.remove(p); continue; }
            stack.add(p.north());
            stack.add(p.south());
            stack.add(p.east());
            stack.add(p.west());
            stack.add(p.up());
            stack.add(p.down());
        }

        boolean sameY = true;
        if(visited.size() <= minVeinSize.get()) return;
        for(BlockPos p : visited) if(p.getY() != pos.getY()){ sameY = false; break; }
        if(sameY || visited.size() >= veinSizeThreshold.get()) for(BlockPos p : visited) mc.world.setBlockState(p, replacement.get().getDefaultState(), Block.NOTIFY_LISTENERS);
    }
}
