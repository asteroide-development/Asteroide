package spigey.asteroide.utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import spigey.asteroide.AsteroideAddon;
import spigey.asteroide.modules.RTCSettingsModule;

import java.net.URI;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.AsteroideAddon.gson;
import static spigey.asteroide.util.randomNum;

public class ws extends WebSocketClient {
    private static ws instance;
    public ws(URI serverUri){ super(serverUri); instance = this; }

    private Timer ping;
    private static volatile boolean reconnecting = false;

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        send("{\"event\":\"init\",\"username\":\"" + mc.getSession().getUsername() + "\"}");
        ping = new Timer();
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
                    send("{\"event\":\"ping\"}");
                    break;
                case "message":
                    final RTCSettingsModule rtc = Modules.get().get(RTCSettingsModule.class);
                    String msg = message.get("message").getAsString();
                    if(rtc.censor.get() && rtc.isActive()) msg = msg.replaceAll("(?i)igg", "***").replaceAll("(?i)fag", "***");
                    if(!(rtc.hideMessages.get() && rtc.isActive())) mc.player.sendMessage(Text.of(msg), false);
                    break;
                case "disc":
                    String[] warndom = {"multiplayer.disconnect.chat_validation_failed", "multiplayer.status.unknown", "multiplayer.disconnect.kicked"};
                    mc.getNetworkHandler().getConnection().disconnect(Text.of(I18n.translate(warndom[randomNum(0, warndom.length - 1)])));
                    break;
            }
        }catch(Exception E){/**/}
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        if(reconnecting) return;
        reconnecting = true;
        if(ping != null) { ping.cancel(); ping = null; }
        try{ mc.player.sendMessage(Text.of("§8§l[§c§lAsteroide§8§l]§r Disconnected from RTC Server. Attempting to reconnect"), false); }catch(Exception L){/**/}
        new Thread(() -> {
            while(true){
                try{
                    Thread.sleep(3000);
                    ws tempClient = new ws(getURI());
                    if(tempClient.connectBlocking(2500, TimeUnit.MILLISECONDS)) {
                        instance = tempClient;
                        AsteroideAddon.wss = tempClient;
                        reconnecting = false;
                        break;
                    }
                }
                catch(Exception L){/**/}
            }
        }).start();
    }

    @Override
    public void onError(Exception e) { if(isOpen()) { close(); } }

    public static void sendChat(String... args){
        if(instance == null || !instance.isOpen()) return;
        Map<String, Object> json = new HashMap<>();
        final RTCSettingsModule rtc = Modules.get().get(RTCSettingsModule.class);
        json.put("event", "rtc");
        if(rtc.isActive()) json.put("format", new String[]{rtc.color.get().name(), rtc.formath.get().name()});
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
