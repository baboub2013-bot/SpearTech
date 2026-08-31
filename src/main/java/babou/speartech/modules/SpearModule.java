package babou.speartech.modules;

import babou.speartech.SpearTechAddon;
import babou.speartech.util.WorldGuard;
import meteordevelopment.meteorclient.systems.modules.Module;

/**
 * Base class for modules that modify gameplay.
 *
 * It centralizes the environment check so each gameplay module does not need
 * to repeat the same activation and runtime guard logic.
 */
public abstract class SpearModule extends Module {
    protected SpearModule(String name, String description) {
        super(SpearTechAddon.CATEGORY, name, description);
    }

    @Override
    public void onActivate() {
        if (WorldGuard.canModifyGameplay()) return;

        error("Gameplay modifiers are only available in worlds you control.");
        toggle();
    }

    protected final boolean canRun() {
        return isActive()
            && mc.player != null
            && mc.level != null
            && WorldGuard.canModifyGameplay();
    }
}
