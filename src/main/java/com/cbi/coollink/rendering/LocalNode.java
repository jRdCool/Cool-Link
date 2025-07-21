package com.cbi.coollink.rendering;

import com.cbi.coollink.blocks.cables.createadditons.NodeRotation;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Encoder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

public class LocalNode {

    public static final String ID = "id";
    public static final String OTHER = "other";
    public static final String TYPE = "type";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";

    private final BlockEntity entity;

    private final int index;
    /**
     * The index of the node this node is connected to.
     */
    private final int otherIndex;
    /**
     * The type of wire used to connect to this node.
     */
    private final WireType type;
    /**
     * The relative position of this node from the original block entity.
     */
    private Vec3i relativePos;

    /**
     * Whether this node is invalid.
     */
    private boolean invalid = false;

    /**create a connection for a wire between ports
     * @param entity the block entity the connection starts from
     * @param index the index on the starting block entity
     * @param otherIndex the index on the destination block entity
     * @param type the type of wire to connect
     * @param position the position of the target block entity
     */
    public LocalNode(BlockEntity entity, int index, int otherIndex, WireType type, BlockPos position) {
        this.entity = entity;
        this.index = index;
        this.otherIndex = otherIndex;
        this.type = type;
        this.relativePos = position.subtract(entity.getPos());
    }

    public LocalNode(BlockEntity entity, LocalNodeConnection read) {
        this.entity = entity;
        this.index = read.index();
        this.otherIndex = read.otherIndex();
        this.type = WireType.fromIndex(read.type());
        this.relativePos = new Vec3i(read.x(), read.y(), read.z());
    }

    public LocalNode(BlockEntity entity, ReadView read) {
        this.entity = entity;
        this.index = read.getInt(ID,-1);
        this.otherIndex = read.getInt(OTHER,-1);
        this.type = WireType.fromIndex(read.getInt(TYPE,-1));
        this.relativePos = new Vec3i(read.getInt(X,0), read.getInt(Y,0), read.getInt(Z,0));
    }

//    public void write(NbtCompound tag) {
//        tag.putInt(ID, this.index);
//        tag.putInt(OTHER, this.otherIndex);
//        tag.putInt(TYPE, this.type.getIndex());
//        tag.putInt(X, this.relativePos.getX());
//        tag.putInt(Y, this.relativePos.getY());
//        tag.putInt(Z, this.relativePos.getZ());
//    }

    public LocalNodeConnection write(){
        return new LocalNodeConnection(index,otherIndex,type.getIndex(),relativePos.getX(),relativePos.getY(),relativePos.getZ());
    }

    public void write(WriteView writer){
        writer.putInt(ID, this.index);
        writer.putInt(OTHER, this.otherIndex);
        writer.putInt(TYPE, this.type.getIndex());
        writer.putInt(X, this.relativePos.getX());
        writer.putInt(Y, this.relativePos.getY());
        writer.putInt(Z, this.relativePos.getZ());
    }

    public void updateRelative(NodeRotation rotation) {
        this.relativePos = rotation.updateRelative(this.relativePos);
    }

    public int getIndex() {
        return index;
    }

    public int getOtherIndex() {
        return otherIndex;
    }

    public WireType getType() {
        return type;
    }

    public Vec3i getRelativePos() {
        return this.relativePos;
    }

    public BlockPos getTargetPos() {
        return new BlockPos(entity.getPos().getX()+relativePos.getX(),entity.getPos().getY()+relativePos.getY(),entity.getPos().getZ()+relativePos.getZ());
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void invalid() {
        this.invalid = true;
    }

    public BlockEntity getBlockEntity(){
        return entity;
    }


}
