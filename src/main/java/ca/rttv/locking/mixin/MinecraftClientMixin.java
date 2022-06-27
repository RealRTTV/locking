package ca.rttv.locking.mixin;

import ca.rttv.locking.Locking;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
final class MinecraftClientMixin {
    @Shadow
    @Nullable
    public ClientPlayerEntity player;

    @ModifyExpressionValue(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", ordinal = 1))
    private boolean swapHands(boolean value) {
        //noinspection ConstantConditions
        return value || Locking.LOCKS.contains(player.getInventory().selectedSlot) || Locking.LOCKS.contains(40);
    }

    @ModifyExpressionValue(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z", ordinal = 2))
    private boolean dropItem(boolean value) {
        //noinspection ConstantConditions
        return value || Locking.LOCKS.contains(player.getInventory().selectedSlot);
    }

    @Inject(method = "disconnect(Lnet/minecraft/client/gui/screen/Screen;)V", at = @At("TAIL"))
    private void disconnect(Screen screen, CallbackInfo ci) {
        Locking.loaded = false;
    }
}
