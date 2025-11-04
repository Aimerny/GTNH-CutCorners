package cn.elytra.gtnh.cutcorners.init;

import cn.elytra.gtnh.cutcorners.CutCorners;
import cpw.mods.fml.common.Loader;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;
import tectech.TecTech;
import tectech.recipe.EyeOfHarmonyRecipe;
import tectech.recipe.EyeOfHarmonyRecipeStorage;
import tectech.recipe.TecTechRecipeMaps;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class GTRecipeInit {

    private static final Set<String> UPDATED_RECIPE_MAPS = new HashSet<>();

    private static final String GTNL_MODID = "sciencenotleisure";
    private static final String GTNL_RECIPE_POOL_CLASS = "com.science.gtnl.loader.RecipePool";
    private static final String GTNL_RECIPE_PREFIX = "gtnl.";

    public static void init() {
        updateGeneralRecipes();
        updateOptionalRecipeMaps();
        updateAssemblyLineRecipes();
        updateEOHRecipes();
        updateResearchStationRecipes();
    }

    private static void updateGeneralRecipes() {
        RecipeMap.ALL_RECIPE_MAPS.forEach((s, map) -> applyStrategyToRecipeMap(map));
    }

    private static void updateAssemblyLineRecipes() {
        CutCorners.LOG.info("Updating Assembly Line Recipes");
        CutCorners.getStrategy().updateAssemblyLineRecipeList(GTRecipe.RecipeAssemblyLine.sAssemblylineRecipes);
    }

    private static void updateEOHRecipes() {
        CutCorners.LOG.info("Updating Eye of Harmony Recipes");
        applyStrategyToRecipeMap(TecTechRecipeMaps.eyeOfHarmonyRecipes);

        var recipeMap = getRecipeHashMap(TecTech.eyeOfHarmonyRecipeStorage);
        CutCorners.getStrategy().updateEOHRecipeMap(recipeMap);
    }

    private static void updateResearchStationRecipes() {
        CutCorners.LOG.info("Updating Research Station Recipes");
        applyStrategyToRecipeMap(TecTechRecipeMaps.researchStationFakeRecipes);
        CutCorners.getStrategy().updateResearchStationRecipeMap(TecTechRecipeMaps.researchStationFakeRecipes);
    }

    private static void updateOptionalRecipeMaps() {
        if (!Loader.isModLoaded(GTNL_MODID)) {
            return;
        }

        try {
            Class<?> recipePoolClass = Class.forName(GTNL_RECIPE_POOL_CLASS);
            for (Field field : recipePoolClass.getDeclaredFields()) {
                if (!RecipeMap.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                field.setAccessible(true);
                Object value = field.get(null);
                if (!(value instanceof RecipeMap<?> recipeMap)) {
                    continue;
                }
                if (recipeMap.unlocalizedName == null || !recipeMap.unlocalizedName.startsWith(GTNL_RECIPE_PREFIX)) {
                    continue;
                }
                applyStrategyToRecipeMap(recipeMap);
            }
        } catch (ClassNotFoundException e) {
            CutCorners.LOG.warn("GT-Not-Leisure recipe pool class not found, skipping optional integration.");
        } catch (ReflectiveOperationException e) {
            CutCorners.LOG.error("Failed to update GT-Not-Leisure recipes", e);
        }
    }

    private static void applyStrategyToRecipeMap(RecipeMap<?> recipeMap) {
        if (recipeMap == null) {
            return;
        }
        CutCorners.getStrategy().updateGTRecipeMap(recipeMap);
        if (recipeMap.unlocalizedName != null) {
            UPDATED_RECIPE_MAPS.add(recipeMap.unlocalizedName);
        }
    }

    public static boolean isRecipeMapUpdated(String recipeMapName) {
        return recipeMapName != null && UPDATED_RECIPE_MAPS.contains(recipeMapName);
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
