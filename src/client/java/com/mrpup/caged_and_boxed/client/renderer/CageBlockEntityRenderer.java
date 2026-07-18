package com.mrpup.caged_and_boxed.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrpup.caged_and_boxed.block.custom.CageBlock;
import com.mrpup.caged_and_boxed.block.enitities.CageBlockEntity;
import com.mrpup.caged_and_boxed.util.CageSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CageBlockEntityRenderer implements BlockEntityRenderer<CageBlockEntity> {

    private final Map<BlockPos, Entity> entityCache = new HashMap<>();

    public CageBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(CageBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        BlockPos pos = blockEntity.getBlockPos();

        if (!blockEntity.hasMob()) {
            entityCache.remove(pos);
            return;
        }

        String typeId = blockEntity.getEntityTypeId();
        CompoundTag entityData = blockEntity.getEntityData();
        if (typeId == null || entityData == null) return;

        Level level = blockEntity.getLevel();
        if (level == null) return;

        Entity entity = getOrCreateEntity(pos, entityData, level);
        if (entity == null) return;

        BlockState state = blockEntity.getBlockState();
        Direction facing = state.hasProperty(CageBlock.FACING) ? state.getValue(CageBlock.FACING) : Direction.NORTH;

        float yRot = facing.toYRot();
        float scale = getScale(blockEntity, entity);

        entity.tickCount = 0;

        entity.setYRot(yRot);
        entity.setXRot(0f);
        entity.yRotO = yRot;
        entity.xRotO = 0f;

        if (entity instanceof LivingEntity living) {
            living.yBodyRot = yRot;
            living.yBodyRotO = yRot;
            living.yHeadRot = yRot;
            living.yHeadRotO = yRot;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        poseStack.scale(scale, scale, scale);

        float heightOffset = -(entity.getBbHeight() / 2.0f);
        poseStack.translate(0, heightOffset, 0);

        Minecraft mc = Minecraft.getInstance();
        EntityRenderer<? super Entity> renderer = (EntityRenderer<? super Entity>) mc.getEntityRenderDispatcher().getRenderer(entity);

        renderer.render(entity, 0f, partialTick, poseStack, bufferSource, combinedLight);

        poseStack.popPose();
    }

    private float getScale(CageBlockEntity blockEntity, Entity e) {
        if (!(blockEntity.getBlockState().getBlock() instanceof CageBlock cb)) return 0.3f;

        CageSize size = cb.getCageSize();

        float available = switch (size) {
            case SMALL  -> 0.6f;
            case MEDIUM -> 0.75f;
            case UNIVERSAL  -> 0.6f;
        };

        float mobMaxDim = Math.max(e.getBbWidth(), e.getBbHeight());
        if (mobMaxDim <= 0) return 0.3f;

        return Math.min(available / mobMaxDim, 0.9f);
    }

    private Entity getOrCreateEntity(BlockPos pos, CompoundTag entityData, Level level) {
        Entity cached = entityCache.get(pos);
        if (cached != null) return cached;

        Optional<EntityType<?>> typeOpt = EntityType.by(entityData);
        if (typeOpt.isEmpty()) return null;

        Entity entity = typeOpt.get().create(level);
        if (entity == null) return null;

        try {
            entity.load(entityData);
        } catch (Exception ignored) {}

        entityCache.put(pos, entity);
        return entity;
    }

    public void clearCache() {
        entityCache.clear();
    }
}
