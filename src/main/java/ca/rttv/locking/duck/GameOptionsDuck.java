package ca.rttv.locking.duck;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBind;

public interface GameOptionsDuck {

    KeyBind locking$getLockKey();

    static KeyBind getLockKey(GameOptions options) {
        return ((GameOptionsDuck) options).locking$getLockKey();
    }
}
