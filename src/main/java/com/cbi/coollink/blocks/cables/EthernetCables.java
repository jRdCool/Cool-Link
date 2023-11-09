package com.cbi.coollink.blocks.cables;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class EthernetCables extends Block {

    public static BooleanProperty north=BooleanProperty.of("north");
    public static BooleanProperty south=BooleanProperty.of("south");
    public static BooleanProperty east=BooleanProperty.of("east");
    public static BooleanProperty west=BooleanProperty.of("west");
    public static BooleanProperty up=BooleanProperty.of("up");
    public static BooleanProperty down=BooleanProperty.of("down");
    public static IntProperty shape = IntProperty.of("shape",0,15);
    //NS,EW,UD,NE,NW,SE,SW,NU,SU,EU,WU,ND,SD,ED,WD
    // 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12,13,14,15

    public EthernetCables(Settings settings){
        super(settings);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(shape)
                .add(north)
                .add(south)
                .add(east)
                .add(west);
    }



    public boolean isFacingMultiDirection(World world, BlockPos pos) {
        BlockState state=world.getBlockState(pos);
        if(state.getBlock() instanceof EthernetCables){
            if(   ((state.get(north) && (state.get(south)|state.get(east) |state.get(west)|state.get(up)  |state.get(down) ) ))
                    ||((state.get(south) && (state.get(north)|state.get(east) |state.get(west)|state.get(up)  |state.get(down) ) ))
                    ||((state.get(east)  && (state.get(north)|state.get(south)|state.get(west)|state.get(up)  |state.get(down) ) ))
                    ||((state.get(west)  && (state.get(north)|state.get(south)|state.get(east)|state.get(up)  |state.get(down) ) ))
                    ||((state.get(up)    && (state.get(north)|state.get(south)|state.get(east)|state.get(west)|state.get(down) ) ))
                    ||((state.get(down)  && (state.get(north)|state.get(south)|state.get(east)|state.get(west)|state.get(up)   ) ))
            ){
                return true;
            }
        }
        return false;
    }


}
