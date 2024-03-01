package ca.rttv.locking.mixin;

import ca.rttv.locking.Locking;
import ca.rttv.locking.duck.GameOptionsDuck;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(HandledScreen.class)
final class HandledScreenMixin extends Screen {
    @Unique
    private static final Identifier SLOT_LOCK_TEXTURE = new Identifier("locking", "textures/gui/lock.png");

    private HandledScreenMixin(Text text) {
        super(text);
    }

    @Shadow
    @Nullable
    @SuppressWarnings("ShadowModifiers")
    private Slot focusedSlot;

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;handleHotbarKeyPressed(II)Z", shift = At.Shift.AFTER))
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        //noinspection ConstantConditions
        if (focusedSlot != null && GameOptionsDuck.getLockKey(client.options).matchesKey(keyCode, scanCode)) {
            Locking.toggleLock(focusedSlot);
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/ingame/HandledScreen;drawSlot(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/screen/slot/Slot;)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void drawSlot(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, int i, int j, int k, Slot slot) {
        if (slot != null && Locking.isLocked(slot)) {
            context.drawTexture(SLOT_LOCK_TEXTURE, slot.x, slot.y, 0, 0, 0, 16, 16, 16, 16);
        }
    }

    @Inject(method = "onMouseClick(Lnet/minecraft/screen/slot/Slot;IILnet/minecraft/screen/slot/SlotActionType;)V", at = @At("HEAD"), cancellable = true)
    private void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType, CallbackInfo ci) {
        if (slot != null
         && Locking.isLocked(slot)
         || actionType == SlotActionType.SWAP
         && Locking.LOCKS.contains(button)
        ) {
            ci.cancel();
        }
    }
}
