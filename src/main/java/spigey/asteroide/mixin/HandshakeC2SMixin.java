package spigey.asteroide.mixin;

import com.google.gson.JsonObject;
import org.spongepowered.asm.mixin.*;
import spigey.asteroide.modules.BetterBungeeSpoofModule;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spigey.asteroide.util;

import static spigey.asteroide.AsteroideAddon.gson;
import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.AsteroideAddon.spoofedIP;

@Mixin(HandshakeC2SPacket.class)
public abstract class HandshakeC2SMixin {
    // public abstract NetworkState getNewNetworkState();

    @Mutable
    @Shadow
    @Final
    private String address;

    @Inject(method = "<init>(ILjava/lang/String;ILnet/minecraft/network/packet/c2s/handshake/ConnectionIntent;)V", at = @At("RETURN"))
    private void onHandshakeC2SPacket(int i, String string, int j, ConnectionIntent connectionIntent, CallbackInfo ci) {
        BetterBungeeSpoofModule bungeeSpoofModule = Modules.get().get(BetterBungeeSpoofModule.class);
        assert bungeeSpoofModule != null;
        if (!bungeeSpoofModule.isActive()) return;
        // if (this.getNewNetworkState() != NetworkState.LOGIN) return;
        // this is so definitely gonna fucking break
        String spoofedUUID = mc.getSession().getUuidOrNull().toString();
        spoofedIP = bungeeSpoofModule.spoofedAddress.get();
        if(bungeeSpoofModule.randomize.get()) spoofedIP = util.randomNum(0,255) + "." + util.randomNum(0,255) + "." + util.randomNum(0,255) + "." + util.randomNum(0,255);


        String URL = "https://api.mojang.com/users/profiles/minecraft/" + mc.getSession().getUsername();

        Http.Request request = Http.get(URL);
        String response = request.sendString();
        if (response != null) {
            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

            if (jsonObject != null && jsonObject.has("id")) {
                spoofedUUID = jsonObject.get("id").getAsString();
            }
        }
        this.address += "\u0000" + spoofedIP + "\u0000" + spoofedUUID;
    }
}
