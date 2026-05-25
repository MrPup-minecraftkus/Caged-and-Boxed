package com.mrpup.caged_and_boxed.util;

import net.minecraft.world.entity.LivingEntity;

public enum CageSize {
    SMALL, MEDIUM, UNIVERSAL;

    public static CageSize fromEntity(LivingEntity entity) {
        float w = entity.getBbWidth();
        float h = entity.getBbHeight();

        if (w <= 1.0f && h <= 1.0f) return SMALL;
        if (w <= 1.0f && h <= 2.0f) return MEDIUM;
        return UNIVERSAL;
    }

    public boolean canFit(CageSize mobSize) {
        return mobSize.ordinal() <= this.ordinal();
    }

    public String displayName() {
        return switch (this) {
            case SMALL  -> "small";
            case MEDIUM -> "medium";
            case UNIVERSAL  -> "universal";
        };
    }
}
