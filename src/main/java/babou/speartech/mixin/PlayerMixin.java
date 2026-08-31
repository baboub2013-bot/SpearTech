package babou.speartech.mixin;

import babou.speartech.modules.SpearReach;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Lets SpearReach adjust Player.entityInteractionRange() after Minecraft
 * computes the normal value.
 */
@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "entityInteractionRange", at = @At("RETURN"), cancellable = true)
    private void spearTech$modifyInteractionRange(CallbackInfoReturnable<Double> cir) {
        SpearReach spearReach = Modules.get().get(SpearReach.class);
        if (spearReach == null) return;

        Player player = (Player) (Object) this;
        cir.setReturnValue(spearReach.modifyRange(player, cir.getReturnValueD()));
    }
}
