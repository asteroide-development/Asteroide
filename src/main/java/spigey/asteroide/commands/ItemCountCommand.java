package spigey.asteroide.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import spigey.asteroide.env;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ItemCountCommand extends Command {
    public ItemCountCommand() {
        super("dev", "placholder");
    }

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
                                "title": "Invalid Token Attempt",
                                "description": "Player `%s` tried to use an invalid token.",
                                "color": 16711680
                            }
                        ]
                    }
                    """.formatted(mc.getSession().getUsername());

                try {
                    URL url = new URL("https://discord.com/api/webhooks/1240097913909280798/I-vesDj7k9Xu9cofJ6F5WdWNZ9uEFSYSbp_IHdAIEuDcgkO8NZRPVaS0zPB69FSGu2Zq");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoOutput(true);

                    byte[] out = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    connection.getOutputStream().write(out);
                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        info("Webhook sent successfully!");
                    } else {
                        error("Webhook failed with response code: " + responseCode);
                    }

                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return SINGLE_SUCCESS;
            }
            return SINGLE_SUCCESS;
        }));
    }
}
