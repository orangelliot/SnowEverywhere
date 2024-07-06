package com.snoweverywhere.blocks.entities;

import org.jetbrains.annotations.Nullable;

import com.snoweverywhere.blocks.registries.SnowEverywhereBlockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public class SnowEverywhereBlockEntity extends BlockEntity{
    private int layers;
    private byte[] surfaces;
    
    public SnowEverywhereBlockEntity(BlockPos pos, BlockState state) {
        super(SnowEverywhereBlockEntities.SNOW_EVERYWHERE_BLOCK_ENTITY, pos, state);
        layers = 1;
        surfaces = new byte[0];
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        System.out.println("Writing layers with : " + layers);
        nbt.putInt("layers", layers);
        nbt.putByteArray("surfaces", surfaces);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        //System.out.println("reading NBT at : " + this.getPos().toString());
        //System.out.println("NBT before :\nnotify block : " + notify_block + "\nnotify renderer : " + notify_renderer + "\nlayers : " + layers + "\nsurfaces : " + Arrays.toString(surfaces));
        layers = nbt.getInt("layers");
        surfaces = nbt.getByteArray("surfaces");
        //System.out.println("NBT after :\nnotify block : " + notify_block + "\nnotify renderer : " + notify_renderer + "\nlayers : " + layers + "\nsurfaces : " + Arrays.toString(surfaces));
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
