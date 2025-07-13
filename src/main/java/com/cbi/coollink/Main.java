package com.cbi.coollink;

import com.cbi.coollink.blocks.*;
import com.cbi.coollink.blocks.blockentities.*;
import com.cbi.coollink.blocks.blockentities.wallports.*;
import com.cbi.coollink.blocks.cables.*;
import com.cbi.coollink.blocks.conduits.*;
import com.cbi.coollink.blocks.networkdevices.*;
import com.cbi.coollink.blocks.wallports.*;
import com.cbi.coollink.cli.CliProgramInit;
import com.cbi.coollink.cli.example.HelloWorld;
import com.cbi.coollink.cli.example.Loading;
import com.cbi.coollink.cli.repo.CliCommandPackage;
import com.cbi.coollink.cli.repo.CliPackageRepository;
import com.cbi.coollink.items.*;
import com.cbi.coollink.net.*;
import com.cbi.coollink.rendering.IWireNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ConduitBlockEntity;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.OverlayMessageS2CPacket;
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
    public static final String[] woodTypes = {"oak","spruce","birch","jungle","acacia","dark_oak","mangrove","cherry","bamboo","crimson","warped","pale_oak"};

    public static final BooleanProperty ASSEMBLED_BOOLEAN_PROPERTY = BooleanProperty.of("assembled");

    //Block Entity Registry
    public static final BlockEntityType<AIOBlockEntity> AIO_BLOCK_ENTITY = registerBlockEntity(Identifier.of("cool-link", "aio_block_entity"), AIOBlockEntity::new, AIO_Network.ENTRY);
    public static final BlockEntityType<ConduitBlockEntity> SMALL_CONDUIT_BLOCK_ENTITY = registerBlockEntity(Identifier.of("cool-link", "small_conduit_block_entity"), ConduitBlockEntity::new, SmallConduit.ENTRY);
    public static final BlockEntityType<ConduitBlockEntity> MEDIUM_CONDUIT_BLOCK_ENTITY = registerBlockEntity(Identifier.of("cool-link", "medium_conduit_block_entity"), ConduitBlockEntity::new, MediumConduit.ENTRY);
    public static final BlockEntityType<ConduitBlockEntity> LARGE_CONDUIT_BLOCK_ENTITY = registerBlockEntity(Identifier.of("cool-link", "large_conduit_block_entity"), ConduitBlockEntity::new, LargeConduit.ENTRY);
    public static final BlockEntityType<ServerRackBlockEntity> SERVER_RACK_BLOCK_ENTITY = registerBlockEntity(Identifier.of(namespace,"server_rack_block_entity"), ServerRackBlockEntity::new,ServerRack.ENTRY);
    public static final BlockEntityType<SwitchSimpleBE> SWITCH_SIMPLE_BLOCK_ENTITY = registerBlockEntity(Identifier.of("cool-link", "switch_simple_be"), SwitchSimpleBE::new, SwitchSimple.ENTRY);
    public static final BlockEntityType<SatelliteDishBlockEntity> SATELLITE_DISH_BLOCK_ENTITY = registerBlockEntity(Identifier.of(namespace,"satellite-dish-block-entity"),SatelliteDishBlockEntity::new,SatelliteDishBlock.ENTRY);
    //public static final BlockEntityType<CoaxWallPortSingleBE> COAX_WALL_PORT_SINGLE_BLOCK_ENTITY =



    //Cable Registries
    public static final Cat6Cable cat6CableEntry = Registry.register(Registries.ITEM, Cat6Cable.ITEM_KEY, new Cat6Cable(new Item.Settings().registryKey(Cat6Cable.ITEM_KEY)));
    public static final CoaxialCable coaxialCableEntry = Registry.register(Registries.ITEM, CoaxialCable.ITEM_KEY,new CoaxialCable(new Item.Settings().registryKey(CoaxialCable.ITEM_KEY)));

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

    static HashMap<String, CoaxWallPortSingle> coaxWallPortVarients = new HashMap<>();
    public static HashMap<String, BlockEntityType<CoaxWallPortSingleBE>> coaxWallPortSingleBlockEntities = new HashMap<>();
    @Override
    public void onInitialize() {
        LOGGER.info("loading cool link");

        Registry.register(Registries.ITEM_GROUP, ITEM_GROUP, COOL_LINK_ITEM_GROUP);//post 1.20 changes to item groups


        //test block
        Registry.register(Registries.BLOCK, TestBlock.BLOCK_KEY,TestBlock.ENTRY);
        Registry.register(Registries.ITEM, TestBlock.ITEM_KEY,new BlockItem(TestBlock.ENTRY, new Item.Settings().registryKey(TestBlock.ITEM_KEY)));

        //aio network
        Registry.register(Registries.BLOCK, AIO_Network.BLOCK_KEY, AIO_Network.ENTRY);
        BlockRenderLayerMap.putBlock(AIO_Network.ENTRY, BlockRenderLayer.CUTOUT);
        Registry.register(Registries.ITEM, AIO_Network.ITEM_KEY,new BlockItem(AIO_Network.ENTRY, new Item.Settings().registryKey(AIO_Network.ITEM_KEY)));

        //smartphone
        SmartPhone smartPhoneEntry = Registry.register(Registries.ITEM, SmartPhone.ITEM_KEY,new SmartPhone(new Item.Settings().registryKey(SmartPhone.ITEM_KEY)));

        //redstone controller wired
        Registry.register(Registries.BLOCK, RedstoneControllerWired.BLOCK_KEY, RedstoneControllerWired.ENTRY);
        //BlockRenderLayerMap.putBlock(RedstoneControllerWired.ENTRY, BlockRenderLayer.CUTOUT);
        Registry.register(Registries.ITEM, RedstoneControllerWired.ITEM_KEY,new BlockItem(RedstoneControllerWired.ENTRY, new Item.Settings().registryKey(RedstoneControllerWired.ITEM_KEY)));

        //wire tester
        WireTester wireTesterEntry = Registry.register(Registries.ITEM, WireTester.ITEM_KEY,new WireTester(new Item.Settings().registryKey(WireTester.ITEM_KEY)));
        ProgramingCable programingCableEntry = Registry.register(Registries.ITEM, ProgramingCable.ITEM_KEY,new ProgramingCable(new Item.Settings().registryKey(ProgramingCable.ITEM_KEY)));

        //server Rack
        Registry.register(Registries.BLOCK, ServerRack.BLOCK_KEY, ServerRack.ENTRY);
        BlockRenderLayerMap.putBlock(ServerRack.ENTRY, BlockRenderLayer.CUTOUT);
        Registry.register(Registries.ITEM, ServerRack.ITEM_KEY,new BlockItem(ServerRack.ENTRY, new Item.Settings().registryKey(ServerRack.ITEM_KEY) ));

        //satellite dish
        Registry.register(Registries.BLOCK, SatelliteDishBlock.BLOCK_KEY, SatelliteDishBlock.ENTRY);
        BlockRenderLayerMap.putBlock(SatelliteDishBlock.ENTRY, BlockRenderLayer.CUTOUT);
        Registry.register(Registries.ITEM, SatelliteDishBlock.ITEM_KEY,new BlockItem(SatelliteDishBlock.ENTRY, new Item.Settings().registryKey(SatelliteDishBlock.ITEM_KEY)));

        //small conduit
        Registry.register(Registries.BLOCK, SmallConduit.BLOCK_KEY,SmallConduit.ENTRY);
        Registry.register(Registries.ITEM, SmallConduit.ITEM_KEY,new BlockItem(SmallConduit.ENTRY, new Item.Settings().registryKey(SmallConduit.ITEM_KEY)));

        //medium conduit
        Registry.register(Registries.BLOCK, MediumConduit.BLOCK_KEY, MediumConduit.ENTRY);
        Registry.register(Registries.ITEM, MediumConduit.ITEM_KEY,new BlockItem(MediumConduit.ENTRY, new Item.Settings().registryKey(MediumConduit.ITEM_KEY)));

        //large conduit
        Registry.register(Registries.BLOCK, LargeConduit.BLOCK_KEY, LargeConduit.ENTRY);
        Registry.register(Registries.ITEM, LargeConduit.ITEM_KEY,new BlockItem(LargeConduit.ENTRY, new Item.Settings().registryKey(LargeConduit.ITEM_KEY)));

        //coax cable
        Registry.register(Registries.BLOCK, CoaxCable.BLOCK_KEY, CoaxCable.ENTRY);
        BlockRenderLayerMap.putBlock(CoaxCable.ENTRY, BlockRenderLayer.CUTOUT);

        //Switch(Simple)
        Registry.register(Registries.BLOCK, SwitchSimple.BLOCK_KEY, SwitchSimple.ENTRY);
        BlockRenderLayerMap.putBlock(SwitchSimple.ENTRY, BlockRenderLayer.CUTOUT);
        Registry.register(Registries.ITEM, SwitchSimple.ITEM_KEY,new BlockItem(SwitchSimple.ENTRY, new Item.Settings().registryKey(SwitchSimple.ITEM_KEY)));

        //legacy Items
        Registry.register(Registries.BLOCK, AIOCableBundle.BLOCK_KEY, AIOCableBundle.ENTRY);
        Registry.register(Registries.ITEM,AIOCableBundle.ITEM_KEY,new BlockItem(AIOCableBundle.ENTRY,new Item.Settings().registryKey(AIOCableBundle.ITEM_KEY)));

        for(String wood:woodTypes) {
            //Main.LOGGER.info("Registering coax wall port of type: "+ wood);
            Identifier ID = Identifier.of(Main.namespace,"wall_ports/coax_wall_port_"+wood);
            RegistryKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
            RegistryKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
            CoaxWallPortSingle block = new CoaxWallPortSingle(AbstractBlock.Settings.create().hardness(0.5f).registryKey(BLOCK_KEY),wood);
            coaxWallPortVarients.put(wood,block);
            Registry.register(Registries.BLOCK, BLOCK_KEY, block);
            Registry.register(Registries.ITEM, ITEM_KEY, new BlockItem(block, new Item.Settings().registryKey(ITEM_KEY)));
            coaxWallPortSingleBlockEntities.put(wood, registerBlockEntity(Identifier.of(namespace,"coax_wall_port_single_block_entity_"+wood), CoaxWallPortSingleBE.of(wood),block));
        }

        Registry.register(Registries.BLOCK,AIOWallPort.BLOCK_KEY,AIOWallPort.ENTRY);
        Registry.register(Registries.ITEM,AIOWallPort.ITEM_KEY,new BlockItem(AIOWallPort.ENTRY,new Item.Settings().registryKey(AIOWallPort.ITEM_KEY)));

        //item registration
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(AIO_Network.ENTRY));
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(SwitchSimple.ENTRY));
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
        PayloadTypeRegistry.playC2S().register(WireInfoDataPacket.ID,WireInfoDataPacket.CODEC);

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
                //they removed the get both hands at the same time method so saving them both into a tmp array is a good enough for now solution
                for (ItemStack itemStack : new ItemStack[]{context.player().getMainHandStack(),context.player().getOffHandStack()}) {
                    if (itemStack.getItem().equals(itemFromClient.getItem())) {
                        heldItem = itemStack;
                        break;
                    }
                }
                if(heldItem!=null)
                  heldItem.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));


            });
        });

        ServerPlayNetworking.registerGlobalReceiver(WireInfoDataPacket.ID ,(payload, context) -> {
            //check if the item has the component on it already
            ItemStack itemFromClient = payload.heldItem();
            World world = context.player().getWorld();
            if(world == null){
                return;
            }
            context.server().execute(() -> {
                ItemStack heldItem = null;
                //they removed the get both hands at the same time method so saving them both into a tmp array is a good enough for now solution
                for (ItemStack itemStack : new ItemStack[]{context.player().getMainHandStack(),context.player().getOffHandStack()}) {
                    if (itemStack.getItem().equals(itemFromClient.getItem())) {
                        heldItem = itemStack;
                        break;
                    }
                }
                if (heldItem == null) {
                    LOGGER.error("Player attempted to create wire info but were not holding the item they started with");
                    return;
                }
                if(heldItem.contains(WIRE_INFO_COMPONENT)){
                    if(payload.clear()){
                        heldItem.remove(WIRE_INFO_COMPONENT);
                        return;
                    }
                    //finishing the wire
                    WireInfoComponent startingWireInfo = heldItem.get(WIRE_INFO_COMPONENT);
                    BlockEntity block2 = world.getBlockEntity(payload.originBlock());
                    if(block2 == null){
                        heldItem.remove(WIRE_INFO_COMPONENT);
                        return;
                    }
                    if(startingWireInfo != null && world.getBlockEntity(startingWireInfo.originBlock()) instanceof IWireNode block){
                        block.setNode(startingWireInfo.index(),payload.index(),payload.originBlock(),payload.wireType());

                        ((IWireNode) block2).setNode(payload.index(), startingWireInfo.index(),startingWireInfo.originBlock(),payload.wireType());
                    }
                    heldItem.remove(WIRE_INFO_COMPONENT);
                    context.player().swingHand(context.player().getActiveHand(),true);
                }else {
                    //starting the wire
                    heldItem.set(WIRE_INFO_COMPONENT, new WireInfoComponent(payload.index(), payload.originBlock()));
                    context.player().swingHand(context.player().getActiveHand(), true);
                    BlockPos origin = payload.originBlock();
                    context.player().networkHandler.sendPacket(new OverlayMessageS2CPacket(Text.of("Starting " +payload.wireType().toString() + " Connection at: " + origin.getX() + ", " + origin.getY() + ", " + origin.getZ())));
                }
            });

        });


        //Register Cli program packages
        CliPackageRepository.registerPackage(new CliCommandPackage(
                    Identifier.of(namespace,"example"),
                    "Examples of how the cli program system can be used with external packages",
                    new CliCommandPackage.CommandInfo("helloworld",CliProgramInit.of(HelloWorld::new,"Simple hello world program")),
                    new CliCommandPackage.CommandInfo("load",CliProgramInit.of(Loading::new,"Loading example of how a command can execute over time"))
                ),
            CliPackageRepository.ANY_ENVIRONMENT);//end of example package

    }

    public static boolean macDNE(int[] mac){
        //todo
        return true;
    }

    public static void setKnownMacs(int[] mac){
        //todo
    }

    /**Register a new block entity to minecraft
     * @param id The id of the block entity
     * @param entityFactory The constructor for the block entity (MyBlockEntity::new)
     * @param blocks A reference to the block object this block entity will be attaching to
     * @return The registered block entity type
     * @param <T> The type of the block entity class
     */
    public static <T extends BlockEntity> BlockEntityType<T> registerBlockEntity(Identifier id, FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory, Block... blocks){
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static RegistryKey<Block> createBlockRegistryKey(Identifier id) {
        return RegistryKey.of(RegistryKeys.BLOCK, id);
    }

    public static RegistryKey<Item> createItemRegistryKey(Identifier id) {
        return RegistryKey.of(RegistryKeys.ITEM, id);
    }
}