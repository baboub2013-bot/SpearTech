package babou.speartech;

import babou.speartech.hud.SpearStatusHud;
import babou.speartech.modules.LungeBoost;
import babou.speartech.modules.OneShot;
import babou.speartech.modules.SpearCooldown;
import babou.speartech.modules.SpearReach;
import babou.speartech.modules.SpearRangePreview;
import babou.speartech.modules.SpearSwap;
import babou.speartech.modules.SpearVelocity;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.render.DisplayItemUtils;
import net.minecraft.world.item.Items;

public class SpearTechAddon extends MeteorAddon {
    public static final Category SPEAR = new Category("Spear Tech", () -> DisplayItemUtils.toStack(Items.DIAMOND_SPEAR));
    public static final HudGroup HUD_GROUP = new HudGroup("Spear Tech");

    @Override
    public void onInitialize() {
        Modules.get().add(new SpearSwap());
        Modules.get().add(new LungeBoost());
        Modules.get().add(new SpearReach());
        Modules.get().add(new OneShot());
        Modules.get().add(new SpearCooldown());
        Modules.get().add(new SpearVelocity());
        Modules.get().add(new SpearRangePreview());

        Hud.get().register(SpearStatusHud.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(SPEAR);
    }

    @Override
    public String getPackage() {
        return "babou.speartech";
    }
}
