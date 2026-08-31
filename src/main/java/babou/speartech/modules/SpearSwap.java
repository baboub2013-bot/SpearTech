package babou.speartech.modules;

import babou.speartech.util.WorldGuard;
import meteordevelopment.meteorclient.events.entity.player.DoAttackEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Chooses a spear from the hotbar before an attack, then optionally restores
 * the previously selected slot.
 */
public final class SpearSwap extends SpearModule {
    private final SettingGroup general = settings.getDefaultGroup();
    private final SettingGroup smartSwap = settings.createGroup("Smart Swap");

    private final Setting<Boolean> lungeOnMiss = smartSwap.add(new BoolSetting.Builder()
        .name("lunge-on-miss")
        .description("Swaps to a Lunge spear when attacking without a normal in-range target.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> reachSpear = smartSwap.add(new BoolSetting.Builder()
        .name("reach-spear")
        .description("Prefers a non-Lunge spear when a target is outside normal interaction range.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> excludeLungeFromReach = smartSwap.add(new BoolSetting.Builder()
        .name("exclude-lunge-from-reach")
        .description("Does not select a Lunge spear for the reach swap.")
        .defaultValue(true)
        .visible(reachSpear::get)
        .build()
    );

    private final Setting<Integer> scanRange = smartSwap.add(new IntSetting.Builder()
        .name("scan-range")
        .description("Maximum distance used to look for a target in front of the player.")
        .defaultValue(8)
        .min(3)
        .max(20)
        .sliderRange(3, 12)
        .visible(reachSpear::get)
        .build()
    );

    private final Setting<Boolean> swapBack = general.add(new BoolSetting.Builder()
        .name("swap-back")
        .description("Returns to the original hotbar slot after swapping.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> swapBackDelay = general.add(new IntSetting.Builder()
        .name("swap-back-delay")
        .description("Ticks to wait before restoring the original slot.")
        .defaultValue(2)
        .min(0)
        .max(20)
        .sliderRange(0, 10)
        .visible(swapBack::get)
        .build()
    );

    private boolean waitingToSwapBack;
    private int swapBackTimer;

    public SpearSwap() {
        super("spear-swap", "Selects an appropriate spear from the hotbar before an attack.");
    }

    @Override
    public void onDeactivate() {
        restoreOriginalSlot();
    }

    @EventHandler
    private void onAttack(DoAttackEvent event) {
        if (!canRun() || waitingToSwapBack || mc.hitResult == null) return;
        if (mc.hitResult.getType() == HitResult.Type.BLOCK) return;

        if (tryReachSwap()) return;

        if (lungeOnMiss.get()) {
            swapTo(findSpearSlot(true));
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!waitingToSwapBack) return;

        if (!WorldGuard.canModifyGameplay()) {
            restoreOriginalSlot();
            return;
        }

        if (swapBackTimer-- > 0) return;
        restoreOriginalSlot();
    }

    private boolean tryReachSwap() {
        if (!reachSpear.get()) return false;

        Entity target = findTargetInView();
        if (target == null) return false;

        double normalRange = mc.player.entityInteractionRange();
        if (mc.player.distanceTo(target) <= normalRange + 0.5) return false;

        return swapTo(findSpearSlot(false));
    }

    private boolean swapTo(int slot) {
        if (slot < 0 || slot > 8) return false;
        if (slot == mc.player.getInventory().getSelectedSlot()) return false;
        if (!InvUtils.swap(slot, swapBack.get())) return false;

        waitingToSwapBack = swapBack.get();
        swapBackTimer = swapBackDelay.get();
        return true;
    }

    private void restoreOriginalSlot() {
        if (waitingToSwapBack) {
            InvUtils.swapBack();
        }

        waitingToSwapBack = false;
        swapBackTimer = 0;
    }

    private int findSpearSlot(boolean requireLunge) {
        int selectedSlot = -1;
        int selectedLungeLevel = -1;

        for (int slot = 0; slot < 9; slot++) {
            ItemStack stack = mc.player.getInventory().getItem(slot);
            if (!stack.is(ItemTags.SPEARS)) continue;

            int lungeLevel = Utils.getEnchantmentLevel(stack, Enchantments.LUNGE);

            if (requireLunge && lungeLevel <= 0) continue;
            if (!requireLunge && excludeLungeFromReach.get() && lungeLevel > 0) continue;

            if (lungeLevel > selectedLungeLevel) {
                selectedLungeLevel = lungeLevel;
                selectedSlot = slot;
            }
        }

        return selectedSlot;
    }

    private Entity findTargetInView() {
        double distance = scanRange.get();
        Vec3 start = mc.player.getEyePosition(1.0F);
        Vec3 direction = mc.player.getViewVector(1.0F);
        Vec3 end = start.add(direction.scale(distance));

        AABB searchBox = mc.player
            .getBoundingBox()
            .expandTowards(direction.scale(distance))
            .inflate(1.0);

        Entity closestTarget = null;
        double closestDistanceSquared = distance * distance;

        for (Entity entity : mc.level.getEntities(
            mc.player,
            searchBox,
            candidate -> !candidate.isSpectator() && candidate.isPickable()
        )) {
            AABB hitbox = entity.getBoundingBox().inflate(0.15);
            if (hitbox.clip(start, end).isEmpty()) continue;

            double distanceSquared = start.distanceToSqr(entity.position());
            if (distanceSquared >= closestDistanceSquared) continue;

            closestDistanceSquared = distanceSquared;
            closestTarget = entity;
        }

        return closestTarget;
    }
}
