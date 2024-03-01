package ca.rttv.locking.mixin;

import ca.rttv.locking.duck.GameOptionsDuck;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(GameOptions.class)
final class GameOptionsMixin implements GameOptionsDuck {
    @Unique
    private final KeyBinding lockKey = new KeyBinding("key.lock", 75, "key.categories.inventory");

    @Override
    public KeyBinding locking$getLockKey() {
        return lockKey;
    }

    @SuppressWarnings("unchecked")
    @ModifyArg(method = "<init>", at = @At(value = "INVOKE", target = "Lorg/apache/commons/lang3/ArrayUtils;addAll([Ljava/lang/Object;[Ljava/lang/Object;)[Ljava/lang/Object;"), index = 0)
    private <T> T[] addAll(T[] arr) {
        T[] keys = (T[]) new KeyBinding[arr.length + 1];
        System.arraycopy(arr, 0, keys, 0, arr.length);
        keys[arr.length] = (T) lockKey;
        return keys;
    }
}
