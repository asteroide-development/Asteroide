package spigey.asteroide;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.misc.text.MeteorClickEvent;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import spigey.asteroide.modules.BanStuffs;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.player.ChatUtils.error;
import static meteordevelopment.meteorclient.utils.player.ChatUtils.info;

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
    /*public static boolean give(ItemStack ItemToGive, String ItemNBT) throws CommandSyntaxException {
        assert mc.player != null;
        ItemToGive.setNbt(StringNbtReader.parse(ItemNBT));
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new CreativeInventoryActionC2SPacket(36 + mc.player.getInventory().selectedSlot, ItemToGive));
        return true;
    }
    public static boolean give(ItemStack ItemToGive) throws CommandSyntaxException {
        assert mc.player != null;
        Objects.requireNonNull(mc.getNetworkHandler()).sendPacket(new CreativeInventoryActionC2SPacket(36 + mc.player.getInventory().selectedSlot, ItemToGive));
        return true;
    }*/
    public static int getPermissionLevel(){
        assert mc.player != null;
        int perm = 0;
        while(mc.player.hasPermissionLevel(perm)){
            perm++;
        }
        return perm - 1;
    }
    public static void addModule(meteordevelopment.meteorclient.systems.modules.Module ModuleToAdd){
        Modules.get().add(ModuleToAdd);
    }
    public static void addCommand(Command CommandToAdd){
        Commands.add(CommandToAdd);
    }
    public static void addHud(HudElementInfo HudToAdd){
        Hud.get().register(HudToAdd);
    } // {display:{Name:'["",{"text":"COMMAND","color":"dark_gray"}]'},Enchantments:[{lvl:1,id:infinity}],HideFlags:1}
    /*public static void CommandBlock(Item CommandBlockToGive, String Command, int AlwaysActive, String DisplayName, boolean Enchanted) throws CommandSyntaxException {
        if(!CommandBlockToGive.toString().toUpperCase().contains("COMMAND_BLOCK")){return;}    // Only allow Command Blocks
        Command = Command.replaceAll("\"", "\\\\\\\"");
        DisplayName = DisplayName.replaceAll("\"", "\\\\\\\"");
        String nbt = "{display:{Name:'[\"\",{\"text\":\"" + DisplayName +"\",\"color\":\"gray\"}]'},BlockEntityTag:{Command:\"" + Command + "\",auto:" + AlwaysActive + "b},HideFlags:127"; // idfk nbt
        if(Enchanted){nbt += ",Enchantments:[{lvl:1,id:infinity}]";}
        nbt += "}";
        give(itemstack(CommandBlockToGive), nbt.replaceAll("\\\\\\\"", "\\\\\""));
    }
    public static void CommandBlock(Item CommandBlockToGive, String Command, int AlwaysActive, String[] DisplayName, String[] lore, boolean Enchanted) throws CommandSyntaxException {
        if(!CommandBlockToGive.toString().toUpperCase().contains("COMMAND_BLOCK")){return;}    // Only allow Command Blocks
        Command = Command.replaceAll("\"", "\\\\\\\"");
        DisplayName[0] = DisplayName[0].replaceAll("\"", "\\\\\\\"");
        String nbt = "{display:{Name:'[\"\",{\"text\":\"" + DisplayName[0] +"\",\"color\":\"" + DisplayName[1] +"\", \"italic\":" + DisplayName[2] + "}]', Lore:['[\"\",{\"text\":\"" + lore[0] +"\",\"italic\":" + lore[2] + ",\"color\":\"" + lore[1] + "\"}]']},BlockEntityTag:{Command:\"" + Command + "\",auto:" + AlwaysActive + "b},HideFlags:127"; // idfk nbt
        if(Enchanted){nbt += ",Enchantments:[{lvl:1,id:infinity}]";}
        nbt += "}";
        give(itemstack(CommandBlockToGive), nbt.replaceAll("\\\\\\\"", "\\\\\""));
    }
    public static void CommandBlock(Item CommandBlockToGive, String Command, int AlwaysActive, boolean Enchanted) throws CommandSyntaxException {
        if(!CommandBlockToGive.toString().toUpperCase().contains("COMMAND_BLOCK")){return;}    // Only allow Command Blocks
        Command = Command.replaceAll("\"", "\\\\\\\"");
        String nbt = "{BlockEntityTag:{Command:\"" + Command + "\",auto:" + AlwaysActive + "b},HideFlags:127}"; // idfk nbt
        if(Enchanted){
            nbt = "{BlockEntityTag:{Command:\\\"\" + Command + \"\\\",auto:\" + AlwaysActive + \"b},Enchantments:[{lvl:1,id:infinity}],HideFlags:127}";
        }
        give(itemstack(CommandBlockToGive), nbt.replaceAll("\\\\\\\"", "\\\\\""));
    }
    public static void CommandBlock(Item CommandBlockToGive, String Command, int AlwaysActive) throws CommandSyntaxException {
        CommandBlock(CommandBlockToGive, Command, AlwaysActive, false);
    }*/
    public static double meth(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)` | number
            //        | functionName `(` expression `)` | functionName factor
            //        | factor `^` factor

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if      (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if      (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return +parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // parentheses
                    x = parseExpression();
                    if (!eat(')')) throw new RuntimeException("Missing ')'");
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else if (ch >= 'a' && ch <= 'z') { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    if (eat('(')) {
                        x = parseExpression();
                        if (!eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
                    } else {
                        x = parseFactor();
                    }
                    if (func.equals("sqrt")) x = Math.sqrt(x);
                    else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
                    else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
                    else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
                    else throw new RuntimeException("Unknown function: " + func);
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }
    public static boolean[] append(boolean[] kys, boolean retard){ // wtf??
        boolean[] killyourself = new boolean[kys.length + 1];
        System.arraycopy(kys, 0, killyourself, 0, kys.length);
        killyourself[killyourself.length - 1] = retard;
        return killyourself;
    }
    public static void banstuff(){
        Module thing = Modules.get().get(BanStuffs.class);
        if(!thing.isActive()){thing.toggle();}
    }
    private static final Random random = new Random();
    public static int randomNum(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    public static boolean BoolContains(boolean[] ARRAY, boolean VALUE){
        boolean istfgiamsofuckingunmotivatedrnomgplsjustfuckingkillmelikeactuallyiwannakmsrn = false;
        for(int i = 0; i < ARRAY.length; i++){
            if(ARRAY[i] == VALUE){istfgiamsofuckingunmotivatedrnomgplsjustfuckingkillmelikeactuallyiwannakmsrn = true;}
        }
        return istfgiamsofuckingunmotivatedrnomgplsjustfuckingkillmelikeactuallyiwannakmsrn;
    }
    /*public static void MultiCommandBlock(List<String> commands){
        commands.add("setblock ~ ~1 ~ barrier");
        commands.add("summon minecraft:falling_block ~ ~2 ~ {BlockState:{Name:command_block},TileEntityData:{Command:'fill ~ ~ ~ ~ ~-4 ~ air',auto:1b}}");
        commands.add("kill @e[type=command_block_minecart,tag=oawiudoawiudoawidu]");
        StringBuilder out = getStringBuilder(commands);
        try {
            give(itemstack(Items.COMMAND_BLOCK), String.valueOf(out));
            // CommandBlock(Items.COMMAND_BLOCK, String.valueOf(out), 1, new String[]{"Multi-Command Command Block", "light_gray", "false"}, new String[]{trim(commands.get(0), commands.get(0).indexOf(" ")) + ", " + trim(commands.get(1), commands.get(1).indexOf(" ")) + ", " + trim(commands.get(2), commands.get(2).indexOf(" ")) + "...", "dark_gray", "true"}, true);
        } catch (CommandSyntaxException e) {
            error(String.valueOf(e));
            info(String.valueOf(out));
        }
    }

    private static @NotNull StringBuilder getStringBuilder(List<String> commands) {
        StringBuilder out = new StringBuilder("/summon falling_block ~ ~2 ~ {Time:1,BlockState:{Name:redstone_block}, Passengers:[{id:falling_block, Time:0, BlockState:{Name:activator_rail}");
        for (int i = 0; i < commands.size(); i++) {
            out.append(",Passengers:[{id:command_block_minecart, CustomName:'@', Tags:[\"oawiudoawiudoawidu\"], Command:\"").append(commands.get(i).replaceAll("\"", "\\\\\\\\\"")).append('"');
        }
        for (int i = 0; i < commands.size(); i++){
            out.append("}]");
        }
        // final String nbt = ",display:{Name:'[\"Multi-Command Command Block\"]',Lore:['[\"\",{\"text\":\"" + trim(commands.get(0), commands.get(0).indexOf(" ")) +"\",\"italic\":false,\"color\":\"gray\"},{\"text\":\", \",\"italic\":false,\"color\":\"dark_gray\"},{\"text\":\"" + trim(commands.get(1), commands.get(1).indexOf(" ")) +"\",\"italic\":false,\"color\":\"gray\"},{\"text\":\", \",\"italic\":false,\"color\":\"dark_gray\"},{\"text\":\"" + trim(commands.get(2), commands.get(2).indexOf(" ")) + "\",\"italic\":false,\"color\":\"gray\"},{\"text\":\"...\",\"italic\":false,\"color\":\"dark_gray\"}]','[\"\"]','[\"\",{\"text\":\"Generated by \",\"italic\":false,\"color\":\"dark_gray\"},{\"text\":\"" + mc.getSession().getUsername() +"\",\"italic\":false,\"color\":\"gray\"},{\"text\":\" with \",\"italic\":false,\"color\":\"dark_gray\"},{\"text\":\"Asteroide\",\"italic\":false,\"bold\":true,\"color\":\"red\"},{\"text\":\".\",\"italic\":false,\"color\":\"dark_gray\"}]']},Enchantments:[{lvl:1,id:infinity}],HideFlags:1}";
        out.append("}]}");
        final String nbt = "{display:{Lore:['[\"\",{\"text\":\"" + trim(commands.get(0), commands.get(0).indexOf(" ")) + "\",\"italic\":false,\"color\":\"gray\"},{\"text\":\", \",\"italic\":false,\"color\":\"dark_gray\"},{\"text\":\"" + trim(commands.get(1), commands.get(1).indexOf(" ")) + "\",\"italic\":false,\"color\":\"gray\"},{\"text\":\", \",\"italic\":false,\"color\":\"dark_gray\"},{\"text\":\"" + trim(commands.get(2), commands.get(2).indexOf(" ")) + "\",\"italic\":false,\"color\":\"gray\"},{\"text\":\"...\",\"italic\":false,\"color\":\"dark_gray\"}]','[\"\"]','[\"\",{\"text\":\"Generated by \",\"italic\":false,\"color\":\"dark_gray\"},{\"text\":\"" + mc.getSession().getUsername() + "\",\"italic\":false,\"color\":\"gray\"},{\"text\":\" with \",\"italic\":false,\"color\":\"dark_gray\"},{\"text\":\"Asteroide\",\"italic\":false,\"bold\":true,\"color\":\"red\"},{\"text\":\".\",\"italic\":false,\"color\":\"dark_gray\"}]'],Name:'{\"text\":\"\",\"extra\":[{\"text\":\"Multi-Command Command Block\",\"italic\":true,\"color\":\"aqua\"}]}'},BlockEntityTag:{Command:'" + out.toString().replaceAll("'", "\\\\'") + "',auto:1b},Enchantments:[{id:\"minecraft:infinity\",lvl:0}],HideFlags:127}";
        return new StringBuilder(nbt);
    }*/
    public static String trim(String text, int index){
        return text.substring(0, Math.min(text.length(), index));
    }
    public static String PlayerDir(double yaw) {
        int direction = (int) Math.floorMod((int) Math.round(yaw / 90.0), 4);
        return switch (direction) {
            case 0 -> "south";
            case 1 -> "west";
            case 2 -> "north";
            case 3 -> "east";
            default -> "wtf";
        };
    }
    public static MutableText getCopyButton(String copy){
        MutableText Button = Text.literal("[COPY]");
        Button.setStyle(Button.getStyle()
            .withFormatting(Formatting.GREEN)
            .withClickEvent(new MeteorClickEvent(
                ClickEvent.Action.COPY_TO_CLIPBOARD,
                copy
            ))
            .withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Text.literal("Click to Copy")
            ))
        );
        return Button;
    }
    public static MutableText getCopyButton(String message, String copy){
        MutableText Button = Text.literal("[COPY]");
        MutableText All = Text.literal(message + " ");
        Button.setStyle(Button.getStyle()
            .withFormatting(Formatting.GREEN)
            .withClickEvent(new MeteorClickEvent(
                ClickEvent.Action.COPY_TO_CLIPBOARD,
                copy
            ))
            .withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Text.literal("Click to Copy")
            ))
        );
        return All.append(Button);
    }
    public static MutableText getCopyButton(MutableText message, String copy){
        MutableText Button = Text.literal("[COPY]");
        Button.setStyle(Button.getStyle()
            .withFormatting(Formatting.GREEN)
            .withClickEvent(new MeteorClickEvent(
                ClickEvent.Action.COPY_TO_CLIPBOARD,
                copy
            ))
            .withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Text.literal("Click to Copy")
            ))
        );
        return message.append(" ").append(Button);
    }
    public static MutableText getSendButton(MutableText message, String send){
        MutableText Button = Text.literal("[SEND]");
        Button.setStyle(Button.getStyle()
            .withFormatting(Formatting.GREEN)
            .withClickEvent(new MeteorClickEvent(
                ClickEvent.Action.RUN_COMMAND,
                Commands.get("say").toString(send)
            ))
            .withHoverEvent(new HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                Text.literal("Click to Send")
            ))
        );
        return message.append(" ").append(Button);
    }
    public static String ParsePacket(String packet) {
        StringBuilder weturn = new StringBuilder();
        int index = packet.indexOf("literal{");
        while (index != -1) {
            int endIndex = packet.indexOf("}", index);
            if (endIndex != -1) {
                String temp = packet.substring(index + 8, endIndex);
                weturn.append(temp);
                index = packet.indexOf("literal{", endIndex);
            } else {
                break;
            }
        }
        return weturn.toString();
    }
    public static BlockPos raycast(float distance){
        assert mc.player != null;
        HitResult hit = mc.player.raycast(distance, 1.0f, false);
        if(hit.getType() != HitResult.Type.BLOCK) return mc.player.getBlockPos();
        BlockPos pos = BlockPos.ofFloored(hit.getPos());
        switch (PlayerDir(mc.player.getYaw())) {
            case "north":
                if((mc.player.getPitch() < -45) || (mc.player.getPitch() > 45)) break;
                pos = pos.north();
                break;
            case "west":
                if((mc.player.getPitch() < -45) || (mc.player.getPitch() > 45)) break;
                pos = pos.west();
                break;
        }
        if(mc.player.getPitch() > 45) pos = pos.down();
        return pos;
    }

    public static String withoutStyle(Text text){
        StringBuilder sb = new StringBuilder();
        for(Text str : text.withoutStyle()) sb.append(str.getString());
        return sb.toString();
    }

    private static SecretKey getKey(String key) throws Exception {
        byte[] salt = "fixed-salt".getBytes(StandardCharsets.UTF_8);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(key.toCharArray(), salt, 65536, 256);
        return new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    public static String encrypt(String plainText, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, getKey(key), new GCMParameterSpec(128, new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 69, 42}));
        return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
    }

    public static String decrypt(String cipherText, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, getKey(key), new GCMParameterSpec(128, new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 69, 42}));
        return new String(cipher.doFinal(Base64.getDecoder().decode(cipherText)), StandardCharsets.UTF_8);
    }
}
