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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BoxBlockEntity extends BlockEntity {

    private static final String TAG_STORED = "StoredBlock";

    @Nullable
    private CompoundTag storedTag = null;

    private boolean isRestoring = false;

    public void setRestoring(boolean restoring) {
        this.isRestoring = restoring;
    }

    public boolean isRestoring() {
        return this.isRestoring;
    }

    public BoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BOX_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean hasStoredBlock() {
        return storedTag != null;
    }

    @Nullable
    public CompoundTag getStoredTag() {
        return storedTag;
    }

    public void setStoredBlock(CompoundTag tag) {
        this.storedTag = tag;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void clearStoredBlock() {
        this.storedTag = null;
        setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (storedTag != null) {
            tag.put(TAG_STORED, storedTag);
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        storedTag = tag.contains(TAG_STORED) ? tag.getCompound(TAG_STORED) : null;
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
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
                             HolderLookup.Provider registries) {
        if (pkt.getTag() != null) loadAdditional(pkt.getTag(), registries);
    }



    public ItemStack createItemStack() {
        ItemStack stack = new ItemStack(ModBlocks.BOX_BLOCK.get());
        if (storedTag != null) {
            CompoundTag tag = new CompoundTag();
            tag.put(TAG_STORED, storedTag);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return stack;
    }
}
