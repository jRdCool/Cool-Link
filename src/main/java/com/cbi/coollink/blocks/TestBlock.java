package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class TestBlock extends Block {
    public static final TestBlock ENTRY = new TestBlock(FabricBlockSettings.of(Material.STONE).hardness(0.5f));
    public TestBlock(Settings settings) {
        super(settings);
    }
}
