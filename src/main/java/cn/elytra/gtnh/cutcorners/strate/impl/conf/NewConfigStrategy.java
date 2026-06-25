package cn.elytra.gtnh.cutcorners.strate.impl.conf;

import cn.elytra.gtnh.cutcorners.CutCorners;
import cn.elytra.gtnh.cutcorners.config.CutCornersConfig;
import cn.elytra.gtnh.cutcorners.config.ValueModification;
import cn.elytra.gtnh.cutcorners.strate.ICutCornerStrategy;
import cn.elytra.gtnh.cutcorners.util.ResearchStationHelper;
import cn.elytra.gtnh.cutcorners.mixins.late.gregtech.EyeOfHarmonyRecipeAccessor;
import cn.elytra.gtnh.cutcorners.mixins.late.railcraft.BlastFurnaceRecipeAccessor;
import cn.elytra.gtnh.cutcorners.mixins.late.railcraft.CokeOvenRecipeAccessor;
import gregtech.api.enums.TierEU;
import gregtech.api.recipe.RecipeMap;
import gregtech.api.util.GTRecipe;
import mods.railcraft.api.crafting.IBlastFurnaceRecipe;
import mods.railcraft.api.crafting.ICokeOvenRecipe;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import tectech.recipe.EyeOfHarmonyRecipe;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NewConfigStrategy implements ICutCornerStrategy {

    @NotNull
    private final CutCornersConfig config;

    public NewConfigStrategy(CutCornersConfig config) {
        this.config = Objects.requireNonNull(config);
    }

    private boolean shouldSkipGTRecipeMap(RecipeMap<?> recipeMap) {
        if (recipeMap == null) {
            return config.whitelistMode();
        }
        return ArrayUtils.contains(config.getGregTechBlacklistedRecipeMaps(), recipeMap.unlocalizedName)
            ^ config.whitelistMode();
    }

    @Override
    public void updateGTRecipeMap(RecipeMap<?> recipeMap) {
        if (shouldSkipGTRecipeMap(recipeMap)) {
            CutCorners.LOG.info("Skipped GT RecipeMap: {}", recipeMap.unlocalizedName);
            return;
        }

        CutCorners.LOG.info("Hacking GT Recipe Map: {}", recipeMap.unlocalizedName);

        for (GTRecipe recipe : recipeMap.getAllRecipes()) {
            updateGTRecipeUnchecked(recipe);
        }
    }

    @Override
    public void updateGTRecipe(RecipeMap<?> recipeMap, GTRecipe recipe) {
        if (shouldSkipGTRecipeMap(recipeMap)) {
            return;
        }

        updateGTRecipeUnchecked(recipe);
    }

    private void updateGTRecipeUnchecked(GTRecipe recipe) {
        recipe.mDuration = config.getDurationModification().getModifiedValue(recipe.mDuration, 1);

        if (config.useAllLVRecipes()) {
            recipe.mEUt = (int) TierEU.RECIPE_LV;
        }
    }

    @Override
    public int getGTRecipeDuration(Object instance, RecipeMap<?> recipeMap, int original) {
        return shouldSkipGTRecipeMap(recipeMap)
            ? original
            : config.getDurationModification().getModifiedValue(original, 1);
    }

    @Override
    public void updateAssemblyLineRecipeList(List<GTRecipe.RecipeAssemblyLine> recipes) {
        if (config.doesBlacklistAssemblyLine()) {
            CutCorners.LOG.info("Skipped GregTech Assembly Line Recipes");
            return;
        }

        CutCorners.LOG.info("Hacking GregTech Assembly Line Recipes");

        for (GTRecipe.RecipeAssemblyLine recipe : recipes) {
            updateAssemblyLineRecipeUnchecked(recipe);
        }
    }

    @Override
    public void updateAssemblyLineRecipe(GTRecipe.RecipeAssemblyLine recipe) {
        if (config.doesBlacklistAssemblyLine()) {
            return;
        }

        updateAssemblyLineRecipeUnchecked(recipe);
    }

    private void updateAssemblyLineRecipeUnchecked(GTRecipe.RecipeAssemblyLine recipe) {
        recipe.mDuration = config.getDurationModification().getModifiedValue(recipe.mDuration, 1);

        if (config.useAllLVRecipes()) {
            recipe.mEUt = (int) TierEU.RECIPE_LV;
        }
    }

    @Override
    public void updateEOHRecipeMap(HashMap<String, EyeOfHarmonyRecipe> recipeMap) {
        if (config.doesBlacklistEyeOfHarmony()) {
            CutCorners.LOG.info("Skipped EOH Recipes");
            return;
        }

        CutCorners.LOG.info("Hacking EOH Recipes");

        for (EyeOfHarmonyRecipe recipe : recipeMap.values()) {
            updateEOHRecipeUnchecked(recipe);
        }
    }

    @Override
    public void updateEOHRecipe(EyeOfHarmonyRecipe recipe) {
        if (config.doesBlacklistEyeOfHarmony()) {
            return;
        }

        updateEOHRecipeUnchecked(recipe);
    }

    private void updateEOHRecipeUnchecked(EyeOfHarmonyRecipe recipe) {
        var recipeAcc = (EyeOfHarmonyRecipeAccessor) recipe;
        recipeAcc.set_miningTimeSeconds(config.getDurationModification().getModifiedValue((int) recipeAcc.get_miningTimeSeconds(), 1));
        recipeAcc.set_euStartCost(config.getEOHStartEuCostModification().getModifiedValue((int) recipeAcc.get_euStartCost(), 1));
    }

    @Override
    public void updateResearchStationRecipeMap(RecipeMap<?> recipeMap) {
        if (config.doesBlacklistResearchStation()) {
            CutCorners.LOG.info("Skipped Research Station Recipes");
            return;
        }

        CutCorners.LOG.info("Hacking Research Station Recipes");

        for (GTRecipe recipe : recipeMap.getAllRecipes()) {
            updateResearchStationRecipeUnchecked(recipe);
        }
    }

    @Override
    public void updateResearchStationRecipe(GTRecipe recipe) {
        if (config.doesBlacklistResearchStation()) {
            return;
        }

        updateResearchStationRecipeUnchecked(recipe);
    }

    private void updateResearchStationRecipeUnchecked(GTRecipe recipe) {
        recipe.mDuration = config.getDurationModification().getModifiedValue(recipe.mDuration, 1);
        int amp = ResearchStationHelper.getAmp(recipe.mSpecialValue);
        recipe.mSpecialValue = ResearchStationHelper.getSpecialValueAtAmp(
            recipe.mSpecialValue,
            config.getResearchStationAmpModification().getModifiedValue(amp, 1)
        );
        int minComputation = ResearchStationHelper.getMinComputation(recipe.mSpecialValue);
        recipe.mSpecialValue = ResearchStationHelper.getSpecialValueAtMinComputation(
            recipe.mSpecialValue,
            config.getResearchStationMinComputationModification().getModifiedValue(minComputation, 1)
        );
    }

    @Override
    public int getMaxFurnaceSmeltingTime(int original) {
        return config.doesBlacklistFurnace()
            ? original
            : config.getDurationModification().getModifiedValue(original, 1);
    }

    @Override
    public int getMaxSpecialFurnaceSmeltingTime(int original) {
        // delegate to furnace.
        // to rational, it works as intended.
        // to fixed, everything is pull to a same speed, so it's not a problem.
        return getMaxFurnaceSmeltingTime(original);
    }

    @Override
    public int getMaxProgressTime(Object instance, int original) {
        return config.doesBlacklistWildcardDurationModification()
            ? original
            : config.getDurationModification().getModifiedValue(original, 1);
    }

    @Override
    public long getGTSteamTurbineOutputAmperage(Object instance, long original) {
        return config.getGTSteamTurbineOutputAmperage();
    }

    @Override
    public int getThaumcraftFurnaceSmeltingTime(int original) {
        return ICutCornerStrategy.super.getThaumcraftFurnaceSmeltingTime(original);
    }

    @Override
    public int getThaumcraftNodeRegenerationTime(int original) {
        return ICutCornerStrategy.super.getThaumcraftNodeRegenerationTime(original);
    }

    @Override
    public void updateRailcraftCokeOvenRecipe(ICokeOvenRecipe recipe) {
        if (config.doesBlacklistRailcraft()) {
            CutCorners.LOG.info("Skipped Railcraft Coke Oven Recipes");
            return;
        }

        CutCorners.LOG.info("Hacking Railcraft Coke Oven Recipes");

        var recipeAcc = (CokeOvenRecipeAccessor) recipe;
        recipeAcc.set_cookTime(config.getDurationModification().getModifiedValue(recipeAcc.get_cookTime(), 1));
    }

    @Override
    public void updateRailcraftBlastFurnaceRecipe(IBlastFurnaceRecipe recipe) {
        if (config.doesBlacklistRailcraft()) {
            CutCorners.LOG.info("Skipped Railcraft Blast Furnace Recipes");
            return;
        }

        CutCorners.LOG.info("Hacking Railcraft Blast Furnace Recipes");

        var recipeAcc = (BlastFurnaceRecipeAccessor) recipe;
        recipeAcc.set_cookTime(config.getDurationModification().getModifiedValue(recipeAcc.get_cookTime(), 1));
    }

    @Override
    public float getRailcraftWaterTankRefillRate(Object instance, float original) {
        return config.doesBlacklistRailcraft()
            ? original
            : original * config.getRailcraftWaterTankRefillRateMultiplier();
    }

    @Override
    public int getRailcraftSteamOvenCookStep(Object instance, int original) {
        return config.doesBlacklistRailcraft()
            ? original
            : Math.max(1, (int) (original * config.getRailcraftSteamOvenSpeedMultiplier()));
    }
}
