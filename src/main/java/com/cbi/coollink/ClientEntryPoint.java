package com.cbi.coollink;

import com.cbi.coollink.app.AppRegistry;
import com.cbi.coollink.app.ExampleApp;
import com.cbi.coollink.app.SnakeGameApp;
import com.cbi.coollink.guis.ConduitGUI;
import com.cbi.coollink.guis.ConduitScreen;
import com.cbi.coollink.guis.PhoneGui;
import com.cbi.coollink.guis.PhoneScreen;
import com.cbi.coollink.net.OpenPhoneGuiPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public class ClientEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //register client things here
        AppRegistry.registerApp(new ExampleApp());
        AppRegistry.registerApp(new SnakeGameApp());

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("cool-link","open-phone-gui"),(client,handler,buf,responseSender) -> {
            OpenPhoneGuiPacket packet = new OpenPhoneGuiPacket(buf);
            RegistryKey<World> wrk = packet.world();
            World world = handler.getWorld();

            if(world==null){
                Main.LOGGER.error("Something has gon massively wrong client is not in a world");
                return;
            }
            if(!world.getRegistryKey().equals(wrk)){
                Main.LOGGER.error("Something has gon massively wrong client world does not match");
                return;
            }
            BlockPos blockEntityPos = packet.block();

            ItemStack heldItem = packet.heldItem();

            boolean noBLockEntity = packet.noBlockEntity();
            BlockEntity usedBlockEntity;
            if(!noBLockEntity)
                usedBlockEntity = Objects.requireNonNull(world).getBlockEntity(blockEntityPos);
            else {
                usedBlockEntity = null;
            }


            client.execute( () -> {
                client.setScreen(new PhoneScreen(new PhoneGui(world, usedBlockEntity, heldItem)));
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(new Identifier("cool-link", "open-conduit-gui"),(client,handler,buf,responseSender) -> {
            client.execute( ()->{
                client.setScreen(new ConduitScreen(new ConduitGUI()));
            });
        });
    }
}
