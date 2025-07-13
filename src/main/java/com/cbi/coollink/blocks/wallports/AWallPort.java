package com.cbi.coollink.blocks.wallports;

import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.util.math.Direction;

import static net.minecraft.state.property.Properties.FACING;

public abstract class AWallPort extends BlockWithEntity implements BlockEntityProvider {
    protected AWallPort(Settings settings,String woodType) {
        super(settings);
        setDefaultState(getDefaultState()
                .with(FACING, Direction.NORTH)
        );
        this.woodType=woodType;
    }//Constructor


    //Variables
    protected final String woodType;

}
