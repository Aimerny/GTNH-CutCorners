package cn.elytra.gtnh.cutcorners.mixins.late.gregtech;

import cn.elytra.gtnh.cutcorners.CutCorners;
import gregtech.common.tileentities.machines.basic.MTEPump;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MTEPump.class, remap = false)
public abstract class GT_MetaTileEntity_PumpMixin {

    @Redirect(
        method = "onPostTick",
        at = @At(
            value = "FIELD",
            target = "Lgregtech/common/tileentities/machines/basic/MTEPump;mPumpTimer:I",
            opcode = Opcodes.PUTFIELD,
            ordinal = 0))
    private void gtnhcc$acceleratePumpTimer(MTEPump instance, int value) {
        instance.mPumpTimer = Math.max(1, CutCorners.getStrategy().getMaxProgressTime(instance, value));
    }
}
