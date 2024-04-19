package com.snoweverywhere.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.WorldChunk;

import javax.swing.text.html.BlockView;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.snoweverywhere.SnowEverywhere;

import net.minecraft.block.SnowBlock;

@Mixin(SnowBlock.class)
public class ShowShapeChange {
    @Inject(method = "getOutlineShape", at = @At("HEAD"), cancellable = true)
    public void snowOutlineChange(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<Boolean> cir){
        WorldChunk chunk = SnowEverywhere.getWorldChunk();
        BlockState down = chunk.getBlockState(pos.down());
        
    }
}
