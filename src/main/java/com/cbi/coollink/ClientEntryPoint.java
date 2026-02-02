package com.cbi.coollink;

import com.cbi.coollink.app.*;
import com.cbi.coollink.blocks.ServerRack;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import com.cbi.coollink.blocks.cables.CoaxCable;
import com.cbi.coollink.blocks.networkdevices.AIO_Network;
import com.cbi.coollink.blocks.networkdevices.SatelliteDishBlock;
import com.cbi.coollink.blocks.networkdevices.SwitchSimple;
import com.cbi.coollink.guis.*;
import com.cbi.coollink.net.*;
import com.cbi.coollink.net.protocol.IpDataPacket;
import com.cbi.coollink.rendering.blockentities.SatelliteDishBlockEntityRenderer;
import com.cbi.coollink.rendering.blockentities.ServerRackBlockEntityRenderer;
import com.cbi.coollink.rendering.WireNodeRenderer;
import com.cbi.coollink.rendering.blockentities.ConduitBlockEntityRender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.Objects;

public class ClientEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //register client things here
        AppRegistry.registerApp(HelpPagesApp.ID,HelpPagesApp::new,null,Text.of("Documentation / reference for how to use the mod Cool Link"));
        AppRegistry.registerApp(ExampleApp.ID,ExampleApp::new,null, Text.of("this is a test app\nlets try 2 lines"));
        AppRegistry.registerApp(SnakeGameApp.appID, SnakeGameApp::new, SnakeGameApp.ICON,Text.of("Snake Game!"));
        AppRegistry.registerApp(TerminalPhoneApp.ID, TerminalPhoneApp::new, TerminalPhoneApp.ICON, Text.of("Terminal for your phone"));
        AppRegistry.registerApp(ConduitHiderApp.ID, ConduitHiderApp::new, null,Text.of("Quick and dirty app to change the cover of a conduit"), ConduitHiderApp::openOnBlockEntity);


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

            ItemStack heldItem = payload.heldItem();

            context.client().execute( () -> context.client().setScreen(new PhoneScreen(new PhoneGui(world, heldItem,payload.playerPos()))));
        });

        ClientPlayNetworking.registerGlobalReceiver(OpenConduitGuiPacket.ID,(payload, context) -> context.client().execute( ()-> context.client().setScreen(new BasicScreen(new ConduitGUI()))));

        ClientPlayNetworking.registerGlobalReceiver(AioSyncMacPacket.ID,(payload, context) -> {
            context.client().execute(()->{
                World clientWorld=context.client().world;
                if(clientWorld == null){
                    Main.LOGGER.error("WORLD IS NULL!");
                    return;
                }
                if(clientWorld.getRegistryKey().equals(payload.world())){
                    try {
                        BlockEntity be = clientWorld.getBlockEntity(payload.pos());
                        if(be instanceof AIOBlockEntity aio){
                            aio.setMacAddresses(payload.mac1(),payload.mac2());
                        }
                    }catch(Exception ignored){}
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
                    if(!(clientWorld.getBlockEntity(payload.pos()) instanceof ConduitBlockEntity)){
                        context.client().setScreen(new BasicScreen(new PortSelectGUI(payload.ofType(),payload.type(),clickedOnBlockEntity,payload.heldItem())));
                    }
                    else{
                        context.client().setScreen(new BasicScreen(new PortSelectGUIConduit(payload.ofType(),payload.type(),clickedOnBlockEntity,payload.heldItem())));
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AccessPointLocationPacket.ID, (accessPointLocationPacket, context) -> {
            if(context.client().currentScreen instanceof PhoneScreen screen){
                if(screen.getDescription() instanceof PhoneGui phone){
                    phone.accessPointLocationResponse(accessPointLocationPacket.aps(), accessPointLocationPacket.ssid());
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientWifiConnectionResultPacket.ID, (clientWifiConnectionResultPacket, context) -> {
            if(context.client().currentScreen instanceof PhoneScreen screen){
                if(screen.getDescription() instanceof PhoneGui phone){
                    phone.handleWifiConnectionResponse(clientWifiConnectionResultPacket);
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(WIFIServerIpPacket.ID, (wifiServerIpPacket, context) -> {
            if(context.client().currentScreen instanceof PhoneScreen screen){
                if(screen.getDescription() instanceof PhoneGui phone){
                    phone.handleIncomingDataPacket(wifiServerIpPacket.data());
                }
            }else{
                //send back a device disconnect packet
                NbtCompound response = new NbtCompound();
                response.putString("type","disconnect");
                ClientPlayNetworking.send(new WIFIClientIpPacket(Objects.requireNonNull(context.client().world).getRegistryKey(),wifiServerIpPacket.apPos(),new IpDataPacket("169.0.0.1",wifiServerIpPacket.data().getDestinationIpAddress(),wifiServerIpPacket.data().getDestinationMacAddress(),response)));
            }
        });

        BlockEntityRendererFactories.register(Main.AIO_BLOCK_ENTITY,WireNodeRenderer::new);
        BlockEntityRendererFactories.register(Main.SWITCH_SIMPLE_BLOCK_ENTITY,WireNodeRenderer::new);
        BlockEntityRendererFactories.register(Main.SATELLITE_DISH_BLOCK_ENTITY, SatelliteDishBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(Main.LARGE_CONDUIT_BLOCK_ENTITY, ConduitBlockEntityRender::new);
        BlockEntityRendererFactories.register(Main.SMALL_CONDUIT_BLOCK_ENTITY, ConduitBlockEntityRender::new);
        BlockEntityRendererFactories.register(Main.MEDIUM_CONDUIT_BLOCK_ENTITY, ConduitBlockEntityRender::new);


        BlockRenderLayerMap.putBlock(AIO_Network.ENTRY, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(ServerRack.ENTRY, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(SatelliteDishBlock.ENTRY, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(CoaxCable.ENTRY, BlockRenderLayer.CUTOUT);
        BlockRenderLayerMap.putBlock(SwitchSimple.ENTRY, BlockRenderLayer.CUTOUT);
    }
}
