package ca.rttv.locking.duck;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;

public interface GameOptionsDuck {

    KeyBinding locking$getLockKey();

    static KeyBinding getLockKey(GameOptions options) {
        return ((GameOptionsDuck) options).locking$getLockKey();
    }
}
