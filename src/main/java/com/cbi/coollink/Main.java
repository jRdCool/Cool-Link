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
import com.cbi.coollink.cli.lowlevelnet.ReceivePacketCommand;
import com.cbi.coollink.cli.lowlevelnet.SendPackAndWaitCommand;
import com.cbi.coollink.cli.lowlevelnet.SendPackCommand;
import com.cbi.coollink.cli.netutils.IfConfigProgram;
import com.cbi.coollink.cli.netutils.PingProgram;
import com.cbi.coollink.cli.repo.CliCommandPackage;
import com.cbi.coollink.cli.repo.CliPackageRepository;
import com.cbi.coollink.items.*;
import com.cbi.coollink.net.*;
import com.cbi.coollink.rendering.IWireNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


public class Main implements ModInitializer {

    public static boolean NULL_PACKET_DESK_DEBUG = false;//set to true to log any time a wire traversal fails because the wire dead ends
    public static void blank(){}//This is to kill warnings for unused import of the main class when debug lines are commented out
    public static final Logger LOGGER = LoggerFactory.getLogger("cool-link");

    public static final String namespace = "cool-link";
    public static final String[] woodTypes = {"oak","spruce","birch","jungle","acacia","dark_oak","mangrove","cherry","bamboo","crimson","warped","pale_oak","frame"};

    public static final BooleanProperty ASSEMBLED_BOOLEAN_PROPERTY = BooleanProperty.create("assembled");

    //Block Entity Registry
    public static final BlockEntityType<AIOBlockEntity> AIO_BLOCK_ENTITY = registerBlockEntity(Identifier.fromNamespaceAndPath("cool-link", "aio_block_entity"), AIOBlockEntity::new, AIO_Network.ENTRY);
    public static final BlockEntityType<ConduitBlockEntity> SMALL_CONDUIT_BLOCK_ENTITY = registerBlockEntity(Identifier.fromNamespaceAndPath("cool-link", "small_conduit_block_entity"),  (pos, state) -> ConduitBlockEntity.of(pos,state,0), SmallConduit.ENTRY);
    public static final BlockEntityType<ConduitBlockEntity> MEDIUM_CONDUIT_BLOCK_ENTITY = registerBlockEntity(Identifier.fromNamespaceAndPath("cool-link", "medium_conduit_block_entity"), (pos, state) -> ConduitBlockEntity.of(pos,state,1), MediumConduit.ENTRY);
    public static final BlockEntityType<ConduitBlockEntity> LARGE_CONDUIT_BLOCK_ENTITY = registerBlockEntity(Identifier.fromNamespaceAndPath("cool-link", "large_conduit_block_entity"), (pos, state) -> ConduitBlockEntity.of(pos,state,2), LargeConduit.ENTRY);
    public static final BlockEntityType<ServerRackBlockEntity> SERVER_RACK_BLOCK_ENTITY = registerBlockEntity(Identifier.fromNamespaceAndPath(namespace,"server_rack_block_entity"), ServerRackBlockEntity::new,ServerRack.ENTRY);
    public static final BlockEntityType<SwitchSimpleBE> SWITCH_SIMPLE_BLOCK_ENTITY = registerBlockEntity(Identifier.fromNamespaceAndPath("cool-link", "switch_simple_be"), SwitchSimpleBE::new, SwitchSimple.ENTRY);
    public static final BlockEntityType<SatelliteDishBlockEntity> SATELLITE_DISH_BLOCK_ENTITY = registerBlockEntity(Identifier.fromNamespaceAndPath(namespace,"satellite-dish-block-entity"),SatelliteDishBlockEntity::new,SatelliteDishBlock.ENTRY);
    public static final BlockEntityType<RedstoneControllerWiredBE> RS_SENDER_WIRED_BLOCK_ENTITY = registerBlockEntity(Identifier.fromNamespaceAndPath(namespace,"rs_sender_wired_block_entity"),(pos, state) -> RedstoneControllerWiredBE.of(pos,state,0),RSSenderWired.ENTRY);
    public static final BlockEntityType<RedstoneControllerWiredBE> RS_RECEIVER_WIRED_BLOCK_ENTITY = registerBlockEntity(Identifier.fromNamespaceAndPath(namespace,"rs_receiver_wired_block_entity"),(pos, state) -> RedstoneControllerWiredBE.of(pos,state,1),RSReceiverWired.ENTRY);
    //public static final BlockEntityType<CoaxWallPortSingleBE> COAX_WALL_PORT_SINGLE_BLOCK_ENTITY =


