package com.mrpup.caged_and_boxed.block.custom;

import com.mojang.serialization.MapCodec;
import com.mrpup.caged_and_boxed.block.enitities.BoxBlockEntity;
import com.mrpup.caged_and_boxed.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import org.jetbrains.annotations.Nullable;

public class BoxBlock extends BaseEntityBlock {

    public BoxBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(this);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BoxBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }


    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                            Player player, BlockHitResult hit) {

        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof BoxBlockEntity box)) return InteractionResult.PASS;
        if (!box.hasStoredBlock() || !player.isShiftKeyDown()) return InteractionResult.PASS;

        placeStoredBlock(box, (ServerLevel) level, pos, player);
        return InteractionResult.SUCCESS;
    }


    private void placeStoredBlock(BoxBlockEntity box, ServerLevel level,
                                  BlockPos boxPos, Player player) {
        HolderLookup.Provider registries = level.registryAccess();
        CompoundTag storedTag = box.getStoredTag();
        if (storedTag == null) return;

        BlockState restoredState = null;
        if (storedTag.contains("BlockState")) {
            restoredState = BlockState.CODEC.parse(
                    level.registryAccess().createSerializationContext(NbtOps.INSTANCE),
                    storedTag.get("BlockState")
            ).result().orElse(null);
        }

        if (restoredState == null && storedTag.contains("BlockId")) {
            ResourceLocation id =
                    ResourceLocation.tryParse(storedTag.getString("BlockId"));
            if (id != null) {
                Block block = BuiltInRegistries.BLOCK.get(id);
                if (block != null) restoredState = block.defaultBlockState();
            }
        }

        if (restoredState == null) return;

        box.setRestoring(true);

        level.setBlock(boxPos, restoredState, Block.UPDATE_ALL);

        if (storedTag.contains("BlockEntityData")) {
            BlockEntity newBE = level.getBlockEntity(boxPos);
            if (newBE != null) {
                CompoundTag beTag = storedTag.getCompound("BlockEntityData");
                beTag.putInt("x", boxPos.getX());
                beTag.putInt("y", boxPos.getY());
                beTag.putInt("z", boxPos.getZ());
                newBE.loadWithComponents(beTag, registries);
                newBE.setChanged();
            }
        }

        level.playSound(null, boxPos, SoundEvents.CHEST_OPEN, SoundSource.BLOCKS, 1f, 1.2f);
        player.displayClientMessage(
                Component.literal("Block restored!"), true);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos,
                         BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof BoxBlockEntity box) {
                if (box.isRestoring()) {
                    ItemStack drop = ModItems.BOX_BLOCK.toStack(1);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop);
                    super.onRemove(state, level, pos, newState, isMoving);
                    return;
                }

                ItemStack drop = box.createItemStack();
                Containers.dropItemStack(
                        level, pos.getX(), pos.getY(), pos.getZ(), drop);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }



    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof BoxBlockEntity box) {
            CustomData data =
                    stack.get(DataComponents.CUSTOM_DATA);
            if (data != null && data.contains("StoredBlock")) {
                box.setStoredBlock(data.copyTag().getCompound("StoredBlock"));
            }
        }
    }
}