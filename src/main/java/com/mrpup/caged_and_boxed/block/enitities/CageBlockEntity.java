package com.mrpup.caged_and_boxed.block.enitities;

import com.mrpup.caged_and_boxed.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void clearMob() {
        this.entityData = null;
        this.entityTypeId = null;
        this.entityName = null;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (hasMob()) {
            output.store(TAG_ENTITY_DATA, CompoundTag.CODEC, this.entityData);
            output.putString(TAG_ENTITY_TYPE, this.entityTypeId);
            output.putString(TAG_ENTITY_NAME, this.entityName != null ? this.entityName : "");
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        input.read(TAG_ENTITY_DATA, CompoundTag.CODEC).ifPresent(tag -> {
            this.entityData = tag;
        });

        this.entityTypeId = input.getString(TAG_ENTITY_TYPE).orElse(null);
        this.entityName = input.getString(TAG_ENTITY_NAME).orElse(null);

        if (this.entityName != null && this.entityName.isEmpty()) {
            this.entityName = null;
        }
    }


    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();

        try (ProblemReporter.ScopedCollector reporter =
                     new ProblemReporter.ScopedCollector(this.problemPath(), null)) {

            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);
            this.saveAdditional(output);

            if (hasMob()) {
                tag.put(TAG_ENTITY_DATA, this.entityData);
                tag.putString(TAG_ENTITY_TYPE, this.entityTypeId);
                tag.putString(TAG_ENTITY_NAME, this.entityName != null ? this.entityName : "");
            }
        }

        return tag;
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
