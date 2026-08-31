package babou.speartech.modules;

import babou.speartech.util.WorldGuard;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;

/**
 * Supplies a modified attack-damage value to LivingEntityMixin.
 */
public final class OneShot extends SpearModule {
    private final SettingGroup general = settings.getDefaultGroup();

    private final Setting<Mode> mode = general.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .description("How spear attack damage is modified.")
        .defaultValue(Mode.Minimum)
        .build()
    );

    private final Setting<Double> multiplier = general.add(new DoubleSetting.Builder()
        .name("multiplier")
        .description("Attack-damage multiplier.")
        .defaultValue(10.0)
        .min(1.0)
        .max(100.0)
        .sliderRange(1.0, 25.0)
        .visible(() -> mode.get() == Mode.Multiplier)
        .build()
    );

    private final Setting<Double> minimumDamage = general.add(new DoubleSetting.Builder()
        .name("minimum-damage")
        .description("Minimum attack damage returned for the local player.")
        .defaultValue(100.0)
        .min(1.0)
        .max(2048.0)
        .sliderRange(1.0, 250.0)
        .visible(() -> mode.get() == Mode.Minimum)
        .build()
    );

    private final Setting<Boolean> spearOnly = general.add(new BoolSetting.Builder()
        .name("spear-only")
        .description("Only applies the damage modification while holding a spear.")
        .defaultValue(true)
        .build()
    );

    public OneShot() {
        super("one-shot", "Boosts spear attack damage with configurable damage modes.");
    }

    public double modifyAttackDamage(Player player, double originalDamage) {
        if (!isActive() || !WorldGuard.isOwnedPlayer(player)) {
            return originalDamage;
        }

        if (spearOnly.get() && !player.getMainHandItem().is(ItemTags.SPEARS)) {
            return originalDamage;
        }

        return switch (mode.get()) {
            case Multiplier -> originalDamage * multiplier.get();
            case Minimum -> Math.max(originalDamage, minimumDamage.get());
        };
    }

    @Override
    public String getInfoString() {
        return mode.get() == Mode.Multiplier
            ? multiplier.get() + "x"
            : minimumDamage.get().intValue() + " dmg";
    }

    public enum Mode {
        Multiplier,
        Minimum
    }
}
