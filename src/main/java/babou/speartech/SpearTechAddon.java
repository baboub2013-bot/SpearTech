package babou.speartech;

import babou.speartech.hud.SpearStatusHud;
import babou.speartech.modules.LungeBoost;
import babou.speartech.modules.OneShot;
import babou.speartech.modules.SpearCooldown;
import babou.speartech.modules.SpearRangePreview;
import babou.speartech.modules.SpearReach;
import babou.speartech.modules.SpearSwap;
import babou.speartech.modules.SpearVelocity;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.DisplayItemUtils;
import net.minecraft.world.item.Items;

/**
 * Meteor entry point. This class only registers Spear Tech modules and HUD elements.
 */
public final class SpearTechAddon extends MeteorAddon {
    public static final Category CATEGORY =
        new Category("Spear Tech", () -> DisplayItemUtils.toStack(Items.DIAMOND_SPEAR));

    public static final HudGroup HUD_GROUP = new HudGroup("Spear Tech");

    @Override
    public void onInitialize() {
        Modules modules = Modules.get();

        modules.add(new SpearSwap());
        modules.add(new LungeBoost());
        modules.add(new SpearReach());
        modules.add(new OneShot());
        modules.add(new SpearCooldown());
        modules.add(new SpearVelocity());
        modules.add(new SpearRangePreview());

        Hud.get().register(SpearStatusHud.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "babou.speartech";
    }
}
