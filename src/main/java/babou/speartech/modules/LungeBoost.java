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

public class LungeBoost extends SpearModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> horizontalBoost = sgGeneral.add(new DoubleSetting.Builder()
        .name("horizontal-boost")
        .description("Extra forward impulse added after a spear piercing attack.")
        .defaultValue(0.75)
        .min(0.0)
        .max(8.0)
        .sliderRange(0.0, 3.0)
        .build()
    );

    private final Setting<Double> verticalBoost = sgGeneral.add(new DoubleSetting.Builder()
        .name("vertical-boost")
        .description("Extra vertical impulse. Zero keeps the boost horizontal.")
        .defaultValue(0.0)
        .min(-2.0)
        .max(4.0)
        .sliderRange(-1.0, 2.0)
        .build()
    );

    private final Setting<Boolean> requireLunge = sgGeneral.add(new BoolSetting.Builder()
        .name("require-lunge")
        .description("Requires the selected spear to actually have Lunge.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> scaleByLevel = sgGeneral.add(new BoolSetting.Builder()
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

        int level = Utils.getEnchantmentLevel(player.getMainHandItem(), Enchantments.LUNGE);
        if (requireLunge.get() && level <= 0) return;

        double scale = scaleByLevel.get() && level > 0 ? level : 1.0;
        Vec3 look = player.getLookAngle();
        Vec3 horizontal = new Vec3(look.x, 0.0, look.z);

        if (horizontal.lengthSqr() > 1.0E-7) horizontal = horizontal.normalize();

        Vec3 impulse = horizontal.scale(horizontalBoost.get() * scale)
            .add(0.0, verticalBoost.get() * scale, 0.0);

        player.push(impulse.x, impulse.y, impulse.z);
    }

    @Override
    public String getInfoString() {
        return "+" + horizontalBoost.get();
    }
}
