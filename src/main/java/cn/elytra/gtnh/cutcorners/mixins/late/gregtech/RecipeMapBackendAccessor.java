package cn.elytra.gtnh.cutcorners.mixins.late.gregtech;

import gregtech.api.recipe.RecipeMap;
import gregtech.api.recipe.RecipeMapBackend;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = RecipeMapBackend.class, remap = false)
public interface RecipeMapBackendAccessor {

    @Accessor("recipeMap")
    RecipeMap<?> gtnhcc$getRecipeMap();
}
