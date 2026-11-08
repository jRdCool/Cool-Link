package com.cbi.coollink.blocks.conduits;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class LargeConduit extends Conduit {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"large_conduit");
    public static final ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final LargeConduit ENTRY = new LargeConduit(BlockBehaviour.Properties.of().strength(0.5f));

    //cableShape is an integer that is used to switch between the models
    //  0 = NS
    //  1 = EW
    //  2 = Junction Box (3 or 4 directions)
    //  3 = Vertical Transition Box
    //  4 = NE
    //  5 = SE
    //  6 = SW
    //  7 = NW


    public LargeConduit(BlockBehaviour.Properties settings) {
        super(settings.setId(BLOCK_KEY));
        registerDefaultState(defaultBlockState()
                .setValue(cableLevel,3)
        );
    }
    //@SuppressWarnings({"deprecation","all"})
    @Override
    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level, @NonNull BlockPos pos, @NonNull CollisionContext context) {
        VoxelShape shape= Shapes.empty();
        //use a different hit box based on the rotation of the block
        if(state.getValue(Conduit.HIDDEN)){
            shape = Shapes.box(0,0,0,1,1,1);
        }else {
            if (state.getValue(cableShape) == 0) {
                shape = Shapes.join(shape, makeShapeNS(), BooleanOp.OR);
            }
            if (state.getValue(cableShape) == 1) {
                shape = Shapes.join(shape, makeShapeEW(), BooleanOp.OR);
            }
            if (state.getValue(cableShape) == 2) {
                shape = junctionBoxVoxel();
            }
            if (state.getValue(cableShape) == 4) {
                shape = Shapes.join(shape, makeShapeNE(), BooleanOp.OR);
            }
            if (state.getValue(cableShape) == 5) {
                shape = Shapes.join(shape, makeShapeSE(), BooleanOp.OR);
            }
            if (state.getValue(cableShape) == 6) {
                shape = Shapes.join(shape, makeShapeSW(), BooleanOp.OR);
            }
            if (state.getValue(cableShape) == 7) {
                shape = Shapes.join(shape, makeShapeNW(), BooleanOp.OR);
            }
            if (shape.isEmpty()) {
                shape = makeShapeNS();
            }
        }
        return shape;
    }


    public VoxelShape makeShapeNS() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0, 0.9375, 0.25, 1), BooleanOp.OR);
        return shape;
    }
    public VoxelShape makeShapeEW() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.9375, 1, 0.25, 0.94375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.05625, 1, 0.25, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.0625, 1, 0.25, 0.9375), BooleanOp.OR);
        return shape;
    }
    public VoxelShape makeShapeNE() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0, 0.94375, 0.25, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.9375, 1, 0.25, 0.94375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0.05625, 1, 0.25, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.05625, 0, 0, 0.0625, 0.25, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0, 0.9375, 0.25, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0.0625, 1, 0.25, 0.9375), BooleanOp.OR);
        return shape;
    }
    public VoxelShape makeShapeSE() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0.9375, 1, 0.25, 0.94375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.05625, 0, 0.0625, 0.0625, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0.9375, 0.94375, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.05625, 1, 0.25, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 1, 0.25, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.9375, 0.9375, 0.25, 1), BooleanOp.OR);

        return shape;
    }
    public VoxelShape makeShapeSW() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0.05625, 0, 0.9375, 0.0625, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.05625, 0.9375, 0.25, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.9375, 0.0625, 0.25, 0.94375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0.0625, 0.94375, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.25, 1), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.0625, 0.0625, 0.25, 0.9375), BooleanOp.OR);
        return shape;
    }
    public VoxelShape makeShapeNW() {
        VoxelShape shape = Shapes.empty();
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.05625, 0.0625, 0.25, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.9375, 0, 0, 0.94375, 0.25, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.05625, 0, 0, 0.0625, 0.25, 0.0625), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.9375, 0.9375, 0.25, 0.94375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0, 0, 0.0625, 0.9375, 0.25, 0.9375), BooleanOp.OR);
        shape = Shapes.join(shape, Shapes.box(0.0625, 0, 0, 0.9375, 0.25, 0.0625), BooleanOp.OR);
        return shape;
    }


    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new ConduitBlockEntity(pos, state, Main.LARGE_CONDUIT_BLOCK_ENTITY);
    }
}
