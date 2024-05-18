package com.snoweverywhere;

import com.snoweverywhere.blocks.entities.SnowEverywhereBlockEntity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.block.entity.LightmapCoordinatesRetriever;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class SnowEverywhereBlockEntityRenderer
implements BlockEntityRenderer<SnowEverywhereBlockEntity>{
    public NbtCompound nbt = new NbtCompound();
    public ModelData modelData = new ModelData();
    private final BakedModelManager bakedModelManager = MinecraftClient.getInstance().getBakedModelManager();
    private final Random random = Random.create();

    public SnowEverywhereBlockEntityRenderer(BlockEntityRendererFactory.Context ctx){}

    @Override
    public void render(SnowEverywhereBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){
        entity.writeNbt(nbt);
        Boolean notify = nbt.getBoolean("notify");
        if(notify){
            nbt.putBoolean("notify", false);
            entity.readNbt(nbt);
            BlockPos below = entity.getPos().down();
            World world = entity.getWorld();
            BlockState stateBelow = world.getBlockState(below);
            SpriteIdentifier spriteIdentifier = SnowEverywhereClient.SNOW_SPRITE_IDENTIFIER;
            VertexConsumer vertexConsumer = spriteIdentifier.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
            TexturedModelData modelData = getTexturedModelData(stateBelow, world);
            ModelPart modelPart = modelData.createModel();
            modelPart.render(matrices, vertexConsumer, light, overlay);
        }
    }

    public TexturedModelData getTexturedModelData(BlockState state, World world) {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        List<BakedQuad> quads = getBakedQuads(state, world);
        for(BakedQuad quad : quads){
            float[] topSurface = getQuadTopSurface(quad.getVertexData());
            modelPartData.addChild("test", ModelPartBuilder.create().uv(0, 19).cuboid(0.0f, 0.0f, -(1 - topSurface[3]), topSurface[0] - topSurface[3], topSurface[1] - topSurface[4], 2.0f), ModelTransform.NONE);
        }
        return TexturedModelData.of(modelData, 64, 64);
    }

    private List<BakedQuad> getBakedQuads(BlockState state, World world) {
        ModelIdentifier belowModelId = BlockModels.getModelId(state);
        BakedModel belowModel = bakedModelManager.getModel(belowModelId);
        return belowModel.getQuads(state, null, random);
    }

    //private float[] getQuadDimensions(int[] vertexData) {
    private float[] getQuadTopSurface(int[] vertexData) {
        float f = 32.0f;
        float g = 32.0f;
        float h = 32.0f;
        float i = -32.0f;
        float j = -32.0f;
        float k = -32.0f;
        for (int l = 0; l < 4; ++l) {
            float m = Float.intBitsToFloat(vertexData[l * 8]);
            float n = Float.intBitsToFloat(vertexData[l * 8 + 1]);
            float o = Float.intBitsToFloat(vertexData[l * 8 + 2]);
            f = Math.min(f, m);
            g = Math.min(g, n);
            h = Math.min(h, o);
            i = Math.max(i, m);
            j = Math.max(j, n);
            k = h;
            //k = Math.max(k, o);
        }
        return new float[] {f, i, g, j, h, k};
    }
}