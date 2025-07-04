package spigey.asteroide.utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import spigey.asteroide.AsteroideAddon;

import java.net.URI;
import java.util.*;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.AsteroideAddon.gson;
import static spigey.asteroide.util.randomNum;

public class ws extends WebSocketClient {
    private static ws instance;
    public ws(URI serverUri){ super(serverUri); instance = this; }

    private final Timer ping = new Timer();

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send("{\"event\":\"init\",\"username\":\"" + mc.getSession().getUsername() + "\"}");

        ping.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(isOpen()) send("{\"event\":\"ping\"}");
            }
        }, 30000, 30000);
    }

    @Override
    public void onMessage(String s) {
        JsonObject message = JsonParser.parseString(s).getAsJsonObject();
        try {
            switch (message.has("event") ? message.get("event").getAsString() : "") {
                case "users":
                    Set<String> dearfucknigga = new HashSet<>();
                    message.getAsJsonArray("further").forEach(key -> dearfucknigga.add(key.getAsString()));
                    AsteroideAddon.users = dearfucknigga;
                    break;
                case "message":
                    mc.player.sendMessage(Text.of(message.get("message").getAsString()), false);
                    break;
                case "disc":
                    String[] warndom = {"multiplayer.disconnect.chat_validation_failed", "multiplayer.disconnect.duplicate_login", "multiplayer.disconnect.duplicate_login", "multiplayer.status.unknown", "multiplayer.disconnect.kicked"};
                    Objects.requireNonNull(mc.getNetworkHandler()).getConnection().disconnect(Text.of(I18n.translate(warndom[randomNum(0, warndom.length - 1)])));
                    break;
            }
        }catch(Exception E){/**/}
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        ping.cancel();
        connect();
    }

    @Override
    public void onError(Exception e) {
        close();
    }

    public static void sendChat(String... args){
        if(instance == null || !instance.isOpen()) return;
        Map<String, Object> json = new HashMap<>();
        json.put("event", "rtc");
        json.put("args", Arrays.asList(args));
        instance.send(gson.toJson(json));
    }

    public static void call(String event, String... args){
        if(instance == null || !instance.isOpen()) return;
        Map<String, Object> json = new HashMap<>();
        json.put("event", event);
        json.put("args", Arrays.asList(args));
        instance.send(gson.toJson(json));
    }
}
