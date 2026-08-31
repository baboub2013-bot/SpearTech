package babou.speartech.modules;

import babou.speartech.SpearTechAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.phys.Vec3;

public class SpearRangePreview extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> spearOnly = sgGeneral.add(new BoolSetting.Builder()
        .name("spear-only")
        .description("Only displays the range line while a spear is selected.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> lineColor = sgGeneral.add(new ColorSetting.Builder()
        .name("line-color")
        .description("Color of the range preview line.")
        .defaultValue(new SettingColor(145, 61, 226, 220))
        .build()
    );

    public SpearRangePreview() {
        super(SpearTechAddon.SPEAR, "spear-range-preview", "Draws a live line showing your current entity interaction range.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (mc.player == null || mc.level == null) return;
        if (spearOnly.get() && !mc.player.getMainHandItem().is(ItemTags.SPEARS)) return;

        Vec3 start = mc.player.getEyePosition();
        Vec3 end = start.add(mc.player.getViewVector(1.0f).scale(mc.player.entityInteractionRange()));

        event.renderer.line(start.x, start.y, start.z, end.x, end.y, end.z, lineColor.get());
    }

    @Override
    public String getInfoString() {
        return mc.player == null ? null : String.format("%.1fm", mc.player.entityInteractionRange());
    }
}
