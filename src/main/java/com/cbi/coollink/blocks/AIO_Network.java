package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class AIO_Network extends Block{
	public static final AIO_Network ENTRY = new AIO_Network(FabricBlockSettings.of(Material.STONE).hardness(0.5f));
	public AIO_Network(Settings settings) {
		super(settings);
	}
}