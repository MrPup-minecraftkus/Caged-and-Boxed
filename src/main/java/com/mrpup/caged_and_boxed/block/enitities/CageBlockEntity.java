package com.mrpup.caged_and_boxed.block.enitities;

import com.mrpup.caged_and_boxed.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

public class CageBlockEntity extends BlockEntity {

    private static final String TAG_ENTITY_DATA = "CapturedEntityData";
    private static final String TAG_ENTITY_TYPE = "CapturedEntityType";
    private static final String TAG_ENTITY_NAME = "CapturedEntityName";

    @Nullable private CompoundTag entityData = null;
    @Nullable private String entityTypeId = null;
    @Nullable private String entityName = null;

    public CageBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CAGE_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean hasMob() {
        return entityTypeId != null && !entityTypeId.isEmpty();
    }

    @Nullable
    public CompoundTag getEntityData() {
        return entityData;
    }

    @Nullable
    public String getEntityTypeId() {
        return entityTypeId;
    }

    @Nullable
    public String getEntityName() {
        return entityName;
    }

    public void setEntityData(CompoundTag data, String typeId, String name) {
        this.entityData = data;
        this.entityTypeId = typeId;
        this.entityName = name;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void clearMob() {
        this.entityData = null;
        this.entityTypeId = null;
        this.entityName = null;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (hasMob()) {
            tag.put(TAG_ENTITY_DATA, entityData);
            tag.putString(TAG_ENTITY_TYPE, entityTypeId);
            tag.putString(TAG_ENTITY_NAME, entityName != null ? entityName : "");
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains(TAG_ENTITY_TYPE)) {
            entityTypeId = tag.getString(TAG_ENTITY_TYPE);
            entityData   = tag.getCompound(TAG_ENTITY_DATA);
            entityName   = tag.getString(TAG_ENTITY_NAME);
        } else {
            entityData   = null;
            entityTypeId = null;
            entityName   = null;
        }
    }


    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        if (pkt.getTag() != null) loadAdditional(pkt.getTag(), registries);
    }


    public ItemStack createItemStack() {
        BlockState bs = getBlockState();
        ItemStack stack;

        if (bs.is(ModBlocks.SMALL_CAGE.get())) {
            stack = new ItemStack(ModBlocks.SMALL_CAGE.get());
        } else if (bs.is(ModBlocks.MEDIUM_CAGE.get())) {
            stack = new ItemStack(ModBlocks.MEDIUM_CAGE.get());
        } else {
            stack = new ItemStack(ModBlocks.UNIVERSAL_CAGE.get());
        }

        if (hasMob()) {
            CompoundTag tag = new CompoundTag();
            tag.put(TAG_ENTITY_DATA, entityData);
            tag.putString(TAG_ENTITY_TYPE, entityTypeId);
            tag.putString(TAG_ENTITY_NAME, entityName != null ? entityName : "");
            stack.set(DataComponents.CUSTOM_DATA,
                    CustomData.of(tag));
        }

        return stack;
    }
}
