package com.cbi.coollink;

import com.cbi.coollink.app.*;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.guis.*;
import com.cbi.coollink.net.*;
import com.cbi.coollink.rendering.blockentities.SatelliteDishBlockEntityRenderer;
import com.cbi.coollink.rendering.blockentities.ServerRackBlockEntityRenderer;
import com.cbi.coollink.rendering.WireNodeRenderer;
import com.cbi.coollink.rendering.blockentities.ConduitBlockEntityRender;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.world.World;

public class ClientEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //register client things here
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
                    context.client().setScreen(new BasicScreen(new PortSelectGUI(payload.ofType(),payload.type(),clickedOnBlockEntity,payload.heldItem())));
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

        BlockEntityRendererFactories.register(Main.AIO_BLOCK_ENTITY,WireNodeRenderer::new);
        BlockEntityRendererFactories.register(Main.SWITCH_SIMPLE_BLOCK_ENTITY,WireNodeRenderer::new);
        BlockEntityRendererFactories.register(Main.SATELLITE_DISH_BLOCK_ENTITY, SatelliteDishBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(Main.LARGE_CONDUIT_BLOCK_ENTITY, ConduitBlockEntityRender::new);
        BlockEntityRendererFactories.register(Main.SMALL_CONDUIT_BLOCK_ENTITY, ConduitBlockEntityRender::new);
        BlockEntityRendererFactories.register(Main.MEDIUM_CONDUIT_BLOCK_ENTITY, ConduitBlockEntityRender::new);


    }
}
