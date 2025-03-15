package com.cbi.coollink.blocks.cables.createadditons;


import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.StringIdentifiable;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static net.minecraft.util.math.Direction.Axis.*;
import static net.minecraft.util.BlockRotation.*;

/**
 * A hacky, messy, and probably not the best solution to a problem making my
 * head spin.
 *
 * Used to recalculate the relative positions of wires & nodes.
 */
public enum NodeRotation implements StringIdentifiable {

	NONE(null, null),

	Y_CLOCKWISE_90(Y, CLOCKWISE_90),
	Y_CLOCKWISE_180(Y, CLOCKWISE_180),
	Y_COUNTERCLOCKWISE_90(Y, COUNTERCLOCKWISE_90),

	X_CLOCKWISE_90(X, CLOCKWISE_90),
	X_CLOCKWISE_180(X, CLOCKWISE_180),
	X_COUNTERCLOCKWISE_90(X, COUNTERCLOCKWISE_90),

	Z_CLOCKWISE_90(Z, CLOCKWISE_90),
	Z_CLOCKWISE_180(Z, CLOCKWISE_180),
	Z_COUNTERCLOCKWISE_90(Z, COUNTERCLOCKWISE_90);

	public static final EnumProperty<NodeRotation> ROTATION = EnumProperty.of("rotation", NodeRotation.class);
	public static final NodeRotation[] VALUES = values();

	final Direction.Axis axis;
	final BlockRotation rotation;

	NodeRotation(Direction.Axis axis, BlockRotation rotation) {
		this.axis = axis;
		this.rotation = rotation;
	}

	public Direction rotate(Direction current, boolean handleY) {
		if (this == NONE) return current;
		if (!handleY && this.axis == Y && current.getAxis() == Y) return current;
		if (this.rotation == COUNTERCLOCKWISE_90) return current.rotateCounterclockwise(this.axis);
		if (this.rotation == CLOCKWISE_90) return current.rotateClockwise(this.axis);
		return current.getOpposite();
	}

	public Vec3i updateRelative(Vec3i current) {
		// Calculate the new relative position in a left-handed coordinate system.
		if (this == NONE) return current;
		if (this.axis == Y) {
			if (this.rotation == CLOCKWISE_90) return new Vec3i(-current.getZ(), current.getY(), current.getX());
			if (this.rotation == CLOCKWISE_180) return new Vec3i(-current.getX(), current.getY(), -current.getZ());
			if (this.rotation == COUNTERCLOCKWISE_90) return new Vec3i(current.getZ(), current.getY(), -current.getX());
		}
		if (this.axis == X) {
			if (this.rotation == CLOCKWISE_90) return new Vec3i(current.getX(), current.getZ(), -current.getY());
			if (this.rotation == CLOCKWISE_180) return new Vec3i(current.getX(), -current.getY(), -current.getZ());
			if (this.rotation == COUNTERCLOCKWISE_90) return new Vec3i(current.getX(), -current.getZ(), current.getY());
		}
		if (this.axis == Z) {
			if (this.rotation == CLOCKWISE_90) return new Vec3i(current.getY(), -current.getX(), current.getZ());
			if (this.rotation == CLOCKWISE_180) return new Vec3i(-current.getX(), -current.getY(), current.getZ());
			if (this.rotation == COUNTERCLOCKWISE_90) return new Vec3i(-current.getY(), current.getX(), current.getZ());
		}
		return current;
	}

	public Direction.Axis getAxis() {
		return axis;
	}

	public BlockRotation getRotation() {
		return rotation;
	}


	public @NotNull String getSerializedName() {
		return this.name().toLowerCase(Locale.ROOT);
	}

	public static NodeRotation get(Direction.Axis axis, BlockRotation rotation) {
		if (rotation == BlockRotation.NONE) return NONE;
		for (NodeRotation wr : VALUES) {
			if (wr.axis == axis && wr.rotation == rotation) return wr;
		}
		return NONE;
	}

	@Override
	public String asString() {
		return null;
	}
}
