package com.cbi.coollink.blocks;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public class TestBlock extends Block {
    public static final TestBlock ENTRY = new TestBlock(AbstractBlock.Settings.create().hardness(0.5f));
    public TestBlock(Settings settings) {
        super(settings);
    }
}
