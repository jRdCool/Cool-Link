package com.cbi.coollink.blocks;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Material;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static com.cbi.coollink.Main.ASSEMBLED_BOOLEAN_PROPERTY;

public class ServerRack extends Block {
    public static final ServerRack ENTRY = new ServerRack(FabricBlockSettings.of(Material.STONE).hardness(0.5f));

    public enum Half implements StringIdentifiable{
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        Half(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    };


    public ServerRack(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(half,Half.BOTTOM));
    }

    static EnumProperty<ServerRack.Half> half = EnumProperty.of("half", ServerRack.Half.class);

    static void assignStates(){
        half = EnumProperty.of("half", ServerRack.Half.class);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        assignStates();
        stateManager.add(half);
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView view, BlockPos pos, ShapeContext context) {
        return makeShape();
    }

    public VoxelShape makeShape(){
        VoxelShape shape = VoxelShapes.empty();
        shape = VoxelShapes.union(shape, VoxelShapes.cuboid(0, 0, 0, 1, 1, 1));

        return shape;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.setBlockState(new BlockPos(pos.getX(),pos.getY()+1,pos.getZ()),state.with(half,Half.TOP));
    }
}
