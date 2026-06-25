package cn.elytra.gtnh.cutcorners.mixins.late.railcraft;

import cn.elytra.gtnh.cutcorners.CutCorners;
import mods.railcraft.common.blocks.machine.alpha.TileSteamOven;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = TileSteamOven.class, remap = false)
public class TileSteamOvenMixin {

    @ModifyConstant(method = "updateEntity", constant = @Constant(intValue = 16, ordinal = 2))
    private int gtnhcc$modifyCookStep(int value) {
        return CutCorners.getStrategy().getRailcraftSteamOvenCookStep(this, value);
    }
}
