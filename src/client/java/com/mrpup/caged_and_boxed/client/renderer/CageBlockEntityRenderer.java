package com.mrpup.caged_and_boxed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.mrpup.caged_and_boxed.block.custom.CageBlock;
import com.mrpup.caged_and_boxed.block.enitities.CageBlockEntity;
import com.mrpup.caged_and_boxed.util.CageSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CageBlockEntityRenderer implements BlockEntityRenderer<CageBlockEntity, CageBlockEntityRenderState> {

    private static final Map<String, Entity> ENTITY_CACHE = new HashMap<>();

    public CageBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public CageBlockEntityRenderState createRenderState() {
        return new CageBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(CageBlockEntity blockEntity,
                                   CageBlockEntityRenderState state,
                                   float partialTicks,
                                   Vec3 cameraPosition,
                                   ModelFeatureRenderer.CrumblingOverlay breakProgress) {


        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);

        state.hasMob = blockEntity.hasMob();
        if (!state.hasMob) {

            return;
        }

        state.entityTypeId = blockEntity.getEntityTypeId();
        state.entityData = blockEntity.getEntityData();
        state.partialTick = partialTicks;
        state.scale = computeScale(blockEntity);
    }

    @Override
    public void submit(CageBlockEntityRenderState state,
                       PoseStack poseStack,
                       SubmitNodeCollector collector,
                       CameraRenderState cameraState) {


        if (!state.hasMob) {
            return;
        }
        if (state.entityTypeId == null || state.entityData == null) {
            return;
        }

        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }

        Entity entity = getOrCreateEntity(state.entityTypeId, state.entityData, level);
        if (entity == null) {
            return;
        }

        entity.setYRot(0f);
        entity.setXRot(0f);

        if (entity instanceof LivingEntity living) {
            living.yHeadRot = 0f;
            living.yBodyRot = 0f;
            living.yHeadRotO = 0f;
            living.yBodyRotO = 0f;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.1, 0.5);
        poseStack.scale(state.scale, state.scale, state.scale);

        Minecraft mc = Minecraft.getInstance();
        EntityRenderer<? super Entity, ?> renderer =
                (EntityRenderer<? super Entity, ?>) mc.getEntityRenderDispatcher().getRenderer(entity);


        if (renderer != null) {
            EntityRenderState entityRenderState = renderer.createRenderState(entity, state.partialTick);

            if (entityRenderState != null) {
                @SuppressWarnings({"rawtypes", "unchecked"})
                EntityRenderer rawRenderer = (EntityRenderer) renderer;
                rawRenderer.submit(entityRenderState, poseStack, collector, cameraState);
            }
        }

        poseStack.popPose();
    }

    private float computeScale(CageBlockEntity blockEntity) {

        float available = 0.55f;

        if (blockEntity.getBlockState().getBlock() instanceof CageBlock cageBlock) {
            CageSize size = cageBlock.getCageSize();
            available = switch (size) {
                case SMALL     -> 0.55f;
                case MEDIUM    -> 0.70f;
                case UNIVERSAL -> 0.60f;
            };
        }

        String typeId = blockEntity.getEntityTypeId();
        if (typeId == null) {
            return 0.3f;
        }

        Level level = blockEntity.getLevel();
        if (level == null) {
            return 0.3f;
        }

        Entity tempEntity = getOrCreateEntity(typeId, blockEntity.getEntityData(), level);
        if (tempEntity == null) {
            return 0.3f;
        }

        float mobMaxDim = Math.max(tempEntity.getBbWidth(), tempEntity.getBbHeight());

        if (mobMaxDim <= 0f) {
            return 0.3f;
        }

        float finalScale = Math.min(available / mobMaxDim, 0.9f);
        return finalScale;
    }

    private static Entity getOrCreateEntity(String typeId, CompoundTag entityData, Level level) {
        Entity cached = ENTITY_CACHE.get(typeId);
        if (cached != null) {
            return cached;
        }


        try {
            Identifier resourceLocation = Identifier.parse(typeId);
            Optional<EntityType<?>> typeOpt = BuiltInRegistries.ENTITY_TYPE.getOptional(resourceLocation);

            if (typeOpt.isEmpty()) {
                return null;
            }

            Entity entity = typeOpt.get().create(level, EntitySpawnReason.LOAD);

            if (entity == null) {
                return null;
            }

            try (ProblemReporter.ScopedCollector reporter =
                         new ProblemReporter.ScopedCollector(LogUtils.getLogger())) {

                ValueInput input = TagValueInput.create(reporter, level.registryAccess(), entityData);

                entity.load(input);
            } catch (Exception e) {
                return null;
            }

            ENTITY_CACHE.put(typeId, entity);
            return entity;
        } catch (Exception e) {
            return null;
        }
    }

    public static void clearCache() {
        ENTITY_CACHE.clear();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}
