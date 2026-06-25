package cn.elytra.gtnh.cutcorners.mixins.late.gregtech;

import cn.elytra.gtnh.cutcorners.CutCorners;
import gregtech.common.tileentities.generators.MTESteamTurbine;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = MTESteamTurbine.class, remap = false)
public class MTESteamTurbineMixin {

    public long maxAmperesOut() {
        return CutCorners.getStrategy().getGTSteamTurbineOutputAmperage(this, 1L);
    }
}
