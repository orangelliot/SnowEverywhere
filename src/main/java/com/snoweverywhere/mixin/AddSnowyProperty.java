package com.snoweverywhere.mixin;

import net.minecraft.state.StateManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.snoweverywhere.blocks.registries.SEProperties;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

@Mixin(Block.class)
public class AddSnowyProperty {
    Block block = (Block) (Object) this;
    @Shadow private BlockState defaultState;

    @Inject(method = "<init>", at = @At(value="INVOKE", target="Lnet/minecraft/block/Block;appendProperties(Lnet/minecraft/state/StateManager$Builder;)V", shift=At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = false)
    public void appendProperties(AbstractBlock.Settings settings, CallbackInfo ci, StateManager.Builder<Block, BlockState> builder) {
        if(builder != null && !AbstractFireBlock.class.isAssignableFrom(block.getClass())) {
            System.out.println(block.getClass().getSimpleName());
            try {
                builder.add(SEProperties.SNOWCOVERED, SEProperties.SNOWLOGGED);
            } catch (Exception e) { System.out.println("caught exception " + e.toString()); }
        }
    }

    @Inject(method = "getDefaultState", at = @At("HEAD"), cancellable = true)
    private void getDefaultState(CallbackInfoReturnable<BlockState> cir) {
        if(defaultState != null){
            cir.setReturnValue(defaultState.withIfExists(SEProperties.SNOWLOGGED, false).withIfExists(SEProperties.SNOWCOVERED, false));
        }
    }
}