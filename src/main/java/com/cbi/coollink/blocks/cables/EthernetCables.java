package com.cbi.coollink.blocks.cables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;

public abstract class EthernetCables extends Block {

    public static IntProperty shape = IntProperty.of("shape",0,15);
    //NS,EW,UD,NE,NW,SE,SW,NU,SU,EU,WU,ND,SD,ED,WD
    // 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15

    public EthernetCables(Settings settings){
        super(settings);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(shape);
    }





}
