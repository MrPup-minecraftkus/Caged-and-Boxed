package com.mrpup.caged_and_boxed.item.custom;

import com.mrpup.caged_and_boxed.block.custom.CageBlock;
import com.mrpup.caged_and_boxed.util.CageSize;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Position;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Optional;

public class CageBlockItem extends BlockItem {

    private static final String TAG_ENTITY_DATA = "CapturedEntityData";
    private static final String TAG_ENTITY_TYPE = "CapturedEntityType";
    private static final String TAG_ENTITY_NAME = "CapturedEntityName";

    public CageBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    private CageSize getCageSize() {
        if (getBlock() instanceof CageBlock cb) return cb.getCageSize();
        return CageSize.UNIVERSAL;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        if (isFilled(stack)) return InteractionResult.PASS;
        if (!(target instanceof Mob mob)) return InteractionResult.PASS;

        Level level = player.level();

        if (level.isClientSide) return InteractionResult.SUCCESS;

        CageSize mobSize  = CageSize.fromEntity(mob);
        CageSize cageSize = getCageSize();

        if (!cageSize.canFit(mobSize)) {
            player.displayClientMessage(
                    Component.literal("This mob (" + mobSize.displayName() + ") does not fit in a " +
                            cageSize.displayName() + " cage!").withStyle(ChatFormatting.RED), true);
            return InteractionResult.FAIL;
        }

        CompoundTag entityData = new CompoundTag();
        mob.save(entityData);

        entityData.remove("UUID");

        CompoundTag tag = new CompoundTag();
        tag.put(TAG_ENTITY_DATA, entityData);
        tag.putString(TAG_ENTITY_TYPE, BuiltInRegistries.ENTITY_TYPE
                .getKey(mob.getType()).toString());
        tag.putString(TAG_ENTITY_NAME, mob.getDisplayName().getString());

        ItemStack filledStack = stack.copy();
        if (player.getAbilities().instabuild) {

            filledStack.setCount(1);
            filledStack.setTag(tag);

            if (!player.getInventory().add(filledStack)) {
                player.drop(filledStack, false);
            }
        } else {
            stack.setTag(tag);
        }

        mob.discard();

        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.containerMenu.broadcastChanges();
        }

        level.playSound(null, player.blockPosition(),
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 1f, 0.8f);
        player.displayClientMessage(
                Component.literal("Captured: ").append(mob.getDisplayName())
                        .withStyle(ChatFormatting.GREEN), true);

        return InteractionResult.SUCCESS;
    }

    public static boolean isFilled(ItemStack stack) {
        CompoundTag data = stack.getTag();
        return data != null && data.contains(TAG_ENTITY_TYPE);
    }

    public static String getCapturedName(ItemStack stack) {
        CompoundTag data = stack.getTag();
        if (data != null && data.contains(TAG_ENTITY_NAME)) {
            return data.copy().getString(TAG_ENTITY_NAME);
        }
        return null;
    }

    public static CompoundTag getCapturedEntityData(ItemStack stack) {
        CompoundTag data = stack.getTag();
        if (data == null) return null;
        CompoundTag tag = data.copy();
        return tag.contains(TAG_ENTITY_DATA) ? tag.getCompound(TAG_ENTITY_DATA) : null;
    }

    public static String getCapturedEntityType(ItemStack stack) {
        CompoundTag data = stack.getTag();
        if (data == null) return null;
        CompoundTag tag = data.copy();
        return tag.contains(TAG_ENTITY_TYPE) ? tag.getString(TAG_ENTITY_TYPE) : null;
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);

        CageSize size = getCageSize();
        lines.add(Component.literal("Size: " + size.displayName())
                .withStyle(ChatFormatting.GRAY));

        if (isFilled(stack)) {
            String name = getCapturedName(stack);
            lines.add(Component.literal("Contains: " + (name != null ? name : "???"))
                    .withStyle(ChatFormatting.AQUA));
            lines.add(Component.literal("Shift + Right-click to release")
                    .withStyle(ChatFormatting.YELLOW));
        } else {
            lines.add(Component.literal("Empty").withStyle(ChatFormatting.DARK_GRAY));
            lines.add(Component.literal("Right-click a mob to capture")
                    .withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isFilled(stack);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player != null && player.isShiftKeyDown() && isFilled(stack)) {
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
                releaseMob(stack, serverLevel, context.getClickLocation(), player);

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.containerMenu.broadcastChanges();
                }
            }
            return InteractionResult.SUCCESS;
        }

        return super.useOn(context);
    }

    public void releaseMob(ItemStack stack, ServerLevel level, Position pos, Player player) {
        CompoundTag entityData = getCapturedEntityData(stack);
        if (entityData == null) return;

        String typeStr = getCapturedEntityType(stack);
        if (typeStr == null) return;

        Optional<EntityType<?>> typeOpt = EntityType.byString(typeStr);
        if (typeOpt.isEmpty()) return;

        Entity entity = typeOpt.get().create(level);
        if (entity == null) return;

        entity.load(entityData);
        entity.setPos(pos.x(), pos.y() + 0.1, pos.z());
        level.addFreshEntity(entity);

        stack.removeTagKey(TAG_ENTITY_DATA);
        stack.removeTagKey(TAG_ENTITY_TYPE);
        stack.removeTagKey(TAG_ENTITY_NAME);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1f, 1f);

        player.displayClientMessage(
                Component.literal("Mob Released!").withStyle(ChatFormatting.GOLD), true);
    }
}

