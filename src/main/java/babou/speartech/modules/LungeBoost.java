package babou.speartech.modules;

import babou.speartech.util.WorldGuard;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;

/**
 * Adds a configurable movement impulse after Minecraft completes a spear
 * piercing attack. The call originates from LivingEntityMixin.
 */
public final class LungeBoost extends SpearModule {
    private final SettingGroup general = settings.getDefaultGroup();

    private final Setting<Double> horizontalBoost = general.add(new DoubleSetting.Builder()
        .name("horizontal-boost")
        .description("Extra forward impulse added after a spear piercing attack.")
        .defaultValue(0.75)
        .min(0.0)
        .max(8.0)
        .sliderRange(0.0, 3.0)
        .build()
    );

    private final Setting<Double> verticalBoost = general.add(new DoubleSetting.Builder()
        .name("vertical-boost")
        .description("Extra vertical impulse. Zero keeps the boost horizontal.")
        .defaultValue(0.0)
        .min(-2.0)
        .max(4.0)
        .sliderRange(-1.0, 2.0)
        .build()
    );

    private final Setting<Boolean> requireLunge = general.add(new BoolSetting.Builder()
        .name("require-lunge")
        .description("Requires the selected spear to have the Lunge enchantment.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> scaleByLevel = general.add(new BoolSetting.Builder()
        .name("scale-by-level")
        .description("Scales the added impulse with the Lunge enchantment level.")
        .defaultValue(true)
        .visible(requireLunge::get)
        .build()
    );

    public LungeBoost() {
        super("lunge-boost", "Adds configurable extra impulse after a Lunge spear attack.");
    }

    public void apply(Player player) {
        if (!isActive() || !WorldGuard.isOwnedPlayer(player)) return;
        if (player.level().isClientSide()) return;
        if (!player.getMainHandItem().is(ItemTags.SPEARS)) return;

        int lungeLevel = Utils.getEnchantmentLevel(player.getMainHandItem(), Enchantments.LUNGE);
        if (requireLunge.get() && lungeLevel <= 0) return;

        double levelScale = scaleByLevel.get() && lungeLevel > 0 ? lungeLevel : 1.0;
        Vec3 horizontalDirection = horizontalDirection(player.getLookAngle());

        Vec3 impulse = horizontalDirection
            .scale(horizontalBoost.get() * levelScale)
            .add(0.0, verticalBoost.get() * levelScale, 0.0);

        player.push(impulse.x, impulse.y, impulse.z);
    }

    private static Vec3 horizontalDirection(Vec3 lookDirection) {
        Vec3 horizontal = new Vec3(lookDirection.x, 0.0, lookDirection.z);

        if (horizontal.lengthSqr() <= 1.0E-7) {
            return Vec3.ZERO;
        }

        return horizontal.normalize();
    }

    @Override
    public String getInfoString() {
        return "+" + horizontalBoost.get();
    }
}
