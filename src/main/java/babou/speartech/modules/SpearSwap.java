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

public class SpearSwap extends SpearModule {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgSmart = settings.createGroup("Smart Swap");

    private final Setting<Boolean> lungeOnMiss = sgSmart.add(new BoolSetting.Builder()
        .name("lunge-on-miss")
        .description("Swaps to a Lunge spear when attacking without a normal in-range target.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> reachSpear = sgSmart.add(new BoolSetting.Builder()
        .name("reach-spear")
        .description("Prefers a non-Lunge spear when a target is farther than the current normal interaction range.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> excludeLungeFromReach = sgSmart.add(new BoolSetting.Builder()
        .name("exclude-lunge-from-reach")
        .description("Does not pick a Lunge spear for the reach swap.")
        .defaultValue(true)
        .visible(reachSpear::get)
        .build()
    );

    private final Setting<Integer> scanRange = sgSmart.add(new IntSetting.Builder()
        .name("scan-range")
        .description("Distance used to look for a spear target in front of you.")
        .defaultValue(8)
        .min(3)
        .max(20)
        .sliderRange(3, 12)
        .visible(reachSpear::get)
        .build()
    );

    private final Setting<Boolean> swapBack = sgGeneral.add(new BoolSetting.Builder()
        .name("swap-back")
        .description("Returns to the original slot after swapping.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> swapBackDelay = sgGeneral.add(new IntSetting.Builder()
        .name("swap-back-delay")
        .description("Ticks to wait before swapping back.")
        .defaultValue(2)
        .min(0)
        .max(20)
        .sliderRange(0, 10)
        .visible(swapBack::get)
        .build()
    );

    private int backTimer;
    private boolean awaitingBack;

    public SpearSwap() {
        super("spear-swap", "Meteor-style smart hotbar swapping for spear reach and Lunge movement.");
    }

    @Override
    public void onDeactivate() {
        if (awaitingBack) InvUtils.swapBack();
        awaitingBack = false;
        backTimer = 0;
    }

    @EventHandler
    private void onAttack(DoAttackEvent event) {
        if (!canRun() || awaitingBack || mc.hitResult == null) return;
        if (mc.hitResult.getType() == HitResult.Type.BLOCK) return;

        if (reachSpear.get()) {
            Entity target = getTargetEntity();
            if (target != null && mc.player.distanceTo(target) > mc.player.entityInteractionRange() + 0.5) {
                int slot = getSpearSlot(false);
                if (slot != -1) {
                    doSwap(slot);
                    return;
                }
            }
        }

        if (lungeOnMiss.get()) {
            int slot = getSpearSlot(true);
            if (slot != -1) doSwap(slot);
        }
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (!awaitingBack) return;
        if (!WorldGuard.canModifyGameplay()) {
            InvUtils.swapBack();
            awaitingBack = false;
            return;
        }

        if (backTimer-- > 0) return;

        InvUtils.swapBack();
        awaitingBack = false;
    }

    private void doSwap(int slot) {
        if (slot < 0 || slot > 8) return;
        if (slot == mc.player.getInventory().getSelectedSlot()) return;
        if (!InvUtils.swap(slot, swapBack.get())) return;

        awaitingBack = swapBack.get();
        if (awaitingBack) backTimer = swapBackDelay.get();
    }

    private int getSpearSlot(boolean requireLunge) {
        int bestSlot = -1;
        int bestLevel = -1;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = mc.player.getInventory().getItem(i);
            if (!stack.is(ItemTags.SPEARS)) continue;

            int lunge = Utils.getEnchantmentLevel(stack, Enchantments.LUNGE);
            if (requireLunge && lunge <= 0) continue;
            if (!requireLunge && excludeLungeFromReach.get() && lunge > 0) continue;

            if (lunge > bestLevel) {
                bestLevel = lunge;
                bestSlot = i;
            }
        }

        return bestSlot;
    }

    private Entity getTargetEntity() {
        double maxDistance = scanRange.get();
        Vec3 start = mc.player.getEyePosition(1.0F);
        Vec3 look = mc.player.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(maxDistance));

        AABB searchBox = mc.player.getBoundingBox().expandTowards(look.scale(maxDistance)).inflate(1.0);
        Entity closest = null;
        double closestDistance = maxDistance * maxDistance;

        for (Entity entity : mc.level.getEntities(mc.player, searchBox, e -> !e.isSpectator() && e.isPickable())) {
            AABB hitbox = entity.getBoundingBox().inflate(0.15);
            if (hitbox.clip(start, end).isEmpty()) continue;

            double distance = start.distanceToSqr(entity.position());
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = entity;
            }
        }

        return closest;
    }
}
