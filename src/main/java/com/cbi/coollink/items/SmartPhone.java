package com.cbi.coollink.items;

import com.cbi.coollink.Main;
import com.cbi.coollink.guis.PhoneGui;
import com.cbi.coollink.guis.PhoneScreen;
import com.cbi.coollink.net.OpenPhoneGuiPacket;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class SmartPhone extends Item {


    public SmartPhone(Settings settings) {
        super(settings);

    }
    public BlockEntity usedBlockEntity;



    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand){

        String currentThread = Thread.currentThread().getName();
        //check witch thread the code is being executed on the server
        if(currentThread.equals("Server thread")) {//if the code is being executed on the server thread
            //Main.LOGGER.info("normal");
            //open the phone GUI
            ItemStack heldItem = null;

            for (ItemStack itemStack : user.getHandItems()) {
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
            }else{
                noBLockEntity = true;
                blockEntityPos = new BlockPos(0,0,0);
            }

            //open the phone screen
            OpenPhoneGuiPacket packet = new OpenPhoneGuiPacket(world.getRegistryKey(),blockEntityPos,heldItem,noBLockEntity);
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
                //Main.LOGGER.info(be.getClass().getName());
                usedBlockEntity=be;
            }

        }
        return ActionResult.PASS;
    }

}