    //Cable Registries
    public static final Cat6Cable cat6CableEntry = Registry.register(BuiltInRegistries.ITEM, Cat6Cable.ITEM_KEY, new Cat6Cable(new Item.Properties()/*.registryKey(Cat6Cable.ITEM_KEY)*/));
    public static final CoaxialCable coaxialCableEntry = Registry.register(BuiltInRegistries.ITEM, CoaxialCable.ITEM_KEY,new CoaxialCable(new Item.Properties()/*.registryKey(CoaxialCable.ITEM_KEY)*/));


    //creative mode tab group
    public static final CreativeModeTab COOL_LINK_ITEM_GROUP = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(AIO_Network.ENTRY))
            .displayName(Component.literal("Cool Link"))
            .build();

    //wire connection info item component
    public static final DataComponentType<WireInfoComponent> WIRE_INFO_COMPONENT = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(namespace, "custom"),
            DataComponentType.<WireInfoComponent>builder().persistent(WireInfoComponent.CODEC).networkSynchronized(null).build()
            //TODO make a packet code for this
            // persistent is the save file NBT codec
            // network synchronized is the packet codec
    );

    private static final ResourceKey<CreativeModeTab> ITEM_GROUP = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath("cool-link", "cool-link"));

    static HashMap<String, CoaxWallPortSingle> coaxWallPortVarients = new HashMap<>();
    public static HashMap<String, BlockEntityType<CoaxWallPortSingleBE>> coaxWallPortSingleBlockEntities = new HashMap<>();
    @Override
    public void onInitialize() {
        LOGGER.info("loading cool link");

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEM_GROUP, COOL_LINK_ITEM_GROUP);//post 1.20 changes to item groups


        //test block
        Registry.register(BuiltInRegistries.BLOCK, TestBlock.BLOCK_KEY,TestBlock.ENTRY);
        Registry.register(BuiltInRegistries.ITEM, TestBlock.ITEM_KEY,new BlockItem(TestBlock.ENTRY, new Item.Properties()/*.registryKey(TestBlock.ITEM_KEY)*/));

        //aio network
        Registry.register(BuiltInRegistries.BLOCK, AIO_Network.BLOCK_KEY, AIO_Network.ENTRY);

        Registry.register(BuiltInRegistries.ITEM, AIO_Network.ITEM_KEY,new BlockItem(AIO_Network.ENTRY, new Item.Properties()/*.registryKey(AIO_Network.ITEM_KEY)*/));

        //smartphone
        SmartPhone smartPhoneEntry = Registry.register(BuiltInRegistries.ITEM, SmartPhone.ITEM_KEY,new SmartPhone(new Item.Properties()/*.registryKey(SmartPhone.ITEM_KEY)*/));

        //redstone controllers wired
        Registry.register(BuiltInRegistries.BLOCK, RSReceiverWired.BLOCK_KEY, RSReceiverWired.ENTRY);
        //BlockRenderLayerMap.putBlock(RSReceiverWired.ENTRY, BlockRenderLayer.CUTOUT);
        Registry.register(BuiltInRegistries.ITEM, RSReceiverWired.ITEM_KEY,new BlockItem(RSReceiverWired.ENTRY, new Item.Properties()/*.registryKey(RSReceiverWired.ITEM_KEY)*/));
        Registry.register(BuiltInRegistries.BLOCK, RSSenderWired.BLOCK_KEY, RSSenderWired.ENTRY);
        //BlockRenderLayerMap.putBlock(RSReceiverWired.ENTRY, BlockRenderLayer.CUTOUT);
        Registry.register(BuiltInRegistries.ITEM, RSSenderWired.ITEM_KEY,new BlockItem(RSSenderWired.ENTRY, new Item.Properties()/*.registryKey(RSSenderWired.ITEM_KEY)*/));

        //wire tester
        WireTester wireTesterEntry = Registry.register(BuiltInRegistries.ITEM, WireTester.ITEM_KEY,new WireTester(new Item.Properties()/*.registryKey(WireTester.ITEM_KEY)*/));
        ProgramingCable programingCableEntry = Registry.register(BuiltInRegistries.ITEM, ProgramingCable.ITEM_KEY,new ProgramingCable(new Item.Properties()/*.registryKey(ProgramingCable.ITEM_KEY)*/));

        //server Rack
        Registry.register(BuiltInRegistries.BLOCK, ServerRack.BLOCK_KEY, ServerRack.ENTRY);

        Registry.register(BuiltInRegistries.ITEM, ServerRack.ITEM_KEY,new BlockItem(ServerRack.ENTRY, new Item.Properties()/*.registryKey(ServerRack.ITEM_KEY)*/ ));

        //satellite dish
        Registry.register(BuiltInRegistries.BLOCK, SatelliteDishBlock.BLOCK_KEY, SatelliteDishBlock.ENTRY);

        Registry.register(BuiltInRegistries.ITEM, SatelliteDishBlock.ITEM_KEY,new BlockItem(SatelliteDishBlock.ENTRY, new Item.Properties()/*.registryKey(SatelliteDishBlock.ITEM_KEY)*/));

        //small conduit
        Registry.register(BuiltInRegistries.BLOCK, SmallConduit.BLOCK_KEY,SmallConduit.ENTRY);
        Registry.register(BuiltInRegistries.ITEM, SmallConduit.ITEM_KEY,new BlockItem(SmallConduit.ENTRY, new Item.Properties()/*.registryKey(SmallConduit.ITEM_KEY)*/));

        //medium conduit
        Registry.register(BuiltInRegistries.BLOCK, MediumConduit.BLOCK_KEY, MediumConduit.ENTRY);
        Registry.register(BuiltInRegistries.ITEM, MediumConduit.ITEM_KEY,new BlockItem(MediumConduit.ENTRY, new Item.Properties()/*.registryKey(MediumConduit.ITEM_KEY)*/));

        //large conduit
        Registry.register(BuiltInRegistries.BLOCK, LargeConduit.BLOCK_KEY, LargeConduit.ENTRY);
        Registry.register(BuiltInRegistries.ITEM, LargeConduit.ITEM_KEY,new BlockItem(LargeConduit.ENTRY, new Item.Properties()/*.registryKey(LargeConduit.ITEM_KEY)*/));

        //coax cable
        Registry.register(BuiltInRegistries.BLOCK, CoaxCable.BLOCK_KEY, CoaxCable.ENTRY);


        //Switch(Simple)
        Registry.register(BuiltInRegistries.BLOCK, SwitchSimple.BLOCK_KEY, SwitchSimple.ENTRY);

        Registry.register(BuiltInRegistries.ITEM, SwitchSimple.ITEM_KEY,new BlockItem(SwitchSimple.ENTRY, new Item.Properties()/*.registryKey(SwitchSimple.ITEM_KEY)*/));

        //legacy Items
        Registry.register(BuiltInRegistries.BLOCK, AIOCableBundle.BLOCK_KEY, AIOCableBundle.ENTRY);
        Registry.register(BuiltInRegistries.ITEM,AIOCableBundle.ITEM_KEY,new BlockItem(AIOCableBundle.ENTRY,new Item.Properties()/*.registryKey(AIOCableBundle.ITEM_KEY)*/));

        for(String wood:woodTypes) {
            //Main.LOGGER.info("Registering coax wall port of type: "+ wood);
            Identifier ID = Identifier.fromNamespaceAndPath(Main.namespace,"wall_ports/coax_wall_port_"+wood);
            ResourceKey<Block> BLOCK_KEY = Main.createBlockRegistryKey(ID);
            ResourceKey<Item> ITEM_KEY = Main.createItemRegistryKey(ID);
            CoaxWallPortSingle block = new CoaxWallPortSingle(BlockBehaviour.Properties.of().strength(0.5f)/*.registryKey(BLOCK_KEY)*/,wood);
            coaxWallPortVarients.put(wood,block);
            Registry.register(BuiltInRegistries.BLOCK, BLOCK_KEY, block);
            Registry.register(BuiltInRegistries.ITEM, ITEM_KEY, new BlockItem(block, new Item.Properties()/*.registryKey(ITEM_KEY)*/));
            coaxWallPortSingleBlockEntities.put(wood, registerBlockEntity(Identifier.fromNamespaceAndPath(namespace,"coax_wall_port_single_block_entity_"+wood), CoaxWallPortSingleBE.of(wood),block));
        }

        Registry.register(BuiltInRegistries.BLOCK,AIOWallPort.BLOCK_KEY,AIOWallPort.ENTRY);
        Registry.register(BuiltInRegistries.ITEM,AIOWallPort.ITEM_KEY,new BlockItem(AIOWallPort.ENTRY,new Item.Properties()/*.registryKey(AIOWallPort.ITEM_KEY)*/));

        //item registration
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(AIO_Network.ENTRY));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(SwitchSimple.ENTRY));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(ServerRack.ENTRY));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(RSSenderWired.ENTRY));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(RSReceiverWired.ENTRY));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(smartPhoneEntry));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(cat6CableEntry));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(coaxialCableEntry));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(wireTesterEntry));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(programingCableEntry));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(SatelliteDishBlock.ENTRY));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(SmallConduit.ENTRY));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(MediumConduit.ENTRY));
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(LargeConduit.ENTRY));
        //ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP).register(entries -> entries.add(AIOCableBundle.ENTRY));
        for(String wood:woodTypes) {
            CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(coaxWallPortVarients.get(wood)));
        }
        CreativeModeTabEvents.modifyOutputEvent(ITEM_GROUP).register(entries -> entries.accept(AIOWallPort.ENTRY));



        //register network packets
        PayloadTypeRegistry.clientboundPlay().register(OpenPhoneGuiPacket.ID,OpenPhoneGuiPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenConduitGuiPacket.ID,OpenConduitGuiPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(SavePhoneDataPacket.ID,SavePhoneDataPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(AioSetSSIDPacket.ID,AioSetSSIDPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(AioSetAdminPasswordPacket.ID,AioSetAdminPasswordPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(AioSetNetPasswordPacket.ID,AioSetNetPasswordPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AioSyncMacPacket.ID,AioSyncMacPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(OpenPortSelectGuiPacket.ID,OpenPortSelectGuiPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(WireInfoDataPacket.ID,WireInfoDataPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(UpdateConduitBlockCover.ID,UpdateConduitBlockCover.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(WIFIClientIpPacket.ID,WIFIClientIpPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(AccessPointLocationPacket.ID,AccessPointLocationPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(RequestAccessPointPositionsPacket.ID,RequestAccessPointPositionsPacket.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ConnectToWifiNetworkRequestPacket.ID,ConnectToWifiNetworkRequestPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ClientWifiConnectionResultPacket.ID,ClientWifiConnectionResultPacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(WIFIServerIpPacket.ID,WIFIServerIpPacket.CODEC);

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

            CompoundTag nbt=payload.nbt();
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

        ServerPlayNetworking.registerGlobalReceiver(UpdateConduitBlockCover.ID, (payload, context) -> {
            BlockPos pos = payload.position();
            RegistryKey<World> wrk=payload.world();
            BlockState ns = payload.state();
            //execute code on the server thread
            context.server().execute(() ->{
                BlockEntity be = context.server().getWorld(wrk).getBlockEntity(pos);
                if(be instanceof ConduitBlockEntity conduitBlockEntity){
                    if(ns.getRegistryEntry().getIdAsString().equals("minecraft:air")){
                        conduitBlockEntity.setCoverBlock(null);
                    }else{
                        conduitBlockEntity.setCoverBlock(ns);
                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(WIFIClientIpPacket.ID,(wifiClientIpPacket, context) -> {
            RegistryKey<World> wrk=wifiClientIpPacket.world();
            BlockPos apLocation = wifiClientIpPacket.accessPointPosition();
            World world = context.server().getWorld(wrk);
            if(world == null){
                LOGGER.error("Wold was null");
                return;
            }
            context.server().execute(()->{
                BlockEntity be = world.getBlockEntity(apLocation);
                if(be instanceof AccessPoint ap){
                    ap.processIncomingWifiPacket(wifiClientIpPacket,context.player());
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(RequestAccessPointPositionsPacket.ID,(wifiClientIpPacket, context) -> {
            RegistryKey<World> wrk=wifiClientIpPacket.world();
            BlockPos apLocation = wifiClientIpPacket.accessPointPosition();
            World world = context.server().getWorld(wrk);
            if(world == null){
                LOGGER.error("Wold was null");
                return;
            }
            context.server().execute(()->{
                BlockEntity be = world.getBlockEntity(apLocation);
                if(be instanceof AccessPoint ap){
                    ap.getNetworkAccessPointLocations(context.player());
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ConnectToWifiNetworkRequestPacket.ID, (connectToWifiNetworkRequestPacket, context) -> {
            RegistryKey<World> wrk=connectToWifiNetworkRequestPacket.world();
            BlockPos apLocation = connectToWifiNetworkRequestPacket.accessPointPosition();
            World world = context.server().getWorld(wrk);
            if(world == null){
                LOGGER.error("Wold was null");
                return;
            }
            context.server().execute(()->{
                BlockEntity be = world.getBlockEntity(apLocation);
                if(be instanceof AccessPoint ap){
                    ap.handleClientWifiConnectionRequest(connectToWifiNetworkRequestPacket.password(),connectToWifiNetworkRequestPacket.deviceMacAddress(),context.player(),connectToWifiNetworkRequestPacket.deviceName());
                }
            });
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, commandRegistryAccess, registrationEnvironment) -> dispatcher.register(Commands.literal("getIp")
                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                        .executes(commandContext -> {
                            BlockPos pos = BlockPosArgument.getBlockPos(commandContext,"pos");
                            BlockEntity block = commandContext.getSource().getLevel().getBlockEntity(pos);
                            if(block == null){
                                commandContext.getSource().sendFailure(Component.literal("That block is not a block entity"));
                                return 1;
                            }
                            if(block instanceof NetworkDevice netDevice){
                                //Text.of("This block's ip address is: "+netDevice.getIpAddress()
                                MutableComponent ipText = MutableComponent.create(new PlainTextContents.LiteralContents(netDevice.getIpAddress())).withColor(0x00FF00);
                                ipText.setStyle(ipText.getStyle().withClickEvent(new ClickEvent.CopyToClipboard(netDevice.getIpAddress()))
                                        .withHoverEvent(new HoverEvent.ShowText(Component.literal("Click to Copy"))));
                                commandContext.getSource().sendFailure(MutableComponent.create(new PlainTextContents.LiteralContents("This block's ip address is: ")).append(ipText));
                                return 0;
                            }else {
                                commandContext.getSource().sendFailure(Component.literal("This block is not a network device with an ip"));
                                return 1;
                            }
                        })
                )));



        //Register Cli program packages
        CliPackageRepository.registerPackage(new CliCommandPackage(
                    Identifier.fromNamespaceAndPath(namespace,"example"),
                    "Examples of how the cli program system can be used with external packages",
                    new CliCommandPackage.CommandInfo("helloworld",CliProgramInit.of(HelloWorld::new,"Simple hello world program")),
                    new CliCommandPackage.CommandInfo("load",CliProgramInit.of(Loading::new,"Loading example of how a command can execute over time"))
                ),
            CliPackageRepository.ANY_ENVIRONMENT);//end of example package
        CliPackageRepository.registerPackage(new CliCommandPackage(
                    Identifier.fromNamespaceAndPath(namespace,"low-level-net"),
                    "A collection of low level network utilities",
                    new CliCommandPackage.CommandInfo("sendpack",CliProgramInit.of(SendPackCommand::new,"Usage: sendpack <ip> <nbtdata>\nManually craft and send a packet over the network to the desired ipaddress. Spaces can be present in the raw nbt data. Does not wait for a response from the target device")),
                    new CliCommandPackage.CommandInfo("revpack", CliProgramInit.of(ReceivePacketCommand::new,"Waits for this device to receive a packet then prints its content. If the argument \"nolimit\" is present then the program will continue to run after a packet has been received.")),
                    new CliCommandPackage.CommandInfo("sendpacknwait", CliProgramInit.of(SendPackAndWaitCommand::new,"Usage: sendpack <ip> <nbtdata>\nManually craft and send a packet over the network to the desired ipaddress. Then wait for a response from the other device. Spaces can be present in the raw nbt data."))
                ),
            CliPackageRepository.ANY_ENVIRONMENT);
        CliPackageRepository.registerPackage(new CliCommandPackage(
                    Identifier.fromNamespaceAndPath(namespace,"net-utils"),
                    "Common network utility programs",
                    new CliCommandPackage.CommandInfo("ifconfig",CliProgramInit.of(IfConfigProgram::new,"Prints ip information")),
                    new CliCommandPackage.CommandInfo("ping", CliProgramInit.of(PingProgram::new,"Usage: ping <ip>\nSends a ping packet to the provided ip address and waits for a response"))
                ),
            CliPackageRepository.ANY_ENVIRONMENT);

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
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
    }

    public static ResourceKey<Block> createBlockRegistryKey(Identifier id) {
        return ResourceKey.create(Registries.BLOCK, id);
    }

    public static ResourceKey<Item> createItemRegistryKey(Identifier id) {
        return ResourceKey.create(Registries.ITEM, id);
    }
}