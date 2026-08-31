package babou.speartech.modules;

import meteordevelopment.meteorclient.events.entity.player.DoAttackEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.tags.ItemTags;

public class SpearCooldown extends SpearModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> minimumCharge = sgGeneral.add(new DoubleSetting.Builder()
        .name("minimum-charge")
        .description("Blocks an attack until the spear cooldown reaches this percentage.")
        .defaultValue(0.92)
        .min(0.0)
        .max(1.0)
        .sliderRange(0.0, 1.0)
        .build()
    );

    private final Setting<Boolean> spearOnly = sgGeneral.add(new BoolSetting.Builder()
        .name("spear-only")
        .description("Only applies the cooldown guard while holding a spear.")
        .defaultValue(true)
        .build()
    );

    public SpearCooldown() {
        super("spear-cooldown", "Prevents weak early spear swings by waiting for the configured attack charge.");
    }

    @EventHandler
    private void onAttack(DoAttackEvent event) {
        if (!canRun()) return;
        if (spearOnly.get() && !mc.player.getMainHandItem().is(ItemTags.SPEARS)) return;

        if (mc.player.getAttackStrengthScale(0.0f) < minimumCharge.get()) event.cancel();
    }

    @Override
    public String getInfoString() {
        return Math.round(minimumCharge.get() * 100.0) + "%";
    }
}
