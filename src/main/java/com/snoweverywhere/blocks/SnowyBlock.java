package com.snoweverywhere.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class SnowyBlock extends Block{
    private BlockState identity;

    protected SnowyBlock(AbstractBlock.Settings settings){
        super(settings);
    }

    public static SnowyBlock createSnowyBlock(AbstractBlock.Settings settings, BlockState identity){
        SnowyBlock block = new SnowyBlock(settings);
        block.setIdentity(identity);
        return block;
    }

    public BlockState getIdentity() {
        return identity;
    }

    public void setIdentity(BlockState identity) {
        this.identity = identity;
    }
}
