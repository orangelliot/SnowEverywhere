package com.snoweverywhere.blocks;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

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
import net.minecraft.block.SnowBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SnowEverywhereBlock extends BlockWithEntity{
    public static final MapCodec<SnowEverywhereBlock> CODEC = SnowEverywhereBlock.createCodec(settings -> new SnowEverywhereBlock((AbstractBlock.Settings)settings, () -> SnowEverywhereBlockEntities.SNOW_EVERYWHERE_BLOCK_ENTITY));
    public static final IntProperty LAYERS = Properties.LAYERS;
    public static final int MAX_LAYERS = 8;
    private NbtCompound nbt;

    private static final int X_1 = 0;
    private static final int Z_1 = 1;
    private static final int X_2 = 2;
    private static final int Z_2 = 3;
    private static final int Y = 4;

    private static final VoxelShape DEFAULT_SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 16.0, 16.0);
    private VoxelShape savedShape = DEFAULT_SHAPE;
    private VoxelShape savedCollisionShape = DEFAULT_SHAPE;

    public SnowEverywhereBlock(AbstractBlock.Settings settings, Supplier<BlockEntityType<? extends SnowEverywhereBlockEntity>> supplier) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(LAYERS, 1));
        nbt = new NbtCompound();
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
        BlockState blockState = ctx.getWorld().getBlockState(ctx.getBlockPos());
        if (blockState.isOf(this)) {
            int i = blockState.get(SnowBlock.LAYERS);
            SnowEverywhereBlockEntity entity = (SnowEverywhereBlockEntity)ctx.getWorld().getBlockEntity(ctx.getBlockPos());
            if(entity != null){
                nbt.putInt("layers", Math.min(8, i + 1));
                entity.readNbt(nbt);
                entity.markDirty();
            }
            return (BlockState)blockState.with(SnowBlock.LAYERS, Math.min(8, i + 1));
        }
        return this.getDefaultState();
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify){
        if(oldState.isOf(state.getBlock()) || world.isClient)
            return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof SnowEverywhereBlockEntity){

        }
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        updateShapes(state, world, pos);
        return savedShape;
    }

    @Override
    public VoxelShape getRaycastShape(BlockState state, BlockView world, BlockPos pos) {
        updateShapes(state, world, pos);
        return savedShape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        updateShapes(state, world, pos);
        return savedCollisionShape;
    }

    public void updateShapes(BlockState state, BlockView world, BlockPos pos){
        SnowEverywhereBlockEntity entity = (SnowEverywhereBlockEntity)world.getBlockEntity(pos);
        if(entity != null){
            entity.writeNbt(nbt);
            if(nbt.getBoolean("notify_block")){
                savedShape = getShape(entity, state.get(LAYERS));
                savedCollisionShape = getShape(entity, state.get(LAYERS) - 1);
                nbt.putBoolean("notify_block", false);
                entity.readNbt(nbt);
                entity.markDirty();
            }
        }
    }

    public VoxelShape getShape(SnowEverywhereBlockEntity entity, int layers) {
            entity.writeNbt(nbt);
            byte[] obj = nbt.getByteArray("surfaces");
            if(obj == null)
                return DEFAULT_SHAPE;
            List<float[]> surfaces = (List<float[]>) deserialize(obj);

            System.out.println("=================");
            for(float[] surface : surfaces){
                System.out.println(Arrays.toString(surface));
            }

            VoxelShape shape = null;

            for (float[] surface : surfaces) {
                if (shape == null) {
                    shape = Block.createCuboidShape(surface[X_1], surface[Y] - 16.0f, surface[Z_1], surface[X_2],
                            surface[Y] + (2.0f * layers) - 16.0f, surface[Z_2]);
                } else {
                    shape = VoxelShapes.union(shape, Block.createCuboidShape(surface[X_1], surface[Y] - 16.0f, surface[Z_1],
                            surface[X_2], surface[Y] + (2.0f * layers) - 16.0f, surface[Z_2]));
                }
            }
            return shape;
    }

    @Override
    public MapCodec<? extends SnowEverywhereBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    static Object deserialize(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

        try (ObjectInput in = new ObjectInputStream(bis)) {
            return in.readObject();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}