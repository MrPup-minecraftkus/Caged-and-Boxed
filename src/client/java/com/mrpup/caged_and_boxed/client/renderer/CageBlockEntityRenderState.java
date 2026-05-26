package com.mrpup.caged_and_boxed.client.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.nbt.CompoundTag;

public class CageBlockEntityRenderState extends BlockEntityRenderState {

    public boolean hasMob;
    public String entityTypeId;
    public CompoundTag entityData;
    public float partialTick;
    public float scale;
}
