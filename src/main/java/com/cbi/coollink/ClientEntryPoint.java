package com.cbi.coollink;

import com.cbi.coollink.app.*;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.blocks.blockentities.ConduitBlockEntity;
import com.cbi.coollink.guis.*;
import com.cbi.coollink.net.*;
import com.cbi.coollink.net.protocol.IpDataPacket;
import com.cbi.coollink.rendering.blockentities.SatelliteDishBlockEntityRenderer;
import com.cbi.coollink.rendering.blockentities.ServerRackBlockEntityRenderer;
import com.cbi.coollink.rendering.WireNodeRenderer;
import com.cbi.coollink.rendering.blockentities.ConduitBlockEntityRender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Objects;

public class ClientEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //register client things here
        AppRegistry.registerApp(HelpPagesApp.ID,HelpPagesApp::new,null, Component.literal("Documentation / reference for how to use the mod Cool Link"));
        AppRegistry.registerApp(ExampleApp.ID,ExampleApp::new,null, Component.literal("this is a test app\nlets try 2 lines"));
        AppRegistry.registerApp(SnakeGameApp.appID, SnakeGameApp::new, SnakeGameApp.ICON,Component.literal("Snake Game!"));
        AppRegistry.registerApp(TerminalPhoneApp.ID, TerminalPhoneApp::new, TerminalPhoneApp.ICON, Component.literal("Terminal for your phone"));
        AppRegistry.registerApp(ConduitHiderApp.ID, ConduitHiderApp::new, null,Component.literal("Quick and dirty app to change the cover of a conduit"), ConduitHiderApp::openOnBlockEntity);




        ClientPlayNetworking.registerGlobalReceiver(OpenPhoneGuiPacket.ID,(payload,context) -> {
            ResourceKey<Level> wrk = payload.world();
            Level world = context.client().level;

            if(world==null){
                Main.LOGGER.error("Something has gon massively wrong client is not in a world 💀");
                return;
            }
            if(!world.dimension().equals(wrk)){
                Main.LOGGER.error("Something has gon massively wrong client world does not match 💀");
                return;
            }

            ItemStack heldItem = payload.heldItem();

            context.client().execute( () -> context.client().setScreenAndShow(new PhoneScreen(new PhoneGui(world, heldItem,payload.playerPos()))));
        });

        ClientPlayNetworking.registerGlobalReceiver(OpenConduitGuiPacket.ID,(payload, context) -> context.client().execute( ()-> context.client().setScreenAndShow(new BasicScreen(new ConduitGUI()))));

        ClientPlayNetworking.registerGlobalReceiver(AioSyncMacPacket.ID,(payload, context) -> {
            context.client().execute(()->{
                Level clientWorld=context.client().level;
                if(clientWorld == null){
                    Main.LOGGER.error("WORLD IS NULL!");
                    return;
                }
                if(clientWorld.dimension().equals(payload.world())){
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
            Level clientWorld = context.client().level;
            if(clientWorld == null){
                return;
            }
            context.client().execute(() ->{
                if(clientWorld.dimension().equals(payload.world())){
                    BlockEntity clickedOnBlockEntity = clientWorld.getBlockEntity(payload.pos());
                    if(!(clientWorld.getBlockEntity(payload.pos()) instanceof ConduitBlockEntity)){
                        context.client().setScreenAndShow(new BasicScreen(new PortSelectGUI(payload.ofType(),payload.wireType(),clickedOnBlockEntity,payload.heldItem())));
                    }
                    else{
                        context.client().setScreenAndShow(new BasicScreen(new PortSelectGUIConduit(payload.ofType(),payload.wireType(),clickedOnBlockEntity,payload.heldItem())));
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(AccessPointLocationPacket.ID, (accessPointLocationPacket, context) -> {
            if(context.client().gui.screen() instanceof PhoneScreen screen){
                if(screen.getDescription() instanceof PhoneGui phone){
                    phone.accessPointLocationResponse(accessPointLocationPacket.aps(), accessPointLocationPacket.ssid());
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(ClientWifiConnectionResultPacket.ID, (clientWifiConnectionResultPacket, context) -> {
            if(context.client().gui.screen() instanceof PhoneScreen screen){
                if(screen.getDescription() instanceof PhoneGui phone){
                    phone.handleWifiConnectionResponse(clientWifiConnectionResultPacket);
                }
            }
        });

        ClientPlayNetworking.registerGlobalReceiver(WIFIServerIpPacket.ID, (wifiServerIpPacket, context) -> {
            if(context.client().gui.screen() instanceof PhoneScreen screen){
                if(screen.getDescription() instanceof PhoneGui phone){
                    phone.handleIncomingDataPacket(wifiServerIpPacket.data());
                }
            }else{
                //send back a device disconnect packet
                CompoundTag response = new CompoundTag();
                response.putString("type","disconnect");
                ClientPlayNetworking.send(new WIFIClientIpPacket(Objects.requireNonNull(context.client().level).dimension(),wifiServerIpPacket.apPos(),new IpDataPacket("169.0.0.1",wifiServerIpPacket.data().getDestinationIpAddress(),wifiServerIpPacket.data().getDestinationMacAddress(),response)));
            }
        });

        BlockEntityRenderers.register(Main.SERVER_RACK_BLOCK_ENTITY, ServerRackBlockEntityRenderer::new);
        BlockEntityRenderers.register(Main.AIO_BLOCK_ENTITY,WireNodeRenderer::new);
        BlockEntityRenderers.register(Main.SWITCH_SIMPLE_BLOCK_ENTITY,WireNodeRenderer::new);
        BlockEntityRenderers.register(Main.SATELLITE_DISH_BLOCK_ENTITY, SatelliteDishBlockEntityRenderer::new);
        BlockEntityRenderers.register(Main.LARGE_CONDUIT_BLOCK_ENTITY, ConduitBlockEntityRender::new);
        BlockEntityRenderers.register(Main.SMALL_CONDUIT_BLOCK_ENTITY, ConduitBlockEntityRender::new);
        BlockEntityRenderers.register(Main.MEDIUM_CONDUIT_BLOCK_ENTITY, ConduitBlockEntityRender::new);


//        BlockRenderLayerMap.putBlock(AIO_Network.ENTRY, BlockRenderLayer.CUTOUT);
//        BlockRenderLayerMap.putBlock(ServerRack.ENTRY, BlockRenderLayer.CUTOUT);
//        BlockRenderLayerMap.putBlock(SatelliteDishBlock.ENTRY, BlockRenderLayer.CUTOUT);
//        BlockRenderLayerMap.putBlock(CoaxCable.ENTRY, BlockRenderLayer.CUTOUT);
//        BlockRenderLayerMap.putBlock(SwitchSimple.ENTRY, BlockRenderLayer.CUTOUT);
    }
}
