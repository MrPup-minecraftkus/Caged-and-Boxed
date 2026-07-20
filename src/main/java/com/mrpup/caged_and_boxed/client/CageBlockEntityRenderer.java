package com.mrpup.caged_and_boxed.client;

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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class CageBlockEntityRenderer implements BlockEntityRenderer<CageBlockEntity, CageBlockEntityRenderState> {

    private static final AtomicInteger ENTITY_ID_COUNTER = new AtomicInteger(-1);

    private final Map<BlockPos, Entity> entityCache = new HashMap<>();

    public CageBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public CageBlockEntityRenderState createRenderState() {
        return new CageBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(CageBlockEntity blockEntity, CageBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay breakProgress) {

        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);

        BlockPos pos = blockEntity.getBlockPos();

        state.hasMob = blockEntity.hasMob();

        if (!state.hasMob) {
            entityCache.remove(pos);
            return;
        }

        state.blockPos = pos;
        state.entityTypeId = blockEntity.getEntityTypeId();
        state.entityData = blockEntity.getEntityData();
        state.partialTick = partialTicks;

        BlockState blockState = blockEntity.getBlockState();
        state.facing = blockState.hasProperty(CageBlock.FACING) ? blockState.getValue(CageBlock.FACING) : Direction.NORTH;

        state.scale = computeScale(blockEntity, pos);
    }

    @Override
    public void submit(CageBlockEntityRenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {

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

        Entity entity = getOrCreateEntity(state.blockPos, state.entityTypeId, state.entityData, level);
        if (entity == null) {
            return;
        }

        float yRot = state.facing.toYRot();

        entity.tickCount = 0;

        entity.setYRot(yRot);
        entity.setXRot(0f);
        entity.yRotO = yRot;
        entity.xRotO = 0f;

        if (entity instanceof LivingEntity living) {
            living.yHeadRot = yRot;
            living.yBodyRot = yRot;
            living.yHeadRotO = yRot;
            living.yBodyRotO = yRot;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.scale(state.scale, state.scale, state.scale);

        float heightOffset = -(entity.getBbHeight() / 2.0f);
        poseStack.translate(0, heightOffset, 0);

        Minecraft mc = Minecraft.getInstance();
        EntityRenderer<? super Entity, ?> renderer = (EntityRenderer<? super Entity, ?>) mc.getEntityRenderDispatcher().getRenderer(entity);

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

    private float computeScale(CageBlockEntity blockEntity, BlockPos pos) {

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

        Entity tempEntity = getOrCreateEntity(pos, typeId, blockEntity.getEntityData(), level);
        if (tempEntity == null) {
            return 0.3f;
        }

        float mobMaxDim = Math.max(tempEntity.getBbWidth(), tempEntity.getBbHeight());

        if (mobMaxDim <= 0f) {
            return 0.3f;
        }

        return Math.min(available / mobMaxDim, 0.9f);
    }

    private Entity getOrCreateEntity(BlockPos pos, String typeId, CompoundTag entityData, Level level) {
        Entity cached = entityCache.get(pos);
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

            try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(LogUtils.getLogger())) {

                ValueInput input = TagValueInput.create(reporter, level.registryAccess(), entityData);

                entity.load(input);
            } catch (Exception e) {
                return null;
            }

            entity.setId(ENTITY_ID_COUNTER.getAndDecrement());

            entityCache.put(pos, entity);
            return entity;
        } catch (Exception e) {
            return null;
        }
    }

    public void clearCache() {
        entityCache.clear();
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