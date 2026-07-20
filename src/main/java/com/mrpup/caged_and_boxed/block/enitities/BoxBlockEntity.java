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
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public void clearStoredBlock() {
        this.storedTag = null;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (storedTag != null) {
            output.store(TAG_STORED, CompoundTag.CODEC, this.storedTag);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        this.storedTag = input.read(TAG_STORED, CompoundTag.CODEC).orElse(null);
    }



    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();

        try (ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(this.problemPath(), null)) {

            TagValueOutput output = TagValueOutput.createWithContext(reporter, registries);

            this.saveAdditional(output);
        }

        return tag;
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
