package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;

public class TestBlock extends Block {
    public static final TestBlock ENTRY = new TestBlock(FabricBlockSettings.create().hardness(0.5f));
    public TestBlock(Settings settings) {
        super(settings);
    }
}
