package com.cbi.coollink;

import com.cbi.coollink.blocks.*;
import com.cbi.coollink.blocks.blockentities.AIOBlockEntity;
import com.cbi.coollink.blocks.blockentities.ServerRackBlockEntity;
import com.cbi.coollink.blocks.cables.AIOCableBundle;
import com.cbi.coollink.blocks.cables.CoaxCable;
import com.cbi.coollink.blocks.conduits.LargeConduit;
import com.cbi.coollink.blocks.conduits.MediumConduit;
import com.cbi.coollink.blocks.conduits.SmallConduit;
import com.cbi.coollink.blocks.networkdevices.AIO_Network;
import com.cbi.coollink.blocks.networkdevices.SatelliteDishBlock;
import com.cbi.coollink.blocks.wallports.AIOWallPort;
import com.cbi.coollink.blocks.wallports.CoaxWallPort;
import com.cbi.coollink.items.*;
import com.cbi.coollink.net.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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

import java.util.HashMap;


public class Main implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("cool-link");

    public static final String namespace = "cool-link";
    public static final String[] woodTypes = {"oak","spruce","birch","jungle","acacia","dark_oak","mangrove","cherry","bamboo","crimson","warped"};

    //Block Entity Registry
    public static final BlockEntityType<AIOBlockEntity> AIO_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("cool-link", "aio_block_entity"), BlockEntityType.Builder.create(AIOBlockEntity::new, AIO_Network.ENTRY).build());
    public static final BlockEntityType<ConduitBlockEntity> SMALL_CONDUIT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("cool-link", "small_conduit_block_entity"), BlockEntityType.Builder.create(ConduitBlockEntity::new, SmallConduit.ENTRY).build());
    public static final BlockEntityType<ConduitBlockEntity> MEDIUM_CONDUIT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("cool-link", "medium_conduit_block_entity"), BlockEntityType.Builder.create(ConduitBlockEntity::new, MediumConduit.ENTRY).build());
    public static final BlockEntityType<ConduitBlockEntity> LARGE_CONDUIT_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of("cool-link", "large_conduit_block_entity"), BlockEntityType.Builder.create(ConduitBlockEntity::new, LargeConduit.ENTRY).build());
    public static final BlockEntityType<ServerRackBlockEntity> SERVER_RACK_BLOCK_ENTITY = Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(namespace,"server_rack_block_entity"), BlockEntityType.Builder.create(ServerRackBlockEntity::new,ServerRack.ENTRY).build());

    public static final BooleanProperty ASSEMBLED_BOOLEAN_PROPERTY = BooleanProperty.of("assembled");

    //Cable Registries
    public static final Cat6Cable cat6CableEntry = Registry.register(Registries.ITEM, Identifier.of("cool-link", "cat6_ethernet_cable"),new Cat6Cable(new Item.Settings()));
    public static final CoaxialCable coaxialCableEntry = Registry.register(Registries.ITEM, Identifier.of("cool-link", "coaxial_cable"),new CoaxialCable(new Item.Settings()));

    public static final ItemGroup COOL_LINK_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(AIO_Network.ENTRY))
            .displayName(Text.of("Cool Link"))
            .build();

    public static final ComponentType<WireInfoComponent> WIRE_INFO_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(namespace, "custom"),
            ComponentType.<WireInfoComponent>builder().codec(WireInfoComponent.CODEC).build()
    );

    private static final RegistryKey<ItemGroup> ITEM_GROUP = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of("cool-link", "cool-link"));

    static HashMap<String, CoaxWallPort> coaxWallPortVarients = new HashMap<>();
    @Override
    public void onInitialize() {
        LOGGER.info("loading cool link");

        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, COOL_LINK_ITEM_GROUP);//post 1.20 changes to item groups



        Registry.register(Registries.BLOCK, Identifier.of("cool-link","test_block"),TestBlock.ENTRY);
        Registry.register(Registries.ITEM, Identifier.of("cool-link", "test_block"),new BlockItem(TestBlock.ENTRY, new Item.Settings()));
        Registry.register(Registries.BLOCK, Identifier.of("cool-link","aio_network"), AIO_Network.ENTRY);
        BlockRenderLayerMap.INSTANCE.putBlock(AIO_Network.ENTRY, RenderLayer.getCutout());
        Registry.register(Registries.ITEM, Identifier.of("cool-link", "aio_network"),new BlockItem(AIO_Network.ENTRY, new Item.Settings()));
        SmartPhone smartPhoneEntry= Registry.register(Registries.ITEM, Identifier.of("cool-link", "smart_phone"),new SmartPhone(new Item.Settings()));


        WireTester wireTesterEntry = Registry.register(Registries.ITEM, Identifier.of("cool-link", "wire_tester"),new WireTester(new Item.Settings()));
        ProgramingCable programingCableEntry = Registry.register(Registries.ITEM, Identifier.of("cool-link", "programing_cable"),new ProgramingCable(new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("cool-link","server_rack"), ServerRack.ENTRY);
        BlockRenderLayerMap.INSTANCE.putBlock(ServerRack.ENTRY, RenderLayer.getCutout());
        Registry.register(Registries.ITEM, Identifier.of("cool-link", "server_rack"),new BlockItem(ServerRack.ENTRY, new Item.Settings() ));

        Registry.register(Registries.BLOCK, Identifier.of("cool-link","satellite_dish"), SatelliteDishBlock.ENTRY);
        BlockRenderLayerMap.INSTANCE.putBlock(SatelliteDishBlock.ENTRY, RenderLayer.getCutout());
        Registry.register(Registries.ITEM, Identifier.of("cool-link", "satellite_dish"),new BlockItem(SatelliteDishBlock.ENTRY, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("cool-link","small_conduit"),SmallConduit.ENTRY);
        Registry.register(Registries.ITEM, Identifier.of("cool-link", "small_conduit"),new BlockItem(SmallConduit.ENTRY, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("cool-link","medium_conduit"), MediumConduit.ENTRY);
        Registry.register(Registries.ITEM, Identifier.of("cool-link", "medium_conduit"),new BlockItem(MediumConduit.ENTRY, new Item.Settings()));

        Registry.register(Registries.BLOCK, Identifier.of("cool-link","large_conduit"), LargeConduit.ENTRY);
        Registry.register(Registries.ITEM, Identifier.of("cool-link", "large_conduit"),new BlockItem(LargeConduit.ENTRY, new Item.Settings()));

        Registry.register(Registries.BLOCK,Identifier.of("cool-link","coax_cable"), CoaxCable.ENTRY);
        BlockRenderLayerMap.INSTANCE.putBlock(CoaxCable.ENTRY, RenderLayer.getCutout());

        Registry.register(Registries.BLOCK, Identifier.of(namespace,"aio_cable_bundle"), AIOCableBundle.ENTRY);
        Registry.register(Registries.ITEM,Identifier.of(namespace,"aio_cable_bundle"),new BlockItem(AIOCableBundle.ENTRY,new Item.Settings()));

        for(String wood:woodTypes) {
            CoaxWallPort block = new CoaxWallPort(FabricBlockSettings.create().hardness(0.5f));
            coaxWallPortVarients.put(wood,block);
            Registry.register(Registries.BLOCK, Identifier.of("cool-link", "wall_ports/coax_wall_port_"+wood), block);
            Registry.register(Registries.ITEM, Identifier.of("cool-link", "wall_ports/coax_wall_port_"+wood), new BlockItem(block, new Item.Settings()));
        }

        Registry.register(Registries.BLOCK,Identifier.of("cool-link","aio_wall_port"),AIOWallPort.ENTRY);
        Registry.register(Registries.ITEM,Identifier.of("cool-link","aio_wall_port"),new BlockItem(AIOWallPort.ENTRY,new Item.Settings()));

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
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(AIOCableBundle.ENTRY));
        for(String wood:woodTypes) {
            ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(coaxWallPortVarients.get(wood)));
        }
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(AIOWallPort.ENTRY));


        //register network packets
        PayloadTypeRegistry.playS2C().register(OpenPhoneGuiPacket.ID,OpenPhoneGuiPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenConduitGuiPacket.ID,OpenConduitGuiPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(SavePhoneDataPacket.ID,SavePhoneDataPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AioSetSSIDPacket.ID,AioSetSSIDPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AioSetAdminPasswordPacket.ID,AioSetAdminPasswordPacket.CODEC);
        PayloadTypeRegistry.playC2S().register(AioSetNetPasswordPacket.ID,AioSetNetPasswordPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(AioSyncMacPacket.ID,AioSyncMacPacket.CODEC);
        PayloadTypeRegistry.playS2C().register(OpenPortSelectGuiPacket.ID,OpenPortSelectGuiPacket.CODEC);

        //register a packet listener to listen for the aio-set-password packet
        ServerPlayNetworking.registerGlobalReceiver(AioSetAdminPasswordPacket.ID, (payload, context) -> {
            BlockPos pos = payload.pos();
            String password=payload.newPassword();
            //execute code on the server thread
            RegistryKey<World> wrk=payload.world();
            context.server().execute(() ->{
                BlockEntity be =context.server().getWorld(wrk).getBlockEntity(pos);

                if(be instanceof AIOBlockEntity aio){
                    aio.password=password;
                    aio.markDirty();
                    aio.updateStates();
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(AioSetSSIDPacket.ID, (payload, context) -> {
            BlockPos pos = payload.pos();
            String ssid=payload.newName();
            RegistryKey<World> wrk=payload.world();
            //execute code on the server thread
            context.server().execute(() ->{
                BlockEntity be =context.server().getWorld(wrk).getBlockEntity(pos);

                if(be instanceof AIOBlockEntity aio){
                    aio.ssid=ssid;
                    aio.markDirty();
                    aio.updateStates();
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(AioSetNetPasswordPacket.ID, (payload, context) -> {
            BlockPos pos = payload.pos();
            String netPass=payload.newPassword();
            RegistryKey<World> wrk=payload.world();
            //execute code on the server thread
            context.server().execute(() ->{
                BlockEntity be = context.server().getWorld(wrk).getBlockEntity(pos);

                if(be instanceof AIOBlockEntity aio){
                    aio.netPass=netPass;
                    aio.markDirty();
                    aio.updateStates();
                }
            });
        });

        //receive incoming data from the phone and write it to the server version of the phone object
        ServerPlayNetworking.registerGlobalReceiver(SavePhoneDataPacket.ID,(payload, context) -> {

            NbtCompound nbt=payload.nbt();
            ItemStack itemFromClient = payload.phone();
            context.server().execute(() -> {
                ItemStack heldItem = null;
                for (ItemStack itemStack : context.player().getHandItems()) {
                    if (itemStack.getItem().equals(itemFromClient.getItem())) {
                        heldItem = itemStack;
                        break;
                    }
                }
                if(heldItem!=null)
                  heldItem.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));


            });
        });


    }

    public static boolean macDNE(int[] mac){
        //todo
        return true;
    }

    public static void setKnownMacs(int[] mac){
        //todo
    }
}