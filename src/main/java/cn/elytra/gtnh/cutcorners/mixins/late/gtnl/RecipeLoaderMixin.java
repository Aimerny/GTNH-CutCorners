package cn.elytra.gtnh.cutcorners.mixins.late.gtnl;

import cn.elytra.gtnh.cutcorners.init.GTRecipeInit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.science.gtnl.loader.RecipeLoader", remap = false)
public class RecipeLoaderMixin {

    @Inject(method = "loadCompleteInit", at = @At("RETURN"))
    private static void gtnhcc$afterLoadCompleteInit(CallbackInfo ci) {
        GTRecipeInit.updateLateRecipes("GTNL");
    }
}
