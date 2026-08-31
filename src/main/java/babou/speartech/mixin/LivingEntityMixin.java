package babou.speartech.mixin;

import babou.speartech.modules.LungeBoost;
import babou.speartech.modules.OneShot;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "getAttributeValue", at = @At("RETURN"), cancellable = true)
    private void spearTech$getAttributeValue(Holder<Attribute> attribute, CallbackInfoReturnable<Double> cir) {
        if (!attribute.equals(Attributes.ATTACK_DAMAGE)) return;
        if (!((Object) this instanceof Player player)) return;

        OneShot module = Modules.get().get(OneShot.class);
        if (module == null) return;

        cir.setReturnValue(module.modifyAttackDamage(player, cir.getReturnValueD()));
    }

    @Inject(method = "postPiercingAttack", at = @At("TAIL"))
    private void spearTech$postPiercingAttack(CallbackInfo ci) {
        if (!((Object) this instanceof Player player)) return;

        LungeBoost module = Modules.get().get(LungeBoost.class);
        if (module != null) module.apply(player);
    }
}
