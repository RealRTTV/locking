package ca.rttv.locking;

import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class MixinExtrasBootstrap implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch(ModContainer mod) {
        com.llamalad7.mixinextras.MixinExtrasBootstrap.init();
    }
}
