package com.cbi.coollink.blocks.networkdevices;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.RedstoneControllerWiredBE;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKey;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.state.property.Properties.FACING;

public class RSReceiverWired extends RedstoneControllerWired{
    public RSReceiverWired(Settings settings) {
        super(settings);
    }

    private static final BooleanProperty POWERED = Properties.POWERED;

    public static final Identifier ID = Identifier.of(Main.namespace,"redstone_receiver_wired");
    public static final RegistryKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
    public static final RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public static final RSReceiverWired ENTRY = new RSReceiverWired(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY));


    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RedstoneControllerWiredBE(pos,state,Main.RS_RECEIVER_WIRED_BLOCK_ENTITY);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {return true;}

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING)
                .add(POWERED);
        super.appendProperties(builder);
    }
}
