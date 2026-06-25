package cn.elytra.gtnh.cutcorners.mixins.late.railcraft;

import cn.elytra.gtnh.cutcorners.CutCorners;
import mods.railcraft.common.blocks.machine.alpha.TileTankWater;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = TileTankWater.class, remap = false)
public class TileTankWaterMixin {

    @ModifyConstant(method = "updateEntity", constant = @Constant(floatValue = 10.0F))
    private float gtnhcc$modifyRefillRate(float value) {
        return CutCorners.getStrategy().getRailcraftWaterTankRefillRate(this, value);
    }
}
