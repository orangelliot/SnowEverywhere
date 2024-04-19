package com.snoweverywhere.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.snoweverywhere.SnowEverywhere;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

@Mixin(World.class)
public class CaptureWorldChunk {
    @Inject(method = "setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;II)Z", at = @At("TAIL"), cancellable = true)
    public void captureWorldChunk(BlockPos pos, BlockState state, int flags, int maxUpdateDepth, CallbackInfoReturnable<Boolean> cir, @This World thisObject){
        if (this.isOutOfHeightLimit(pos)) {
            cir.setReturnValue(false);
        }
        if (!this.isClient && this.isDebugWorld()) {
            return false;
        }
        SnowEverywhere.setWorldChunk(worldChunk);
    }
}
