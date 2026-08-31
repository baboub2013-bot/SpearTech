package babou.speartech.hud;

import babou.speartech.SpearTechAddon;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.Minecraft;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.enchantment.Enchantments;

/**
 * Visual-only Meteor HUD element for the selected spear.
 */
public final class SpearStatusHud extends HudElement {
    public static final HudElementInfo<SpearStatusHud> INFO = new HudElementInfo<>(
        SpearTechAddon.HUD_GROUP,
        "spear-status",
        "Displays spear charge, reach and Lunge level.",
        SpearStatusHud::new
    );

    private final SettingGroup general = settings.getDefaultGroup();

    private final Setting<Boolean> shadow = general.add(new BoolSetting.Builder()
        .name("shadow")
        .description("Adds a shadow behind the HUD text.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> labelColor = general.add(new ColorSetting.Builder()
        .name("label-color")
        .description("Color used for labels.")
        .defaultValue(new SettingColor(180, 180, 180))
        .build()
    );

    private final Setting<SettingColor> valueColor = general.add(new ColorSetting.Builder()
        .name("value-color")
        .description("Color used for values.")
        .defaultValue(new SettingColor(255, 255, 255))
        .build()
    );

    private final Setting<SettingColor> readyColor = general.add(new ColorSetting.Builder()
        .name("ready-color")
        .description("Color used when the spear attack is fully charged.")
        .defaultValue(new SettingColor(80, 230, 120))
        .build()
    );

    public SpearStatusHud() {
        super(INFO);
    }

    @Override
    public void render(HudRenderer renderer) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) {
            renderEditorPreview(renderer);
            return;
        }

        boolean holdingSpear = mc.player.getMainHandItem().is(ItemTags.SPEARS);
        float attackCharge = mc.player.getAttackStrengthScale(0.0f);
        double reach = mc.player.entityInteractionRange();

        int lungeLevel = holdingSpear
            ? Utils.getEnchantmentLevel(mc.player.getMainHandItem(), Enchantments.LUNGE)
            : 0;

        String state = statusText(holdingSpear, attackCharge);
        String details = state
            + " • " + String.format("%.1fm", reach)
            + " • Lunge " + roman(lungeLevel);

        renderLine(renderer, details, holdingSpear && attackCharge >= 0.99f);
    }

    private void renderEditorPreview(HudRenderer renderer) {
        if (isInEditor()) {
            renderLine(renderer, "Ready • 3.0m • Lunge III", true);
        }
    }

    private void renderLine(HudRenderer renderer, String details, boolean ready) {
        double scale = Hud.get().getTextScale();

        double valueX = renderer.text(
            "Spear  ",
            x,
            y,
            labelColor.get(),
            shadow.get(),
            scale
        );

        double endX = renderer.text(
            details,
            valueX,
            y,
            ready ? readyColor.get() : valueColor.get(),
            shadow.get(),
            scale
        );

        setSize(endX - x, renderer.textHeight(shadow.get(), scale));
    }

    private static String statusText(boolean holdingSpear, float attackCharge) {
        if (!holdingSpear) return "No spear";
        if (attackCharge >= 0.99f) return "Ready";
        return Math.round(attackCharge * 100f) + "%";
    }

    private static String roman(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> "-";
        };
    }
}
