package ca.rttv.locking.mixin;

import ca.rttv.locking.Locking;
import net.minecraft.network.ClientConnection;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
final class ClientConnectionMixin {
    @Inject(method = "disconnect", at = @At("HEAD"))
    private void disconnect(Text disconnectReason, CallbackInfo ci) {
        Locking.loaded = false;
    }
}
