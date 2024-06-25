package com.snoweverywhere;

import java.util.List;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class ModConfig {

    public ModConfig(){
        allowedBlocks.add("minecraft:oak_stairs");
    }

    @SerializedName("allowed_blocks")
    public List<String> allowedBlocks = new ArrayList<String>();

    // Add a method to get a default instance
    public static ModConfig getDefaultConfig() {
        return new ModConfig();
    }
}