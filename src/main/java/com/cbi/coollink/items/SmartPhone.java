package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.OpenPhoneGuiPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.NonNull;


public class SmartPhone extends Item {


    public static final Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"smart_phone");
    public static final ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
    public SmartPhone(Properties settings) {
        super(settings);

    }
    private static final int deviceID = 0x31;


    @Override
    public @NonNull InteractionResult use(@NonNull Level world, @NonNull Player user, @NonNull InteractionHand hand) {
        String currentThread = Thread.currentThread().getName();
        //check witch thread the code is being executed on the server
        if(currentThread.equals("Server thread")) {//if the code is being executed on the server thread
            //Main.LOGGER.info("normal");
            //open the phone GUI
            ItemStack heldItem = user.getItemInHand(hand);

//            BlockPos blockEntityPos;
//            boolean noBLockEntity;
//            if(usedBlockEntity!=null){
//                blockEntityPos=usedBlockEntity.getPos();
//                noBLockEntity = false;
//                //TODO implement this
//                if(usedBlockEntity instanceof AIOBlockEntity aio) {
//                    ServerPlayNetworking.send((ServerPlayerEntity) user, new AioSyncMacPacket(blockEntityPos, aio.mac1.getBytes(), aio.mac2.getBytes(), world.getRegistryKey()));
//                }
//            }else{
//                noBLockEntity = true;
//                blockEntityPos = new BlockPos(0,0,0);
//            }

            //open the phone screen
            OpenPhoneGuiPacket packet = new OpenPhoneGuiPacket(world.dimension(),heldItem,user.position());

            ServerPlayNetworking.send((ServerPlayer) user,packet);


        }

        return super.use(world, user, hand);
    }

    //use on block
    @Override
    public @NonNull InteractionResult useOn(@NonNull UseOnContext context) {
        if(Thread.currentThread().getName().equals("Server thread")) {
            BlockPos pos = context.getClickedPos();
            //Main.LOGGER.info(pos.toShortString());
            BlockEntity be = context.getLevel().getBlockEntity(pos);
            if (be != null) {
                CompoundTag nbt;
                var rawData = context.getItemInHand().get(DataComponents.CUSTOM_DATA);
                if(rawData!=null){
                    nbt = rawData.copyTag();
                }else{
                    nbt = new CompoundTag();
                }
                nbt.putIntArray("BePos",new int[]{be.getBlockPos().getX(),be.getBlockPos().getY(),be.getBlockPos().getZ()});
                context.getItemInHand().set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            }



        }
        return InteractionResult.PASS;
    }
}
