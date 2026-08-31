package babou.speartech.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * Central environment check used by gameplay-changing modules.
 */
public final class WorldGuard {
    private WorldGuard() {
    }

    /**
     * Returns true when the current client owns the integrated server and no
     * additional players are connected.
     */
    public static boolean canModifyGameplay() {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null || mc.getConnection() == null) {
            return false;
        }

        if (!mc.isLocalServer()) {
            return false;
        }

        return mc.getConnection().getOnlinePlayers().size() <= 1;
    }

    /**
     * Returns true only for the local player while gameplay modification is allowed.
     */
    public static boolean isOwnedPlayer(Player player) {
        Minecraft mc = Minecraft.getInstance();

        return canModifyGameplay()
            && mc.player != null
            && player.getUUID().equals(mc.player.getUUID());
    }
}
