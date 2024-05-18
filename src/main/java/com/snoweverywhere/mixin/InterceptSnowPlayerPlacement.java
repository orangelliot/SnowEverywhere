package com.snoweverywhere.mixin;

import com.snoweverywhere.blocks.SnowEverywhereBlock;
import com.snoweverywhere.blocks.registries.SnowEverywhereBlocks;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import net.minecraft.block.SnowBlock;

@Mixin(SnowBlock.class)
public abstract class InterceptSnowPlayerPlacement {
    SnowBlock snowblock = (SnowBlock) Blocks.SNOW;
    SnowEverywhereBlock snoweverywhereblock = (SnowEverywhereBlock) SnowEverywhereBlocks.SNOW_EVERYWHERE_BLOCK;
    @Shadow public static @Final IntProperty LAYERS;

    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    public void interceptSnowPlayerPlacement(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir){
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        BlockState blockState = world.getBlockState(pos);
        Boolean isFlat = Block.isFaceFullSquare(world.getBlockState(pos.down()).getCollisionShape(world, pos.down()), Direction.UP);
        if (blockState.isOf(snoweverywhereblock)) {
            cir.setReturnValue(snoweverywhereblock.getPlacementState(ctx));
        }
        if (!blockState.isOf(snowblock) && !isFlat) {
            cir.setReturnValue(snoweverywhereblock.getDefaultState());
        }
    }
}