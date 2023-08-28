package com.cbi.coollink;

import com.cbi.coollink.blocks.*;
import com.cbi.coollink.items.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class Main implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("cool-link");
    public static final BlockEntityType<AIOBlockEntity> AIO_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier("cool-link", "aio_block_entity"), FabricBlockEntityTypeBuilder.create(AIOBlockEntity::new, AIO_Network.ENTRY).build());

    public static final BooleanProperty ASSEMBLED_BOOLEAN_PROPERTY = BooleanProperty.of("assembled");

    public static final ItemGroup COOL_LINK_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(AIO_Network.ENTRY))
            .displayName(Text.of("Cool Link"))
            .build();


    private static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, new Identifier("cool-link", "cool-link"));
    @Override
    public void onInitialize() {
        LOGGER.info("loading cool link");

        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, COOL_LINK_ITEM_GROUP);//post 1.20 changes to item groups



        Registry.register(Registries.BLOCK, new Identifier("cool-link","test_block"),TestBlock.ENTRY);
        Registry.register(Registries.ITEM, new Identifier("cool-link", "test_block"),new BlockItem(TestBlock.ENTRY, new FabricItemSettings()));
        Registry.register(Registries.BLOCK, new Identifier("cool-link","aio_network"), AIO_Network.ENTRY);
        BlockRenderLayerMap.INSTANCE.putBlock(AIO_Network.ENTRY, RenderLayer.getCutout());
        Registry.register(Registries.ITEM, new Identifier("cool-link", "aio_network"),new BlockItem(AIO_Network.ENTRY, new FabricItemSettings()));
        SmartPhone smartPhoneEntry= Registry.register(Registries.ITEM, new Identifier("cool-link", "smart_phone"),new SmartPhone(new FabricItemSettings()));
        Cat6Cable cat6CableEntry = Registry.register(Registries.ITEM, new Identifier("cool-link", "cat6_ethernet_cable"),new Cat6Cable(new FabricItemSettings()));
        CoaxialCable coaxialCableEntry = Registry.register(Registries.ITEM, new Identifier("cool-link", "coaxial_cable"),new CoaxialCable(new FabricItemSettings()));
        WireTester wireTesterEntry = Registry.register(Registries.ITEM, new Identifier("cool-link", "wire_tester"),new WireTester(new FabricItemSettings()));
        ProgramingCable programingCableEntry = Registry.register(Registries.ITEM, new Identifier("cool-link", "programing_cable"),new ProgramingCable(new FabricItemSettings()));

        Registry.register(Registries.BLOCK, new Identifier("cool-link","server_rack"), ServerRack.ENTRY);
        BlockRenderLayerMap.INSTANCE.putBlock(ServerRack.ENTRY, RenderLayer.getCutout());
        Registry.register(Registries.ITEM, new Identifier("cool-link", "server_rack"),new BlockItem(ServerRack.ENTRY, new FabricItemSettings() ));

        Registry.register(Registries.BLOCK, new Identifier("cool-link","satellite_dish"), SatelliteDishBlock.ENTRY);
        BlockRenderLayerMap.INSTANCE.putBlock(SatelliteDishBlock.ENTRY, RenderLayer.getCutout());
        Registry.register(Registries.ITEM, new Identifier("cool-link", "satellite_dish"),new BlockItem(SatelliteDishBlock.ENTRY, new FabricItemSettings()));

        Registry.register(Registries.BLOCK, new Identifier("cool-link","small_conduit"),SmallConduit.ENTRY);
        Registry.register(Registries.ITEM, new Identifier("cool-link", "small_conduit"),new BlockItem(SmallConduit.ENTRY, new FabricItemSettings()));

        Registry.register(Registries.BLOCK, new Identifier("cool-link","medium_conduit"),MediumConduit.ENTRY);
        Registry.register(Registries.ITEM, new Identifier("cool-link", "medium_conduit"),new BlockItem(MediumConduit.ENTRY, new FabricItemSettings()));

        Registry.register(Registries.BLOCK, new Identifier("cool-link","large_conduit"),LargeConduit.ENTRY);
        Registry.register(Registries.ITEM, new Identifier("cool-link", "large_conduit"),new BlockItem(LargeConduit.ENTRY, new FabricItemSettings()));


        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(AIO_Network.ENTRY));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(ServerRack.ENTRY));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(smartPhoneEntry));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(cat6CableEntry));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(coaxialCableEntry));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(wireTesterEntry));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(programingCableEntry));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(SatelliteDishBlock.ENTRY));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(SmallConduit.ENTRY));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(MediumConduit.ENTRY));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(LargeConduit.ENTRY));



        //register a packet listener to listen for the aio-set-password packet
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("cool-link","aio-set-password"), (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String password=buf.readString();
            RegistryKey<World> wrk=buf.readRegistryKey(RegistryKeys.WORLD);
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

        ServerPlayNetworking.registerGlobalReceiver(new Identifier("cool-link","aio-set-ssid"), (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String ssid=buf.readString();
            RegistryKey<World> wrk=buf.readRegistryKey(RegistryKeys.WORLD);
            //execute code on the server thread
            server.execute(() ->{
                BlockEntity be =server.getWorld(wrk).getBlockEntity(pos);

                if(be instanceof AIOBlockEntity aio){
                    aio.ssid=ssid;
                    aio.markDirty();
                    aio.updateStates();
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(new Identifier("cool-link","aio-set-net-password"), (server, player, handler, buf, responseSender) -> {
            BlockPos pos = buf.readBlockPos();
            String netPass=buf.readString();
            RegistryKey<World> wrk=buf.readRegistryKey(RegistryKeys.WORLD);
            //execute code on the server thread
            server.execute(() ->{
                BlockEntity be =server.getWorld(wrk).getBlockEntity(pos);

                if(be instanceof AIOBlockEntity aio){
                    aio.netPass=netPass;
                    aio.markDirty();
                    aio.updateStates();
                }
            });
        });

        //receive incoming data from the phone and write it to the server version of the phone object
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("cool-link","save-phone-data"),(server, player, handler, buf, responseSender) -> {

            NbtCompound nbt=buf.readNbt();
            ItemStack itemFromClient = buf.readItemStack();

            server.execute(() -> {
                ItemStack heldItem = null;
                for (ItemStack itemStack : player.getHandItems()) {
                    if (itemStack.getItem().equals(itemFromClient.getItem())) {
                        heldItem = itemStack;
                        break;
                    }
                }
                if(heldItem!=null)
                  heldItem.setNbt(nbt);

            });
        });


    }
}