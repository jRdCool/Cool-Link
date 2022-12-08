package com.cbi.coollink;

import com.cbi.coollink.blocks.AIOBlockEntity;
import com.cbi.coollink.blocks.AIO_Network;
import com.cbi.coollink.blocks.ServerRack;
import com.cbi.coollink.blocks.TestBlock;
import com.cbi.coollink.items.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Main implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("cool-link");
    public static final BlockEntityType<AIOBlockEntity> AIO_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier("cool-link", "aio_block_entity"), FabricBlockEntityTypeBuilder.create(AIOBlockEntity::new, AIO_Network.ENTRY).build());

    public static final ItemGroup COOL_LINK_ITEM_GROUP = FabricItemGroupBuilder.create(
                    new Identifier("cool-link", "cool-link"))
            .icon(() -> new ItemStack(AIO_Network.ENTRY))
            .build();
    @Override
    public void onInitialize() {
        LOGGER.info("loading cool link");



        Registry.register(Registry.BLOCK, new Identifier("cool-link","test_block"),TestBlock.ENTRY);
        Registry.register(Registry.ITEM, new Identifier("cool-link", "test_block"),new BlockItem(TestBlock.ENTRY, new FabricItemSettings()));
        Registry.register(Registry.BLOCK, new Identifier("cool-link","aio_network"), AIO_Network.ENTRY);
        BlockRenderLayerMap.INSTANCE.putBlock(AIO_Network.ENTRY, RenderLayer.getCutout());
        Registry.register(Registry.ITEM, new Identifier("cool-link", "aio_network"),new BlockItem(AIO_Network.ENTRY, new FabricItemSettings().group(COOL_LINK_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier("cool-link", "smart_phone"),new SmartPhone(new FabricItemSettings().group(COOL_LINK_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier("cool-link", "cat6_ethernet_cable"),new Cat6Cable(new FabricItemSettings().group(COOL_LINK_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier("cool-link", "coaxial_cable"),new CoaxialCable(new FabricItemSettings().group(COOL_LINK_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier("cool-link", "wire_tester"),new WireTester(new FabricItemSettings().group(COOL_LINK_ITEM_GROUP)));
        Registry.register(Registry.ITEM, new Identifier("cool-link", "programing_cable"),new ProgramingCable(new FabricItemSettings().group(COOL_LINK_ITEM_GROUP)));

        Registry.register(Registry.BLOCK, new Identifier("cool-link","server_rack"), ServerRack.ENTRY);
        BlockRenderLayerMap.INSTANCE.putBlock(AIO_Network.ENTRY, RenderLayer.getCutout());
        Registry.register(Registry.ITEM, new Identifier("cool-link", "server_rack"),new BlockItem(ServerRack.ENTRY, new FabricItemSettings().group(COOL_LINK_ITEM_GROUP)));

        //register a packet listener to listen for the aio-set-password packet
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("cool-link","aio-set-password"), (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String password=buf.readString();
            RegistryKey<World> wrk=buf.readRegistryKey(Registry.WORLD_KEY);
            //execute code on the server thread
            server.execute(() ->{
                BlockEntity be =server.getWorld(wrk).getBlockEntity(pos);

                if(be instanceof AIOBlockEntity aio){
                    aio.password=password;
                    aio.markDirty();
                    aio.updateStates();
                }
            });
        });
    }
}


/*UPDATING TO 1.19.3
make sure lib gui has updated
read https://fabricmc.net/2022/11/24/1193.html to be informed about api changes (there are a lot of important ones)
add missing gradle build files

 */