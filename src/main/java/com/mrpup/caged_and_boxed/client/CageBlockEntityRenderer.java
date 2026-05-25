package com.mrpup.caged_and_boxed.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mrpup.caged_and_boxed.block.custom.CageBlock;
import com.mrpup.caged_and_boxed.block.enitities.CageBlockEntity;
import com.mrpup.caged_and_boxed.util.CageSize;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CageBlockEntityRenderer implements BlockEntityRenderer<CageBlockEntity> {

    private static final Map<String, Entity> ENTITY_CACHE = new HashMap<>();

    public CageBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(CageBlockEntity blockEntity, float partialTick, PoseStack poseStack,
                       MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        if (!blockEntity.hasMob()) return;

        String typeId = blockEntity.getEntityTypeId();
        CompoundTag entityData = blockEntity.getEntityData();
        if (typeId == null || entityData == null) return;

        Level level = blockEntity.getLevel();
        if (level == null) return;

        Entity entity = getOrCreateEntity(typeId, entityData, level);
        if (entity == null) return;

        float scale = getScale(blockEntity);

        entity.setYRot(0f);
        entity.setXRot(0f);
        entity.setYHeadRot(0f);
        entity.setYBodyRot(0f);

        if (entity instanceof LivingEntity living) {
            living.yBodyRotO = 0f;
            living.yHeadRotO = 0f;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        poseStack.scale(scale, scale, scale);

        float heightOffset = -(entity.getBbHeight() / 2.0f);
        poseStack.translate(0, heightOffset, 0);

        Minecraft mc = Minecraft.getInstance();
        EntityRenderer<? super Entity> renderer =
                (EntityRenderer<? super Entity>) mc.getEntityRenderDispatcher().getRenderer(entity);

        renderer.render(entity, 0f, partialTick, poseStack, bufferSource, combinedLight);

        poseStack.popPose();
    }

    private float getScale(CageBlockEntity blockEntity) {
        if (!(blockEntity.getBlockState().getBlock() instanceof CageBlock cb)) return 0.3f;

        CageSize size = cb.getCageSize();

        float available = switch (size) {
            case SMALL  -> 0.6f;
            case MEDIUM -> 0.75f;
            case UNIVERSAL  -> 0.6f;
        };

        String typeId = blockEntity.getEntityTypeId();
        if (typeId == null) return 0.3f;

        Entity e = ENTITY_CACHE.get(typeId);
        if (e == null) return 0.3f;

        float mobMaxDim = Math.max(e.getBbWidth(), e.getBbHeight());
        if (mobMaxDim <= 0) return 0.3f;

        return Math.min(available / mobMaxDim, 0.9f);
    }

    private Entity getOrCreateEntity(String typeId, CompoundTag entityData, Level level) {
        if (ENTITY_CACHE.containsKey(typeId)) {
            return ENTITY_CACHE.get(typeId);
        }

        Optional<EntityType<?>> typeOpt = EntityType.by(entityData);
        if (typeOpt.isEmpty()) return null;

        Entity entity = typeOpt.get().create(level);
        if (entity == null) return null;

        try {
            entity.load(entityData);
        } catch (Exception ignored) {}

        ENTITY_CACHE.put(typeId, entity);
        return entity;
    }

    public static void clearCache() {
        ENTITY_CACHE.clear();
    }
}
