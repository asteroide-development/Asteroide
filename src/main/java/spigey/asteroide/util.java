package spigey.asteroide;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;

import java.util.Objects;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class util {
    public static String perm(int lvl){
        return "You do not have the required permission level of " + lvl + ", the command will most likely not work!";
    }
    public static void msg(String message) {
        ChatUtils.sendPlayerMsg(message);
    }
    public static ItemStack itemstack(Item temp){
        return new ItemStack(temp);
    }
    public static boolean give(ItemStack ItemToGive, String ItemNBT) throws CommandSyntaxException {
        assert mc.player != null;
        ItemToGive.setNbt(StringNbtReader.parse(ItemNBT));
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new CreativeInventoryActionC2SPacket(36 + mc.player.getInventory().selectedSlot, ItemToGive));
        return true;
    }
    public static boolean give(ItemStack ItemToGive) throws CommandSyntaxException {
        assert mc.player != null;
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new CreativeInventoryActionC2SPacket(36 + mc.player.getInventory().selectedSlot, ItemToGive));
        return true;
    }
    public static int getPermissionLevel(){
        assert mc.player != null;
        int perm = 0;
        if(mc.player.hasPermissionLevel(4)){
            perm = 4;
        } else if(mc.player.hasPermissionLevel(3)){
            perm = 3;
        } else if(mc.player.hasPermissionLevel(2)){
            perm = 2;
        } else if(mc.player.hasPermissionLevel(1)){
            perm = 1;
        } else if(mc.player.hasPermissionLevel(0)){
            perm = 0;
        }
        return perm;
    }
    public static void addModule(meteordevelopment.meteorclient.systems.modules.Module ModuleToAdd){
        Modules.get().add(ModuleToAdd);
    }
    public static void addCommand(Command CommandToAdd){
        Commands.add(CommandToAdd);
    }
    public static void addHud(HudElementInfo HudToAdd){
        Hud.get().register(HudToAdd);
    }
    public static void CommandBlock(Item CommandBlockToGive, String Command, int AlwaysActive) throws CommandSyntaxException {
        if(!CommandBlockToGive.toString().toUpperCase().contains("COMMAND_BLOCK")){return;}    // Only allow Command Blocks
        String nbt = "{BlockEntityTag:{Command:\"" + Command + "\",auto:" + AlwaysActive + "b},HideFlags:127}"; // idfk nbt
        give(itemstack(CommandBlockToGive), nbt);
    }
}
