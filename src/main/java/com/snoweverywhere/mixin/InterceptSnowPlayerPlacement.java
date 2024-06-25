package com.snoweverywhere.mixin;

import com.snoweverywhere.blocks.SnowEverywhereBlock;
import com.snoweverywhere.blocks.registries.SEBlocks;
import com.snoweverywhere.blocks.registries.SEProperties;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraft.block.SnowBlock;

@Mixin(SnowBlock.class)
public abstract class InterceptSnowPlayerPlacement {
    //SnowBlock snowblock = (SnowBlock) Blocks.SNOW;
    //SnowEverywhereBlock snoweverywhereblock = (SnowEverywhereBlock) SEBlocks.SNOW_EVERYWHERE_BLOCK;
    //@Shadow public static @Final IntProperty LAYERS;

    @Inject(method = "getPlacementState", at = @At("HEAD"), cancellable = true)
    public void interceptSnowPlayerPlacement(ItemPlacementContext ctx, CallbackInfoReturnable<BlockState> cir){
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos().down();
        BlockState blockState = world.getBlockState(pos);
        try{
            boolean snowlogged = blockState.get(SEProperties.SNOWLOGGED);
            if(snowlogged){
                System.out.println("Set property \"SNOWCOVERED\" to true for block at " + pos.toString());
                world.setBlockState(pos, blockState.with(SEProperties.SNOWCOVERED, true));
            }
            System.out.println("Set property \"SNOWLOGGED\" to true for block at " + pos.toString());
            world.setBlockState(pos, blockState.with(SEProperties.SNOWLOGGED, true));
        } catch (Exception e){
            System.out.println("Could not find property \"SNOWLOGGED\" for block at " + pos.toString());
        }
    }
}