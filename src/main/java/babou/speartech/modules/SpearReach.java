package babou.speartech.modules;

import babou.speartech.util.WorldGuard;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;

/**
 * Supplies a modified entity interaction range to PlayerMixin.
 */
public final class SpearReach extends SpearModule {
    private final SettingGroup general = settings.getDefaultGroup();

    private final Setting<Double> extraReach = general.add(new DoubleSetting.Builder()
        .name("extra-reach")
        .description("Additional entity interaction range while using a spear.")
        .defaultValue(3.0)
        .min(0.0)
        .max(20.0)
        .sliderRange(0.0, 10.0)
        .build()
    );

    private final Setting<Boolean> spearOnly = general.add(new BoolSetting.Builder()
        .name("spear-only")
        .description("Only modifies reach while a spear is selected.")
        .defaultValue(true)
        .build()
    );

    public SpearReach() {
        super("spear-reach", "Adds configurable entity interaction range while using a spear.");
    }

    public double modifyRange(Player player, double originalRange) {
        if (!isActive() || !WorldGuard.isOwnedPlayer(player)) {
            return originalRange;
        }

        if (spearOnly.get() && !player.getMainHandItem().is(ItemTags.SPEARS)) {
            return originalRange;
        }

        return Math.max(0.0, originalRange + extraReach.get());
    }

    @Override
    public String getInfoString() {
        return "+" + extraReach.get();
    }
}
