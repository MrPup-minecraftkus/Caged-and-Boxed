package com.mrpup.caged_and_boxed.client;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class CageBlockEntityRenderState extends BlockEntityRenderState {

    public boolean hasMob;
    public String entityTypeId;
    public CompoundTag entityData;
    public float partialTick;
    public float scale;
    public BlockPos blockPos;
    public Direction facing;
}
