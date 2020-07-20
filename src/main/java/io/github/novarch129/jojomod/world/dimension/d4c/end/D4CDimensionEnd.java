package io.github.novarch129.jojomod.world.dimension.d4c.end;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.EndDimension;

@MethodsReturnNonnullByDefault
public class D4CDimensionEnd extends EndDimension {
    public D4CDimensionEnd(World worldIn, DimensionType typeIn) {
        super(worldIn, typeIn);
    }

    @Override
    public SleepResult canSleepAt(PlayerEntity player, BlockPos pos) {
        return SleepResult.BED_EXPLODES;
    }
}
