package babou.speartech.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

/**
 * Scales existing horizontal player velocity while the configured movement
 * conditions are met.
 */
public final class SpearVelocity extends SpearModule {
    private final SettingGroup general = settings.getDefaultGroup();

    private final Setting<Double> multiplier = general.add(new DoubleSetting.Builder()
        .name("multiplier")
        .description("Horizontal movement multiplier applied while moving with a spear.")
        .defaultValue(1.08)
        .min(1.0)
        .max(3.0)
        .sliderRange(1.0, 1.5)
        .build()
    );

    private final Setting<Double> maxSpeed = general.add(new DoubleSetting.Builder()
        .name("max-speed")
        .description("Maximum horizontal velocity after the multiplier is applied.")
        .defaultValue(0.55)
        .min(0.05)
        .max(4.0)
        .sliderRange(0.05, 1.25)
        .build()
    );

    private final Setting<Boolean> onlyOnGround = general.add(new BoolSetting.Builder()
        .name("only-on-ground")
        .description("Only applies the velocity multiplier while standing on the ground.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> requireSpear = general.add(new BoolSetting.Builder()
        .name("require-spear")
        .description("Requires a spear in the main hand.")
        .defaultValue(true)
        .build()
    );

    public SpearVelocity() {
        super("spear-velocity", "Adds configurable movement momentum while using a spear.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!canRun()) return;
        if (!hasRequiredItem()) return;
        if (onlyOnGround.get() && !mc.player.onGround()) return;
        if (!isMoving()) return;

        Vec3 velocity = mc.player.getDeltaMovement();
        double currentSpeed = horizontalSpeed(velocity);

        if (currentSpeed <= 1.0E-7) return;

        double targetSpeed = Math.min(maxSpeed.get(), currentSpeed * multiplier.get());
        if (targetSpeed <= currentSpeed) return;

        double scale = targetSpeed / currentSpeed;
        mc.player.setDeltaMovement(
            velocity.x * scale,
            velocity.y,
            velocity.z * scale
        );
    }

    private boolean hasRequiredItem() {
        return !requireSpear.get() || mc.player.getMainHandItem().is(ItemTags.SPEARS);
    }

    private boolean isMoving() {
        Vec2 input = mc.player.input.getMoveVector();
        return input.x * input.x + input.y * input.y > 1.0E-6f;
    }

    private static double horizontalSpeed(Vec3 velocity) {
        return Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
    }

    @Override
    public String getInfoString() {
        return String.format("%.2fx", multiplier.get());
    }
}
