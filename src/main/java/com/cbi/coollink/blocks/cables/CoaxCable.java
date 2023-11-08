package com.cbi.coollink.blocks.cables;

import com.cbi.coollink.items.CoaxialCable;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;

public class CoaxCable extends Block {

    public static final CoaxCable ENTRY =new CoaxCable(FabricBlockSettings.create().hardness(0.5f));

    static BooleanProperty north = BooleanProperty.of("north");
    static BooleanProperty east = BooleanProperty.of("east");
    static BooleanProperty south = BooleanProperty.of("south");
    static BooleanProperty west = BooleanProperty.of("west");
    static BooleanProperty up = BooleanProperty.of("up");
    static BooleanProperty down = BooleanProperty.of("down");

    public CoaxCable(Settings settings){
        super(settings);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager){
        north = BooleanProperty.of("north");
        east = BooleanProperty.of("east");
        south = BooleanProperty.of("south");
        west = BooleanProperty.of("west");
        up = BooleanProperty.of("up");
        down = BooleanProperty.of("down");

        stateManager.add(north);
        stateManager.add(east);
        stateManager.add(south);
        stateManager.add(west);
        stateManager.add(up);
        stateManager.add(down);

    }
}
