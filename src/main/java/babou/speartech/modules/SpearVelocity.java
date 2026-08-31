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

public class SpearVelocity extends SpearModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> multiplier = sgGeneral.add(new DoubleSetting.Builder()
        .name("multiplier")
        .description("Horizontal movement multiplier applied while moving with a spear.")
        .defaultValue(1.08)
        .min(1.0)
        .max(3.0)
        .sliderRange(1.0, 1.5)
        .build()
    );

    private final Setting<Double> maxSpeed = sgGeneral.add(new DoubleSetting.Builder()
        .name("max-speed")
        .description("Maximum horizontal velocity after the multiplier is applied.")
        .defaultValue(0.55)
        .min(0.05)
        .max(4.0)
        .sliderRange(0.05, 1.25)
        .build()
    );

    private final Setting<Boolean> onlyOnGround = sgGeneral.add(new BoolSetting.Builder()
        .name("only-on-ground")
        .description("Only applies the velocity multiplier while standing on the ground.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> requireSpear = sgGeneral.add(new BoolSetting.Builder()
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
        if (requireSpear.get() && !mc.player.getMainHandItem().is(ItemTags.SPEARS)) return;
        if (onlyOnGround.get() && !mc.player.onGround()) return;

        Vec2 input = mc.player.input.getMoveVector();
        if (input.x * input.x + input.y * input.y <= 1.0E-6f) return;

        Vec3 velocity = mc.player.getDeltaMovement();
        double horizontal = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        if (horizontal <= 1.0E-7) return;

        double boosted = Math.min(maxSpeed.get(), horizontal * multiplier.get());
        if (boosted <= horizontal) return;

        double scale = boosted / horizontal;
        mc.player.setDeltaMovement(velocity.x * scale, velocity.y, velocity.z * scale);
    }

    @Override
    public String getInfoString() {
        return String.format("%.2fx", multiplier.get());
    }
}
