package cn.elytra.gtnh.cutcorners.init;

import cn.elytra.gtnh.cutcorners.CutCorners;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;
import tectech.TecTech;
import tectech.recipe.EyeOfHarmonyRecipe;
import tectech.recipe.EyeOfHarmonyRecipeStorage;
import tectech.recipe.TecTechRecipeMaps;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Set;

public class GTRecipeInit {

    private static final Set<GTRecipe> UPDATED_GT_RECIPES = newIdentitySet();
    private static final Set<GTRecipe.RecipeAssemblyLine> UPDATED_ASSEMBLY_LINE_RECIPES = newIdentitySet();
    private static final Set<EyeOfHarmonyRecipe> UPDATED_EOH_RECIPES = newIdentitySet();
    private static final Set<GTRecipe> UPDATED_RESEARCH_STATION_RECIPES = newIdentitySet();

    public static void init() {
        updateGeneralRecipes();
        updateAssemblyLineRecipes();
        updateEOHRecipes();
        updateResearchStationRecipes();
    }

    public static void updateLateRecipes(String source) {
        CutCorners.LOG.info("Updating late GregTech recipes from {}", source);
        updateNewGeneralRecipes();
        updateNewAssemblyLineRecipes();
        updateNewEOHRecipes();
        updateNewResearchStationRecipes();
    }

    private static void updateGeneralRecipes() {
        RecipeMap.ALL_RECIPE_MAPS.forEach((s, map) -> {
            CutCorners.getStrategy().updateGTRecipeMap(map);
            rememberGTRecipes(map);
        });
    }

    private static void updateAssemblyLineRecipes() {
        CutCorners.LOG.info("Updating Assembly Line Recipes");
        CutCorners.getStrategy().updateAssemblyLineRecipeList(GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes);
        rememberAssemblyLineRecipes(GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes);
    }

    private static void updateEOHRecipes() {
        CutCorners.LOG.info("Updating Eye of Harmony Recipes");
        CutCorners.getStrategy().updateGTRecipeMap(TecTechRecipeMaps.eyeOfHarmonyRecipes);
        rememberGTRecipes(TecTechRecipeMaps.eyeOfHarmonyRecipes);

        var recipeMap = getRecipeHashMap(TecTech.eyeOfHarmonyRecipeStorage);
        CutCorners.getStrategy().updateEOHRecipeMap(recipeMap);
        rememberEOHRecipes(recipeMap.values());
    }

    private static void updateResearchStationRecipes() {
        CutCorners.LOG.info("Updating Research Station Recipes");
        CutCorners.getStrategy().updateResearchStationRecipeMap(TecTechRecipeMaps.researchStationFakeRecipes);
        rememberResearchStationRecipes(TecTechRecipeMaps.researchStationFakeRecipes);
    }

    private static void updateNewGeneralRecipes() {
        RecipeMap.ALL_RECIPE_MAPS.forEach((s, map) -> updateNewGTRecipeMap(map));
    }

    private static void updateNewGTRecipeMap(RecipeMap<?> recipeMap) {
        if (recipeMap == null) {
            return;
        }

        int updated = 0;
        for (GTRecipe recipe : recipeMap.getAllRecipes()) {
            if (recipe == null || UPDATED_GT_RECIPES.contains(recipe)) {
                continue;
            }

            CutCorners.getStrategy().updateGTRecipe(recipeMap, recipe);
            UPDATED_GT_RECIPES.add(recipe);
            updated++;
        }

        if (updated > 0) {
            CutCorners.LOG.info("Updated {} late GT recipes in RecipeMap: {}", updated, recipeMap.unlocalizedName);
        }
    }

    private static void updateNewAssemblyLineRecipes() {
        int updated = 0;
        for (GTRecipe.RecipeAssemblyLine recipe : GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes) {
            if (recipe == null || UPDATED_ASSEMBLY_LINE_RECIPES.contains(recipe)) {
                continue;
            }

            CutCorners.getStrategy().updateAssemblyLineRecipe(recipe);
            UPDATED_ASSEMBLY_LINE_RECIPES.add(recipe);
            updated++;
        }

        if (updated > 0) {
            CutCorners.LOG.info("Updated {} late Assembly Line recipes", updated);
        }
    }

    private static void updateNewEOHRecipes() {
        updateNewGTRecipeMap(TecTechRecipeMaps.eyeOfHarmonyRecipes);

        int updated = 0;
        for (EyeOfHarmonyRecipe recipe : getRecipeHashMap(TecTech.eyeOfHarmonyRecipeStorage).values()) {
            if (recipe == null || UPDATED_EOH_RECIPES.contains(recipe)) {
                continue;
            }

            CutCorners.getStrategy().updateEOHRecipe(recipe);
            UPDATED_EOH_RECIPES.add(recipe);
            updated++;
        }

        if (updated > 0) {
            CutCorners.LOG.info("Updated {} late Eye of Harmony recipes", updated);
        }
    }

    private static void updateNewResearchStationRecipes() {
        int updated = 0;
        for (GTRecipe recipe : TecTechRecipeMaps.researchStationFakeRecipes.getAllRecipes()) {
            if (recipe == null || UPDATED_RESEARCH_STATION_RECIPES.contains(recipe)) {
                continue;
            }

            CutCorners.getStrategy().updateResearchStationRecipe(recipe);
            UPDATED_RESEARCH_STATION_RECIPES.add(recipe);
            updated++;
        }

        if (updated > 0) {
            CutCorners.LOG.info("Updated {} late Research Station recipes", updated);
        }
    }

    private static void rememberGTRecipes(RecipeMap<?> recipeMap) {
        if (recipeMap == null) {
            return;
        }

        for (GTRecipe recipe : recipeMap.getAllRecipes()) {
            if (recipe != null) {
                UPDATED_GT_RECIPES.add(recipe);
            }
        }
    }

    private static void rememberAssemblyLineRecipes(Collection<GTRecipe.RecipeAssemblyLine> recipes) {
        UPDATED_ASSEMBLY_LINE_RECIPES.addAll(recipes);
    }

    private static void rememberEOHRecipes(Collection<EyeOfHarmonyRecipe> recipes) {
        UPDATED_EOH_RECIPES.addAll(recipes);
    }

    private static void rememberResearchStationRecipes(RecipeMap<?> recipeMap) {
        if (recipeMap == null) {
            return;
        }

        for (GTRecipe recipe : recipeMap.getAllRecipes()) {
            if (recipe != null) {
                UPDATED_RESEARCH_STATION_RECIPES.add(recipe);
            }
        }
    }

    private static <T> Set<T> newIdentitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }

    private static final Field FIELD_RECIPE_HASH_MAP;

    static {
        try {
            FIELD_RECIPE_HASH_MAP = TecTech.eyeOfHarmonyRecipeStorage.getClass().getDeclaredField("recipeHashMap");
            FIELD_RECIPE_HASH_MAP.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, EyeOfHarmonyRecipe> getRecipeHashMap(EyeOfHarmonyRecipeStorage storage) {
        try {
            return (HashMap<String, EyeOfHarmonyRecipe>) FIELD_RECIPE_HASH_MAP.get(storage);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
