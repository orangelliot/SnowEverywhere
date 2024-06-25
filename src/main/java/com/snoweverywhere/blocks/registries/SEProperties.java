package com.snoweverywhere.blocks.registries;

import net.minecraft.state.property.BooleanProperty;

public class SEProperties {
    public static final BooleanProperty SNOWCOVERED = BooleanProperty.of("snowcovered");
    public static final BooleanProperty SNOWLOGGED = BooleanProperty.of("snowlogged");

    public static void registerProperties(){}
}