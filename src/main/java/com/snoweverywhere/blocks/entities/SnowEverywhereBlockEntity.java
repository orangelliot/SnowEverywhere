package com.snoweverywhere.blocks.entities;

import org.jetbrains.annotations.Nullable;

import com.snoweverywhere.blocks.registries.SnowEverywhereBlockEntities;

import java.util.Arrays;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;

public class SnowEverywhereBlockEntity extends BlockEntity{
    boolean notify_block = true;
    boolean notify_renderer = true;
    int layers = 1;
    byte[] surfaces;
    
    public SnowEverywhereBlockEntity(BlockPos pos, BlockState state) {
        super(SnowEverywhereBlockEntities.SNOW_EVERYWHERE_BLOCK_ENTITY, pos, state);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putBoolean("notify_block", notify_block);
        nbt.putBoolean("notify_renderer", notify_renderer);
        nbt.putInt("layers", layers);
        nbt.putByteArray("surfaces", surfaces);
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        notify_block = nbt.getBoolean("notify_block");
        notify_renderer = nbt.getBoolean("notify_renderer");
        layers = nbt.getInt("layers");
        surfaces = nbt.getByteArray("surfaces");
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
