package com.snoweverywhere;

import com.snoweverywhere.blocks.entities.SnowEverywhereBlockEntity;

import it.unimi.dsi.fastutil.ints.Int2IntFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;

public class SnowEverywhereBlockEntityRenderer
        implements BlockEntityRenderer<SnowEverywhereBlockEntity> {
    public NbtCompound nbt = new NbtCompound();
    private final BakedModelManager bakedModelManager = MinecraftClient.getInstance().getBakedModelManager();
    private final Random random = Random.create();

    private TexturedModelData modelData = null;
    private VertexConsumer vertexConsumer = null;
    private ArrayList<String> names = new ArrayList<String>();
    private List<float[]> surfaces = new ArrayList<float[]>();
    private BlockEntity entity = null;
    private BlockState oldState = null;
    private int oldLayers = 0;

    //public static final SpriteIdentifier SNOW_SPRITE_IDENTIFIER = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, new Identifier("minecraft", "block/snow"));

    private static final int X_1 = 0;
    private static final int Z_1 = 1;
    private static final int X_2 = 2;
    private static final int Z_2 = 3;
    private static final int Y = 4;

    public SnowEverywhereBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(SnowEverywhereBlockEntity entity, float tickDelta, MatrixStack matrices,
            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        this.entity = entity;
        BlockPos below = entity.getPos().down();
        World world = entity.getWorld();
        BlockState stateBelow = world.getBlockState(below);
        entity.writeNbt(nbt, null);
        int layers = nbt.getInt("layers");
        //vertexConsumer = SNOW_SPRITE_IDENTIFIER.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout);
        modelData = getTexturedModelData(stateBelow, Direction.UP, world, layers);
        ModelPart modelPart = modelData.createModel();
        matrices.push();
        int belowLight = world != null ? WorldRenderer.getLightmapCoordinates((BlockRenderView) world, below)
                : LightmapTextureManager.MAX_LIGHT_COORDINATE;
        for (String name : names) {
            modelPart.getChild(name).render(matrices, vertexConsumer, belowLight, overlay);
        }
        matrices.pop();
        oldState = stateBelow;
        oldLayers = layers;
    }

    public TexturedModelData getTexturedModelData(BlockState state, Direction face, World world, int layers) {
        ModelData modelData = new ModelData();
        if(state.equals(oldState) && layers == oldLayers) {
            int i = 0;
            for (float[] surface : surfaces) {
                float xsize = (surface[X_2] - surface[X_1]);
                float zsize = (surface[Z_2] - surface[Z_1]);

                float xoffset = surface[X_1];
                float yoffset = surface[Y];
                float zoffset = surface[Z_1];

                String name = "surface_" + i;
                names.add(name);
                i++;

                modelData.getRoot().addChild(name,
                        ModelPartBuilder.create().uv(0, 19).cuboid(xoffset, yoffset - 16.0f, zoffset, xsize, 2.0f * layers, zsize),
                        ModelTransform.NONE);
            }
            return TexturedModelData.of(modelData, 64, 64);
        }
        names = new ArrayList<String>();
        surfaces = new ArrayList<float[]>();
        List<BakedQuad> quads = getBakedQuads(state, null, world);

        if (face != null)
            quads = Stream.concat(quads.stream(), getBakedQuads(state, face, world).stream()).toList();

        for (BakedQuad quad : quads) {
            float[] surface = getQuadSurface(quad.getVertexData());
            if (surface[X_1] != surface[X_2] && surface[Z_1] != surface[Z_2])
                surfaces.add(getQuadSurface(quad.getVertexData()));
        }

        if (surfaces.size() == 0) {
            return TexturedModelData.of(modelData, 64, 64);
        }

        surfaces = getNonOverlappingSurfaces(surfaces);

        nbt.putByteArray("surfaces", serialize(surfaces));
        nbt.putBoolean("notify_block", true);
        //entity.readNbt(nbt, null);
        entity.markDirty();

        int i = 0;
        for (float[] surface : surfaces) {
            float xsize = (surface[X_2] - surface[X_1]);
            float zsize = (surface[Z_2] - surface[Z_1]);

            float xoffset = surface[X_1];
            float yoffset = surface[Y];
            float zoffset = surface[Z_1];

            String name = "surface_" + i;
            names.add(name);
            i++;

            modelData.getRoot().addChild(name,
                    ModelPartBuilder.create().uv(0, 19).cuboid(xoffset, yoffset - 16.0f, zoffset, xsize, 2.0f * layers, zsize),
                    ModelTransform.NONE);
        }
        return TexturedModelData.of(modelData, 64, 64);
    }

    public List<float[]> getNonOverlappingSurfaces(List<float[]> surfaces) {
        float epsilon = 0.0001f;

        Collections.sort(surfaces, new Comparator<float[]>() {
            @Override
            public int compare(float[] surface1, float[] surface2) {
                if (Math.abs(surface1[Y] - surface2[Y]) <= epsilon) {
                    return 0;
                } else if (surface1[Y] < surface2[Y]) {
                    return -1;
                } else if (surface1[Y] > surface2[Y]) {
                    return 1;
                } else
                    return 0;
            }
        });

        List<float[]> ret = new ArrayList<float[]>();

        for (int i = 0; i < surfaces.size() - 1; i++) {
            List<float[]> toRet = new ArrayList<float[]>();
            toRet.add(surfaces.get(i));
            for (int j = i + 1; j < surfaces.size(); j++) {
                List<float[]> temp = new ArrayList<float[]>();
                for (int k = 0; k < toRet.size(); k++) {
                    temp.addAll(subtractPlane(toRet.get(k), surfaces.get(j)));
                }
                toRet = temp;
            }
            ret.addAll(toRet);
        }

        ret.add(surfaces.get(surfaces.size() - 1));

        return ret;
    }

    public static List<float[]> subtractPlane(float[] a, float[] b) {
        if (Math.max(a[X_1], b[X_1]) >= Math.min(a[X_2], b[X_2])
                || Math.max(a[Z_1], b[Z_1]) >= Math.min(a[Z_2], b[Z_2])) {
            // No overlap
            List<float[]> result = new ArrayList<>();
            result.add(a);
            return result;
        }

        if (a[X_1] == b[X_1] && a[Z_1] == b[Z_1] && a[X_2] == b[X_2] && a[Z_2] == b[Z_2]) {
            return new ArrayList<float[]>();
        }

        // Calculate the intersection
        float x1_i = Math.max(a[X_1], b[X_1]);
        float z1_i = Math.max(a[Z_1], b[Z_1]);
        float x2_i = Math.min(a[X_2], b[X_2]);
        float z2_i = Math.min(a[Z_2], b[Z_2]);

        // List to hold resulting fragments
        List<float[]> surfaces = new ArrayList<>();

        // Fragment 1: Left section
        if (a[X_1] < x1_i) {
            surfaces.add(new float[] { a[X_1], a[Z_1], x1_i, a[Z_2], a[Y] });
        }

        // Fragment 2: Right section
        if (x2_i < a[X_2]) {
            surfaces.add(new float[] { x2_i, a[Z_1], a[X_2], a[Z_2], a[Y] });
        }

        // Fragment 3: Top section
        if (z2_i < a[Z_2]) {
            surfaces.add(new float[] { x1_i, z2_i, x2_i, a[Z_2], a[Y] });
        }

        // Fragment 4: Bottom section
        if (a[Z_1] < z1_i) {
            surfaces.add(new float[] { x1_i, a[Z_1], x2_i, z1_i, a[Y] });
        }

        return surfaces;
    }

    public static boolean planeHasPoint(float[] plane, float[] point) {
        return plane[0] >= point[0] && plane[1] == point[1] && plane[2] >= point[2] && plane[3] <= point[0]
                && plane[4] <= point[2];
    }

    private List<BakedQuad> getBakedQuads(BlockState state, Direction face, World world) {
        ModelIdentifier belowModelId = BlockModels.getModelId(state);
        BakedModel belowModel = bakedModelManager.getModel(belowModelId);
        return belowModel.getQuads(state, face, random);
    }

    private float[] getQuadSurface(int[] vertexData) {
        float x1 = 32.0f;
        float y1 = 32.0f;
        float z1 = 32.0f;
        float x2 = -32.0f;
        float y2 = -32.0f;
        float z2 = -32.0f;
        for (int l = 0; l < 4; ++l) {
            float m = Float.intBitsToFloat(vertexData[l * 8]);
            float n = Float.intBitsToFloat(vertexData[l * 8 + 1]);
            float o = Float.intBitsToFloat(vertexData[l * 8 + 2]);
            x1 = Math.min(x1, m);
            y1 = Math.min(y1, n);
            z1 = Math.min(z1, o);
            x2 = Math.max(x2, m);
            y2 = Math.max(y2, n);
            z2 = Math.max(z2, o);
        }
        return new float[] { x1  * 16.0f, z1 * 16.0f, x2 * 16.0f, z2 * 16.0f, y1 * 16.0f };
    }

    static byte[] serialize(final Object obj) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}