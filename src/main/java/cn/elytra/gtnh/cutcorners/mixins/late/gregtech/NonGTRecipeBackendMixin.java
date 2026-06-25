package cn.elytra.gtnh.cutcorners.mixins.late.gregtech;

import cn.elytra.gtnh.cutcorners.CutCorners;
import gregtech.api.recipe.maps.EFRBlastingBackend;
import gregtech.api.recipe.maps.EFRSmokingBackend;
import gregtech.api.recipe.maps.FurnaceBackend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = { FurnaceBackend.class, EFRBlastingBackend.class, EFRSmokingBackend.class }, remap = false)
public class NonGTRecipeBackendMixin {

    @ModifyArg(method = "overwriteFindRecipe", at = @At(value = "INVOKE", target = "Lgregtech/api/util/GTRecipeBuilder;duration(I)Lgregtech/api/util/GTRecipeBuilder;"), index = 0)
    private int gtnhcc$modifyDynamicRecipeDuration(int duration) {
        return CutCorners.getStrategy().getGTRecipeDuration(this, ((RecipeMapBackendAccessor) this).gtnhcc$getRecipeMap(), duration);
    }
}
