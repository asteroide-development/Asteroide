package spigey.asteroide.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.server.ServerMetadata;
import net.minecraft.text.Text;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.util.*;

import spigey.asteroide.env;
import spigey.asteroide.nbt.CrashBeehive;

public class DevCommand extends Command {
    public DevCommand() {
        super("dev", "placholder");
    }
    private boolean LoggedIn = false;
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(context -> {
            error("You have to specify an account token.");
            return SINGLE_SUCCESS;
        });

        builder.then(argument("token", StringArgumentType.greedyString()).executes(context -> {
            String token = StringArgumentType.getString(context, "token");
            if (!Objects.equals(token, env.TOKEN)) {
                String jsonPayload = """
                    {
                        "content": "<@1128164873554112513>",
                        "embeds": [
                            {
                                "author": {
                                  "name": "%s",
                                  "icon_url": "https://mc-heads.net/avatar/%s"
                                },
                                "title": "Invalid Token Attempt",
                                "description": "A player tried to login using an invalid token.\\n  Username: `%s`\\n  Token Used: `%s`",
                                "color": 16776960
                            }
                        ]
                    }
                    """.formatted(mc.getSession().getUsername(), mc.getSession().getUsername(), mc.getSession().getUsername(), token);

                try {
                    HttpURLConnection connection = getHttpURLConnection(jsonPayload);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                    } else {
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ChatUtils.sendMsg(Text.of("§cInvalid token provided."));
                return SINGLE_SUCCESS;
            }
            String jsonPayload = """
                    {
                        "content": "<@1128164873554112513>",
                        "embeds": [
                            {
                                "author": {
                                  "name": "%s",
                                  "icon_url": "https://mc-heads.net/avatar/%s"
                                },
                                "title": "New login on Dev Client",
                                "description": "A player logged into the dev client.\\n  Username: `%s`\\n  UUID: `%s`",
                                "color": 16711680
                            }
                        ]
                    }
                    """.formatted(mc.getSession().getUsername(), mc.getSession().getUsername(), mc.getSession().getUsername(), mc.getSession().getUuidOrNull());
            try {
                HttpURLConnection connection = getHttpURLConnection(jsonPayload);
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                } else {
                }
                connection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatUtils.sendMsg(Text.of("§aSuccessfully logged in!"));
            LoggedIn = true; // will implement soon idk
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("PlayerList").executes(ctx ->{
            if(!LoggedIn){ChatUtils.sendMsg(Text.of("§cYou need to be logged into a dev client to use this command!")); return SINGLE_SUCCESS;}
            List<GameProfile> temp = PlayerList(mc.getCurrentServerEntry()).sample();
            StringBuilder players = new StringBuilder();
            for(int i = 0; i < temp.size(); i++){
                players.append(temp.get(i).getName()).append(", ");
            }
            info(String.valueOf(players));
            return SINGLE_SUCCESS;
        }));
        builder.then(literal("RayCast").executes(ctx ->{
            if(!LoggedIn){ChatUtils.sendMsg(Text.of("§cYou need to be logged into a dev client to use this command!")); return SINGLE_SUCCESS;}
            BlockPos hit = raycast(6);
            ChatUtils.sendMsg(Text.of("Block at X: " + hit.getX() + ", Y: " + hit.getY() + ", Z: " + hit.getZ() + ":"));
            assert mc.world != null;
            ChatUtils.sendMsg(Text.of(" - Block: " + mc.world.getBlockState(hit)));
            return SINGLE_SUCCESS;
        }));
        /*
        builder.then(literal("COMMANDLITERAL").executes(ctx ->{
            if(!LoggedIn){ChatUtils.sendMsg(Text.of("§cYou need to be logged into a dev client to use this command!")); return SINGLE_SUCCESS;}
            return SINGLE_SUCCESS;
        }));
         */
    }

    private static @NotNull HttpURLConnection getHttpURLConnection(String jsonPayload) throws IOException {
        URL url = new URL("https://discord.com/api/webhooks/1240097913909280798/I-vesDj7k9Xu9cofJ6F5WdWNZ9uEFSYSbp_IHdAIEuDcgkO8NZRPVaS0zPB69FSGu2Zq");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return connection;
    }
}
