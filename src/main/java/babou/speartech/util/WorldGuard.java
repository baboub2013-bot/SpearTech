package babou.speartech.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public final class WorldGuard {
    private WorldGuard() {}

    /**
     * Gameplay-changing features are scoped to a world hosted by this client
     * with no additional connected players.
     */
    public static boolean canModifyGameplay() {
        Minecraft mc = Minecraft.getInstance();

        return mc.player != null
            && mc.level != null
            && mc.getConnection() != null
            && mc.isLocalServer()
            && mc.getConnection().getOnlinePlayers().size() <= 1;
    }

    public static boolean isOwnedPlayer(Player player) {
        Minecraft mc = Minecraft.getInstance();
        return canModifyGameplay() && mc.player != null && player.getUUID().equals(mc.player.getUUID());
    }
}
