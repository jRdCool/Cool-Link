package com.cbi.coollink.rendering;

import com.cbi.coollink.blocks.cables.createadditons.NodeRotation;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class LocalNode {

    public static final String NODES = "nodes";
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
    private Vec3d test;

    /**
     * Whether this node is invalid.
     */
    private boolean invalid = false;

    public LocalNode(BlockEntity entity, int index, int other, WireType type, BlockPos position) {
        this.entity = entity;
        this.index = index;
        this.otherIndex = other;
        this.type = type;
        this.relativePos = position.subtract(entity.getPos());
    }

    public LocalNode(BlockEntity entity, NbtCompound tag) {
        this.entity = entity;
        this.index = tag.getInt(ID);
        this.otherIndex = tag.getInt(OTHER);
        this.type = WireType.fromIndex(tag.getInt(TYPE));
        this.relativePos = new Vec3i(tag.getInt(X), tag.getInt(Y), tag.getInt(Z));
    }

    public void write(NbtCompound tag) {
        tag.putInt(ID, this.index);
        tag.putInt(OTHER, this.otherIndex);
        tag.putInt(TYPE, this.type.getIndex());
        tag.putInt(X, this.relativePos.getX());
        tag.putInt(Y, this.relativePos.getY());
        tag.putInt(Z, this.relativePos.getZ());
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

    public BlockPos getPos() {
        return entity.getPos().offset(Direction.fromVector(this.relativePos.getX(),this.relativePos.getY(),this.relativePos.getZ()));
    }

    public boolean isInvalid() {
        return invalid;
    }

    public void invalid() {
        this.invalid = true;
    }
}
