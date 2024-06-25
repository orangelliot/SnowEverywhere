package com.snoweverywhere.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.snoweverywhere.blocks.registries.SEBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SnowBlock;
import net.minecraft.server.world.ServerWorld;

@Mixin(ServerWorld.class)
public abstract class InterceptSnowWeatherPlacement{
    World world = (ServerWorld)(Object)this;

    @Inject(method = "tickIceAndSnow", at = @At(value = "TAIL"))
    public void interceptSnowWeatherPlacement(BlockPos pos, CallbackInfo ci){/*
        BlockPos topPos = world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, pos);
        BlockPos topBlock = topPos.down();
        BlockState blockState = world.getBlockState(topPos);
        if (world.isRaining()){
            if (world.getBiome(topPos).value().canSetSnow(world, topPos)){
                Boolean isFlat = Block.isFaceFullSquare(world.getBlockState(topBlock).getCollisionShape(world, topBlock), Direction.UP);
                if (blockState.isOf(SEBlocks.SNOW_EVERYWHERE_BLOCK)){
                    int snowAccumulationHeight = world.getGameRules().getInt(GameRules.SNOW_ACCUMULATION_HEIGHT);
                    int snowCurrentHeight = blockState.get(SnowBlock.LAYERS);
                    if (snowCurrentHeight < Math.min(snowAccumulationHeight, 8)) {
                        BlockState addLayer = (BlockState)blockState.with(SnowBlock.LAYERS, snowCurrentHeight + 1);
                        Block.pushEntitiesUpBeforeBlockChange(blockState, addLayer, world, topPos);
                        world.setBlockState(topPos, addLayer);
                    }
                }
                if (!isFlat){
                    world.setBlockState(topPos, SEBlocks.SNOW_EVERYWHERE_BLOCK.getDefaultState());
                }
            }
        }*/
    }
}