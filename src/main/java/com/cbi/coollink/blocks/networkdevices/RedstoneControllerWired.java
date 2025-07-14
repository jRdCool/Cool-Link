package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.mojang.serialization.MapCodec;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public abstract class RedstoneControllerWired extends BlockWithEntity implements BlockEntityProvider {

    private static final IntProperty POWER;
    static {
        POWER = Properties.POWER;
    }


    //public static final RedstoneControllerWired ENTRY = new RedstoneControllerWired(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY));
    private static final int REGULAR_POWER_DELAY = 8;

    private static final int RECOVERABLE_POWER_DELAY = 20;

    protected RedstoneControllerWired(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)this.stateManager.getDefaultState()).with(POWER, 0));
    }//Constructor

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    private static void setPower(WorldAccess world, BlockState state, int power, BlockPos pos, int delay) {
        world.setBlockState(pos, (BlockState)state.with(POWER, power), 3);
        world.scheduleBlockTick(pos, state.getBlock(), delay);
    }

    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if ((Integer)state.get(POWER) != 0) {
            world.setBlockState(pos, (BlockState)state.with(POWER, 0), 3);
        }

    }

    protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        return (Integer)state.get(POWER);
    }

    protected boolean emitsRedstonePower(BlockState state) {
        return true;
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{POWER});
    }

    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient() && !state.isOf(oldState.getBlock())) {
            if ((Integer)state.get(POWER) > 0 && !world.getBlockTickScheduler().isQueued(pos, this)) {
                world.setBlockState(pos, (BlockState)state.with(POWER, 0), 18);
            }

        }
    }



}
