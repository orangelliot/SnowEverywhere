package com.snoweverywhere.blocks;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.snoweverywhere.SnowEverywhereMain;
import com.snoweverywhere.blocks.entities.SnowEverywhereBlockEntity;
import com.snoweverywhere.blocks.registries.SnowEverywhereBlockEntities;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SnowEverywhereBlock extends BlockWithEntity{
    public static final MapCodec<SnowEverywhereBlock> CODEC = SnowEverywhereBlock.createCodec(settings -> new SnowEverywhereBlock((AbstractBlock.Settings)settings, () -> SnowEverywhereBlockEntities.SNOW_EVERYWHERE_BLOCK_ENTITY));

    private static final int X_1 = 0;
    private static final int Z_1 = 1;
    private static final int X_2 = 2;
    private static final int Z_2 = 3;
    private static final int Y = 4;

    private static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);

    public SnowEverywhereBlock(AbstractBlock.Settings settings, Supplier<BlockEntityType<? extends SnowEverywhereBlockEntity>> supplier) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()));
    }

    @Override
    public int getOpacity(BlockState state, BlockView view, BlockPos pos) {
        return 0;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SnowEverywhereBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState state = ctx.getWorld().getBlockState(ctx.getBlockPos());
        SnowEverywhereBlockEntity entity = (SnowEverywhereBlockEntity)ctx.getWorld().getBlockEntity(ctx.getBlockPos());
        if (state.isOf(this) && entity != null) {
            NbtCompound nbt = new NbtCompound();
            entity.writeNbt(nbt);
            int layers = nbt.getInt("layers");
            nbt.putInt("layers", Math.min(8, layers + 1));
            nbt.putBoolean("notify_renderer", true);
            entity.readNbt(nbt);
            entity.markDirty();
            return state;
        }
        return this.getDefaultState();
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify){
        if(oldState.isOf(state.getBlock()) || world.isClient)
            return;
        SnowEverywhereBlockEntity entity = (SnowEverywhereBlockEntity)world.getBlockEntity(pos);
        if(entity != null){
            NbtCompound nbt = new NbtCompound();
            entity.writeNbt(nbt);
            nbt.putBoolean("notify_renderer", true);
            entity.readNbt(nbt);
            entity.markDirty();
        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state, world, pos, false);
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        return getShape(state, world, pos, false);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getShape(state, world, pos, true);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        SnowEverywhereBlockEntity entity = (SnowEverywhereBlockEntity)world.getBlockEntity(pos);
        if(entity != null){
            NbtCompound nbt = new NbtCompound();
            entity.writeNbt(nbt);
            nbt.putBoolean("notify_renderer", true);
            entity.readNbt(nbt);
            entity.markDirty();
        }
    }

    public VoxelShape getShape(BlockState state, BlockView world, BlockPos pos, Boolean collision){
        SnowEverywhereBlockEntity entity = (SnowEverywhereBlockEntity)world.getBlockEntity(pos);
        if(entity == null) return DEFAULT_SHAPE;
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        int layers = nbt.getInt("layers");
        //System.out.println("getting layers returned " + layers + " layers.\ncollision = " + collision);
        if(collision) layers--;
        byte[] obj = nbt.getByteArray("surfaces");
        if (obj.length == 0) return DEFAULT_SHAPE;
        List<float[]> surfaces = (List<float[]>) deserialize(obj);

        VoxelShape shape = null;
        for (float[] surface : surfaces) {
            if (shape == null) {
                shape = Block.createCuboidShape(
                    surface[X_1],
                    surface[Y] - 16.0f,
                    surface[Z_1],
                    surface[X_2],
                    surface[Y] + (2.0f * layers) - 16.0f,
                    surface[Z_2]);
            } else {
                shape = VoxelShapes.union(shape, Block.createCuboidShape(
                    surface[X_1],
                    surface[Y] - 16.0f,
                    surface[Z_1],
                    surface[X_2],
                    surface[Y] + (2.0f * layers) - 16.0f,
                    surface[Z_2]));
            }
        }
        return shape;
    }

    @Override
    public MapCodec<? extends SnowEverywhereBlock> getCodec() {
        return CODEC;
    }

    static Object deserialize(byte[] bytes) {
        //System.out.println("deserializing : " + Arrays.toString(bytes));
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (Exception ex) {
            //System.out.println("Exception in deserializing: " + ex);
            //return null;
            throw new RuntimeException(ex);
        }
    }
}