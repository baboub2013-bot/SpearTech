package babou.speartech.modules;

import babou.speartech.SpearTechAddon;
import babou.speartech.util.WorldGuard;
import meteordevelopment.meteorclient.systems.modules.Module;

public abstract class SpearModule extends Module {
    protected SpearModule(String name, String description) {
        super(SpearTechAddon.SPEAR, name, description);
    }

    @Override
    public void onActivate() {
        if (!WorldGuard.canModifyGameplay()) {
            error("Gameplay modifiers are only available in worlds you control.");
            toggle();
        }
    }

    protected final boolean canRun() {
        return isActive() && WorldGuard.canModifyGameplay() && mc.player != null && mc.level != null;
    }
}
