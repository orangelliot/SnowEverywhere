package com.snoweverywhere.blocks.entities;

import org.jetbrains.annotations.Nullable;

import com.snoweverywhere.blocks.registries.SnowEverywhereBlockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class SnowEverywhereBlockEntity extends BlockEntity{
    boolean notify = true;
    int layers = 1;
    
    public SnowEverywhereBlockEntity(BlockPos pos, BlockState state) {
        super(SnowEverywhereBlockEntities.SNOW_EVERYWHERE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("notify", notify);
        nbt.putInt("layers", layers);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        notify = nbt.getBoolean("notify");
        layers = nbt.getInt("layers");
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
