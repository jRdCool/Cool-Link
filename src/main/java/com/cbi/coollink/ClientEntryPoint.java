package com.cbi.coollink;

import com.cbi.coollink.app.AppRegistry;
import com.cbi.coollink.app.ExampleApp;
import com.cbi.coollink.app.SnakeGameApp;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.guis.*;
import com.cbi.coollink.net.AioSyncMacPacket;
import com.cbi.coollink.net.OpenConduitGuiPacket;
import com.cbi.coollink.net.OpenPhoneGuiPacket;
import com.cbi.coollink.net.OpenPortSelectGuiPacket;
import com.cbi.coollink.rendering.blockentities.ServerRackBlockEntityRenderer;
import com.cbi.coollink.rendering.WireNodeRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class ClientEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //register client things here
        AppRegistry.registerApp(new ExampleApp());
        AppRegistry.registerApp(new SnakeGameApp());


        BlockEntityRendererFactories.register(Main.SERVER_RACK_BLOCK_ENTITY, ServerRackBlockEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(OpenPhoneGuiPacket.ID,(payload,context) -> {
            RegistryKey<World> wrk = payload.world();
            World world = context.client().world;

            if(world==null){
                Main.LOGGER.error("Something has gon massively wrong client is not in a world 💀");
                return;
            }
            if(!world.getRegistryKey().equals(wrk)){
                Main.LOGGER.error("Something has gon massively wrong client world does not match 💀");
                return;
            }
            BlockPos blockEntityPos = payload.block();

            ItemStack heldItem = payload.heldItem();

            boolean noBLockEntity = payload.noBlockEntity();
            BlockEntity usedBlockEntity;
            if(!noBLockEntity)
                usedBlockEntity = Objects.requireNonNull(world).getBlockEntity(blockEntityPos);
            else {
                usedBlockEntity = null;
            }


            context.client().execute( () -> {
                context.client().setScreen(new PhoneScreen(new PhoneGui(world, usedBlockEntity, heldItem,payload.playerPos())));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(OpenConduitGuiPacket.ID,(payload, context) -> {
            context.client().execute( ()->{
                context.client().setScreen(new BasicScreen(new ConduitGUI()));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AioSyncMacPacket.ID,(payload, context) -> {
            context.client().execute(()->{
                if(context.client().world.getRegistryKey().equals(payload.world())){
                    try {
                        BlockEntity be = context.client().world.getBlockEntity(payload.pos());
                        if(be instanceof AIOBlockEntity aio){
                            aio.setMacAddresses(payload.mac1(),payload.mac2());
                        }
                    }catch(Exception e){}
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(OpenPortSelectGuiPacket.ID, (payload, context) -> {
            World clientWorld = context.client().world;
            if(clientWorld == null){
                return;
            }
            context.client().execute(() ->{
                if(clientWorld.getRegistryKey().equals(payload.world())){
                    BlockEntity clickedOnBlockEntity = clientWorld.getBlockEntity(payload.pos());
                    context.client().setScreen(new BasicScreen(new PortSelectGUI(payload.ofType(),payload.type(),clickedOnBlockEntity,payload.heldItem())));
                }
            });
        });

        BlockEntityRendererFactories.register(Main.AIO_BLOCK_ENTITY,WireNodeRenderer::new);


    }
}
