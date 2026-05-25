package com.mrpup.caged_and_boxed.item.custom;

import com.mrpup.caged_and_boxed.block.ModBlocks;
import com.mrpup.caged_and_boxed.block.enitities.BoxBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class BoxBlockItem extends BlockItem {

    private static final String TAG_STORED = "StoredBlock";

    public BoxBlockItem(Properties properties) {
        super(ModBlocks.BOX_BLOCK.get(), properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        ItemStack stack = context.getItemInHand();
        BlockPos targetPos = context.getClickedPos();
        BlockState targetState = level.getBlockState(targetPos);

        if (isFilled(stack)) {
            return super.useOn(context);
        }

        if (level.getBlockEntity(targetPos) instanceof BoxBlockEntity box && box.hasStoredBlock()) {
            return InteractionResult.PASS;
        }

        if (targetState.isAir()
                || targetState.is(ModBlocks.BOX_BLOCK.get())
                || !targetState.getFluidState().isEmpty()
                || targetState.getDestroySpeed(level, targetPos) < 0) {
            return InteractionResult.FAIL;
        }

        if (level.isClientSide) return InteractionResult.SUCCESS;

        HolderLookup.Provider registries = level.registryAccess();

        CompoundTag storedTag = new CompoundTag();
        BlockState.CODEC.encodeStart(
                registries.createSerializationContext(NbtOps.INSTANCE), targetState
        ).result().ifPresent(tag -> storedTag.put("BlockState", tag));
        storedTag.putString("BlockId", BuiltInRegistries.BLOCK.getKey(targetState.getBlock()).toString());

        BlockEntity targetBE = level.getBlockEntity(targetPos);
        CompoundTag targetBEData = null;
        if (targetBE != null) {
            targetBEData = targetBE.saveWithFullMetadata(registries);

            if (targetBE instanceof Container container) {
                container.clearContent();
            }
        }

        if (targetBEData != null) {
            storedTag.put("BlockEntityData", targetBEData);
        }

        boolean blockSet = level.setBlock(targetPos, ModBlocks.BOX_BLOCK.get().defaultBlockState(), 2 | 16);

        if (blockSet && level.getBlockEntity(targetPos) instanceof BoxBlockEntity box) {
            box.setStoredBlock(storedTag);
            box.setChanged();
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        level.playSound(null, targetPos, SoundEvents.CHEST_CLOSE, SoundSource.BLOCKS, 1f, 1.2f);
        player.displayClientMessage(Component.literal("Stored: " + targetState.getBlock().getDescriptionId()), true);

        return InteractionResult.SUCCESS;
    }


    private static boolean isFilled(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.contains(TAG_STORED);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, context, lines, flag);

        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.contains(TAG_STORED)) {
            CompoundTag stored = data.copyTag().getCompound(TAG_STORED);
            if (stored.contains("BlockId")) {
                String id = stored.getString("BlockId");
                String name = id.contains(":") ? id.split(":")[1] : id;
                lines.add(Component.literal("Contains: " + name)
                        .withStyle(ChatFormatting.AQUA));
            }
            lines.add(Component.literal("Shift+Right-click in placed box to release")
                    .withStyle(ChatFormatting.YELLOW));
        } else {
            lines.add(Component.literal("Right-click a block to store it")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.contains(TAG_STORED);
    }
}

