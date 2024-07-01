package spigey.asteroide.mixin;

import com.google.gson.JsonObject;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import spigey.asteroide.modules.BetterBungeeSpoofModule;
import spigey.asteroide.modules.VersionSpoofModule;
import spigey.asteroide.util;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static spigey.asteroide.AsteroideAddon.gson;
import static spigey.asteroide.AsteroideAddon.spoofedIP;

@Mixin(HandshakeC2SPacket.class)
public class BedrockSpoofMixin {

    @Mutable
    @Shadow
    @Final
    private int protocolVersion;

    @Inject(method = "<init>(ILjava/lang/String;ILnet/minecraft/network/packet/c2s/handshake/ConnectionIntent;)V", at = @At("RETURN"))
    private void spoofProtocolVersion(int i, String string, int j, ConnectionIntent connectionIntent, CallbackInfo ci) {
        if(!(Modules.get().get(VersionSpoofModule.class).isActive())) return;
        this.protocolVersion = VersionSpoofModule.readable(Modules.get().get(VersionSpoofModule.class).spoofedVersion.get());
    }
}
