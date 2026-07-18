package com.mrpup.caged_and_boxed.block.custom;

import com.mojang.serialization.MapCodec;
import com.mrpup.caged_and_boxed.block.enitities.CageBlockEntity;
import com.mrpup.caged_and_boxed.util.CageSize;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CageBlock extends BaseEntityBlock {

    private final CageSize cageSize;
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final VoxelShape SHAPE_SMALL = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    private static final VoxelShape SHAPE_MEDIUM = Shapes.box(0.0, 0.0, 0.0, 1.0, 2.0, 1.0);
    private static final VoxelShape SHAPE_UNIVERSAL = Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);

    public CageBlock(CageSize cageSize, Properties properties) {
        super(properties);
        this.cageSize = cageSize;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public MapCodec<? extends BaseEntityBlock> codec() {
        return MapCodec.unit(this);
    }

    public CageSize getCageSize() {
        return cageSize;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (cageSize) {
            case SMALL  -> SHAPE_SMALL;
            case MEDIUM -> SHAPE_MEDIUM;
            case UNIVERSAL  -> SHAPE_UNIVERSAL;
        };
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CageBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return null;
    }


    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (level.isClientSide) {
            if (level.getBlockEntity(pos) instanceof CageBlockEntity cage && cage.hasMob()) {
                cage.clearMob();
            }
            return InteractionResult.SUCCESS;
        }

        if (!(level.getBlockEntity(pos) instanceof CageBlockEntity cage)) return InteractionResult.PASS;

        releaseMob(cage, (ServerLevel) level, pos, player);

        return InteractionResult.CONSUME;
    }

    public void releaseMob(CageBlockEntity cage, ServerLevel level, BlockPos pos, Player player) {
        CompoundTag entityData = cage.getEntityData();
        if (entityData == null) return;

        Optional<EntityType<?>> typeOpt = EntityType.by(entityData);
        if (typeOpt.isEmpty()) return;

        Entity entity = typeOpt.get().create(level);
        if (entity == null) return;

        entity.load(entityData);
        entity.setPos(pos.getX() + 1, pos.getY() + 1.0, pos.getZ() + 1);
        level.addFreshEntity(entity);

        cage.clearMob();

        level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1f, 1f);
        player.displayClientMessage(
               Component.literal("Mob released!"), true);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        ItemStack tool = player.getMainHandItem();

        if (!level.isClientSide && tool.is(ItemTags.PICKAXES) && !player.isCreative()) {
            if (level.getBlockEntity(pos) instanceof CageBlockEntity cage) {
                ItemStack drop = cage.createItemStack();
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), drop);
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            level.removeBlockEntity(pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof CageBlockEntity cage) {
            CustomData data = stack.get(
                    DataComponents.CUSTOM_DATA);
            if (data != null && data.contains("CapturedEntityData")) {
                CompoundTag tag = data.copyTag();
                cage.setEntityData(
                        tag.getCompound("CapturedEntityData"),
                        tag.getString("CapturedEntityType"),
                        tag.getString("CapturedEntityName")
                );
            }
        }
    }
}