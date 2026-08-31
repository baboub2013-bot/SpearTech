package babou.speartech.modules;

import babou.speartech.util.WorldGuard;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;

public class SpearReach extends SpearModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> extraReach = sgGeneral.add(new DoubleSetting.Builder()
        .name("extra-reach")
        .description("Additional entity interaction range while using a spear.")
        .defaultValue(3.0)
        .min(0.0)
        .max(20.0)
        .sliderRange(0.0, 10.0)
        .build()
    );

    private final Setting<Boolean> spearOnly = sgGeneral.add(new BoolSetting.Builder()
        .name("spear-only")
        .description("Only modifies reach while a spear is selected.")
        .defaultValue(true)
        .build()
    );

    public SpearReach() {
        super("spear-reach", "Adds configurable entity interaction range while using a spear.");
    }

    public double modifyRange(Player player, double original) {
        if (!isActive() || !WorldGuard.isOwnedPlayer(player)) return original;
        if (spearOnly.get() && !player.getMainHandItem().is(ItemTags.SPEARS)) return original;

        return Math.max(0.0, original + extraReach.get());
    }

    @Override
    public String getInfoString() {
        return "+" + extraReach.get();
    }
}
