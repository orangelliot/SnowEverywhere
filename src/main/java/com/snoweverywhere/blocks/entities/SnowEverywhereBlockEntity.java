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
    public static final int MAX_LAYERS = 8;
    public static final int MIN_LAYERS = 1;

    boolean notify_renderer;
    int layers;
    byte[] surfaces;
    
    public SnowEverywhereBlockEntity(BlockPos pos, BlockState state) {
        super(SnowEverywhereBlockEntities.SNOW_EVERYWHERE_BLOCK_ENTITY, pos, state);
        this.notify_renderer = true;
        this.layers = 1;
        this.surfaces = new byte[0];
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        System.out.println("layers = " + layers);
        nbt.putBoolean("notify_renderer", notify_renderer);
        nbt.putInt("layers", layers);
        nbt.putByteArray("surfaces", surfaces);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        //System.out.println("reading NBT at : " + this.getPos().toString());
        //System.out.println("NBT before :\nnotify block : " + notify_block + "\nnotify renderer : " + notify_renderer + "\nlayers : " + layers + "\nsurfaces : " + Arrays.toString(surfaces));s
        notify_renderer = nbt.getBoolean("notify_renderer");
        layers = nbt.getInt("layers");
        if(layers < MIN_LAYERS) layers = MIN_LAYERS;
        if(layers > MAX_LAYERS) layers = MAX_LAYERS;
        surfaces = nbt.getByteArray("surfaces");
        //System.out.println("NBT after :\nnotify block : " + notify_block + "\nnotify renderer : " + notify_renderer + "\nlayers : " + layers + "\nsurfaces : " + Arrays.toString(surfaces));
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

    public int getLayers(){
        return layers;
    }
}
