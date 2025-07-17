package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.net.protocol.Mac;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class RedstoneControllerWiredBE extends BlockEntity implements IWireNode {

    private static final int deviceID = 0x52;

    public static RedstoneControllerWiredBE of( BlockPos pos, BlockState state,int type) {
        return switch (type){
            case 0 -> new RedstoneControllerWiredBE(pos, state, Main.RS_SENDER_WIRED_BLOCK_ENTITY);
            case 1 -> new RedstoneControllerWiredBE(pos, state, Main.RS_RECEIVER_WIRED_BLOCK_ENTITY);
            default -> null;
        };

    }

    public RedstoneControllerWiredBE(BlockPos pos, BlockState state, BlockEntityType<?> type) {
        super(type, pos, state);
        this.localNodes = new LocalNode[getNodeCount()];
    }

    public Mac mac;
    public ArrayList<String> deviceIP=new ArrayList<>();
    private final LocalNode[] localNodes;

    private static final int nodeCount = 1;
    private final boolean[] isNodeUsed = new boolean[nodeCount];

    @Override
    public Vec3d getNodeOffset(int node) {
        return null;
    }

    @Override
    public IWireNode getWireNode(int index) {
        return null;
    }

    @Override
    public int getOtherNodeIndex(int index) {
        return 0;
    }

    @Override
    public LocalNode getLocalNode(int index) {
        return null;
    }

    @Override
    public void setNode(int index, int otherNode, BlockPos pos, WireType type) {

    }

    @Override
    public void removeNode(int index, boolean dropWire) {

    }

    @Override
    public WireType getPortType(int index) {
        return WireType.CAT6;
    }

    @Override
    public boolean isNodeInUse(int index) {
        return false;
    }

    @Override
    public void setIsNodeUsed(int index, boolean set) {

    }


}
