package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.net.AioSyncMacPacket;
import com.cbi.coollink.net.OpenPhoneGuiPacket;
import com.cbi.coollink.net.protocol.Mac;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class SmartPhone extends Item {


    public SmartPhone(Settings settings) {
        super(settings);

    }
    public BlockEntity usedBlockEntity;
    public Mac mac;
    private static final int deviceID = 0x31;



    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand){

        String currentThread = Thread.currentThread().getName();
        //check witch thread the code is being executed on the server
        if(currentThread.equals("Server thread")) {//if the code is being executed on the server thread
            //Main.LOGGER.info("normal");
            //open the phone GUI
            ItemStack heldItem = null;

            if(mac==null){
                mac=new Mac(deviceID);
            }

            for (ItemStack itemStack : new ItemStack[]{user.getMainHandStack(),user.getOffHandStack()}) {
                if(itemStack.getItem().equals(this)){
                    heldItem=itemStack;
                    break;
                }
            }
            if(heldItem==null){
                Main.LOGGER.error("attempted to open phone GUI when no phone was held in hand");
                return super.use(world, user, hand);
            }
            BlockPos blockEntityPos;
            boolean noBLockEntity;
            if(usedBlockEntity!=null){
                blockEntityPos=usedBlockEntity.getPos();
                noBLockEntity = false;
                //TODO implement this
                if(usedBlockEntity instanceof AIOBlockEntity aio) {
                    ServerPlayNetworking.send((ServerPlayerEntity) user, new AioSyncMacPacket(blockEntityPos, aio.mac1.getBytes(), aio.mac2.getBytes(), world.getRegistryKey()));
                }
            }else{
                noBLockEntity = true;
                blockEntityPos = new BlockPos(0,0,0);
            }

            //open the phone screen
            OpenPhoneGuiPacket packet = new OpenPhoneGuiPacket(world.getRegistryKey(),blockEntityPos,heldItem,noBLockEntity,user.getPos());

            ServerPlayNetworking.send((ServerPlayerEntity) user,packet);


            //reset the used block entity
            usedBlockEntity=null;

        }

        return super.use(world, user, hand);
    }

    public ActionResult useOnBlock(ItemUsageContext context) {//this gets called before use if used on a block
        if(Thread.currentThread().getName().equals("Render thread")) {
            BlockPos pos = context.getBlockPos();
            //Main.LOGGER.info(pos.toShortString());
            BlockEntity be = context.getWorld().getBlockEntity(pos);
            if (be != null) {
                //this is prbly a terrible way to do this
                usedBlockEntity=be;
            }

        }
        return ActionResult.PASS;
    }

}
