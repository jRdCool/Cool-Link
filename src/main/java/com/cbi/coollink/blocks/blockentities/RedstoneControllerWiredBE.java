package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.Util;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.blocks.networkdevices.NetworkDevice;
import com.cbi.coollink.blocks.networkdevices.RSReceiverWired;
import com.cbi.coollink.blocks.networkdevices.RSSenderWired;
import com.cbi.coollink.blocks.networkdevices.RedstoneControllerWired;
import com.cbi.coollink.net.protocol.IpDataPacket;
import com.cbi.coollink.net.protocol.Mac;
import com.cbi.coollink.net.protocol.WireDataPacket;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class RedstoneControllerWiredBE extends BlockEntity implements IWireNode, NetworkDevice {

    public RedstoneControllerWiredBE(BlockPos pos, BlockState state, BlockEntityType<?> type) {
        super(type, pos, state);
        this.localNodes = new LocalNode[getNodeCount()];
        mac = new Mac(deviceID);
        isSender = state.getBlock() instanceof RSSenderWired;
    }

    private static final int deviceID = 0x52;
    public Mac mac;
    private final LocalNode[] localNodes;

    private static final int nodeCount = 1;
    private final boolean[] isNodeUsed = new boolean[nodeCount];

    private String deviceIp = "127.0.0.1";

    private int networkPingCounter = 0;

    private Mac routerMac;

    private String onChangeIp = "";

    private final boolean isSender;

    private int previousPowerLevel = 0;

    public static RedstoneControllerWiredBE of( BlockPos pos, BlockState state,int type) {
        return switch (type){
            case 0 -> new RedstoneControllerWiredBE(pos, state, Main.RS_SENDER_WIRED_BLOCK_ENTITY);
            case 1 -> new RedstoneControllerWiredBE(pos, state, Main.RS_RECEIVER_WIRED_BLOCK_ENTITY);
            default -> null;
        };

    }

    @Override
    public @org.jspecify.annotations.Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /*
    @Override
    public CompoundTag toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }//Rendering Crucial
*/


    @Override
    public Vec3 getNodeOffset(int node) {
        double nodeXN = 0.5;
        double nodeZN = 0.5;
        double nodeY = 0.5;
        Vec3[] nodes = {
                new Vec3(nodeXN,nodeY,nodeZN),// - - - - - NORTH
                new Vec3(1-nodeZN,nodeY,nodeXN),// - -  EAST
                new Vec3(1-nodeXN,nodeY,1-nodeZN),// SOUTH
                new Vec3(nodeZN,nodeY,1-nodeXN) // - -  WEST
        };
        int dir = 0;
        switch (getBlockState().getValue(BlockStateProperties.FACING)){
            case EAST -> dir=1;
            case SOUTH -> dir=2;
            case WEST -> dir=3;
            default -> {}
        }

        //return nodes[dir];
        return new Vec3(nodeXN,nodeY,nodeZN);
    }//Getting the offset from the block origin for the nodes

    public void setMacAddress(int[] mac){
        this.mac = new Mac(mac);
    }//mac addresses new

    @Override
    public IWireNode getWireNode(int index) {
        return this;
    }

    @Override
    public int getOtherNodeIndex(int index) {
        return localNodes[index].getOtherIndex();
    }

    @Override
    public LocalNode getLocalNode(int index) {
        return localNodes[index];
    }

    @Override
    public void setNode(int index, int otherNode, BlockPos pos, WireType type) {
        this.localNodes[index] = new LocalNode(this, index, otherNode, type, pos);
        isNodeUsed[index]=true;
        setChanged();
        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
    }//Sets the node connection

    @Override
    public void removeNode(int index, boolean dropWire) {
        //LocalNode old = this.localNodes[index];
        this.localNodes[index] = null;
        setChanged();
        assert level != null;
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 0);
        this.setIsNodeUsed(index,false);
    }//Breaks the wire

    @Override
    public WireType getPortType(int index) {
        return WireType.CAT6;
    }

    @Override
    public boolean isNodeInUse(int index) {
        return isNodeUsed[index];
    }

    @Override
    public void setIsNodeUsed(int index, boolean set) {
        isNodeUsed[index]=set;
    }

    /**
     * Get the node of the destination device at the other end of the wire (not the next place the wire connects)
     *
     * @param connectionIndex The index of the start of the connection on this device
     * @return The {@link LocalNode} representing the other end of the wire, where index is the index of the node connection this wire terminates in on the receiving device
     */
    @Override
    public LocalNode getDestinationNode(int connectionIndex) {
        if(connectionIndex != 0){
            return null;
        }
        if(localNodes[connectionIndex] == null){
            return null;
        }
        LocalNode outputNode = IWireNode.traverseWire(localNodes[connectionIndex]);
        if(outputNode == null || outputNode.getType() != WireType.CAT6){
            if(Main.NULL_PACKET_DESK_DEBUG) {
                Main.LOGGER.error("Null destination or incorrect output wire type (from RedstoneControllerWiredBE)");
            }
            return null;
        }
        return outputNode;
    }

    /**
     * Send a packet of data to this device.
     * NOTE FOR IMPLEMENTATION: this method is your class receiving this data from another class, this is called from another class.
     * Mid wire blocks(wall ports, conduits, ect..) should throw a warning upon calling this method.
     * All other blocks should first check that the data packet is of the correct type (coax, ethernet, fiber ect..) then process the packet accordingly
     *
     * @param connectionIndex The index of the connection node on the destination device that is reviving the data
     * @param data            The data to send to the other device
     */
    @Override
    public void transmitData(int connectionIndex, WireDataPacket data) {
        //TODO auto ip mac cashing
        if(data instanceof IpDataPacket ipData) {
            String type = ipData.getData().getStringOr("type", "unknown");
            switch (type) {
                case "connected" -> {
                    deviceIp = Util.parseIpGetIp(ipData.getDestinationIpAddress());
                    routerMac = ipData.getSourceMacAddress();
                    //Main.LOGGER.info("Set device Ip to "+deviceIp);
                }
                case "setpower" -> {
                    if(isReceiver()){
                        sendPacket(generateNotSupportedPacket("set power",ipData));
                    }else {
                        int powerLevel = ipData.getData().getIntOr("power", 0);
                        powerLevel = Math.min(15, Math.max(0, powerLevel));
                        Level world = getLevel();
                        if (world != null) {
                            world.setBlock(getBlockPos(), getBlockState().setValue(RedstoneControllerWired.POWER, powerLevel),0);
                            //Main.LOGGER.info("RSS set power to: "+powerLevel);
                        }
                    }
                }
                case "getpower" -> {
                    if(isSender()){
                        sendPacket(generateNotSupportedPacket("get power",ipData));
                    } else {
                        int powerLevel = getBlockState().getValue(RSReceiverWired.RECEIVED_POWER);
                        CompoundTag response = new CompoundTag();
                        response.putString("type","powerlevel");
                        response.putInt("power",powerLevel);
                        sendPacket(ipData.createResponsePacket(response));
                    }
                }
                case "setOnPowerChangeIp" -> {
                    if(isSender()){
                        sendPacket(generateNotSupportedPacket("set power change ip",ipData));
                    } else {
                        onChangeIp = ipData.getData().getStringOr("ip","");
                        if(!onChangeIp.isEmpty()){
                            if(!Util.validIp(onChangeIp)){
                                onChangeIp = "";
                            }
                        }
                        setChanged();
                    }
                }
                case "ping" -> {
                    CompoundTag pongCompound = new CompoundTag();
                    pongCompound.putString("type","pong");
                    sendPacket(ipData.createResponsePacket(pongCompound));
                }
                //default -> Main.LOGGER.info("Received data RSS: "+data+" on port: "+connectionIndex+" at "+getPos());
            }
        }
    }

    public static void tick(Level world, BlockPos blockPos, BlockState blockState, RedstoneControllerWiredBE redstoneControllerWiredBE) {
        if(world.isClientSide()){
            return;
        }
        redstoneControllerWiredBE.networkPingCounter++;
        if(redstoneControllerWiredBE.networkPingCounter == 5*20){
            redstoneControllerWiredBE.networkPingCounter = 0;
            CompoundTag payload = new CompoundTag();
            payload.putString("type","connect");
            payload.putString("deviceName","Redstone "+(redstoneControllerWiredBE.isReceiver()?"Receiver":"Sender")+" Wired");
            if(redstoneControllerWiredBE.routerMac == null) {
                redstoneControllerWiredBE.sendPacket(new IpDataPacket("169.0.0.1", redstoneControllerWiredBE.deviceIp, redstoneControllerWiredBE.mac, payload));
            }else{
                redstoneControllerWiredBE.sendPacket(new IpDataPacket("169.0.0.1", redstoneControllerWiredBE.deviceIp, redstoneControllerWiredBE.mac,redstoneControllerWiredBE.routerMac, payload));
            }
        }

        if(redstoneControllerWiredBE.isReceiver()){
            int currentPower = blockState.getValue(RSReceiverWired.RECEIVED_POWER);
            if(currentPower != redstoneControllerWiredBE.previousPowerLevel){
                redstoneControllerWiredBE.previousPowerLevel = currentPower;
                if(!redstoneControllerWiredBE.onChangeIp.isEmpty()){
                    CompoundTag data = new CompoundTag();
                    data.putString("type","powerChange");
                    data.putInt("power",currentPower);
                    redstoneControllerWiredBE.sendPacket(new IpDataPacket(redstoneControllerWiredBE.onChangeIp, redstoneControllerWiredBE.deviceIp, redstoneControllerWiredBE.mac,data));
                }
            }
        }
    }

    void sendPacket(IpDataPacket data){
        LocalNode portConnected = getDestinationNode(0);
        if(portConnected == null){
            return;
        }
        if(portConnected.getBlockEntity() instanceof IWireNode otherDevice){
            otherDevice.transmitData(portConnected.getIndex(), data);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput view) {
        super.saveAdditional(view);
        view.putIntArray("MAC",mac.getMac());
        ValueOutput.ValueOutputList connections = view.childrenList("connections");
        //WriteView.ListAppender<LocalNodeConnection> listAppender = view.getListAppender("connections", LocalNodeConnection.CODEC);
        for(int i=0;i<nodeCount;i++){
            if(localNodes[i]==null){
                connections.addChild();
            }else{
                ValueOutput connection = connections.addChild();
                localNodes[i].write(connection);
            }
        }
        view.putString("onChangeIp",onChangeIp);

        //view.put("connections",,nodeIDS);
    }//Writing data

    @Override
    protected void loadAdditional(@NonNull ValueInput view) {
        super.loadAdditional(view);
        int[] mac1Bytes = view.getIntArray("MAC").get();
        setMacAddress(mac1Bytes);
        ValueInput.ValueInputList lrv = view.childrenListOrEmpty("connections");
        List<ValueInput> connectionNodes = lrv.stream().toList();

        for (int i=0;i<nodeCount;i++){
            if(i < connectionNodes.size()){
                ValueInput cn = connectionNodes.get(i);

                if(cn.childrenListOrEmpty(LocalNode.ID).isEmpty()){
                    isNodeUsed[i] = false;
                }else{
                    localNodes[i]=new LocalNode(this , cn);
                    isNodeUsed[i] = true;
                }
            }else{
                isNodeUsed[i] = false;
            }
        }
        onChangeIp = view.getStringOr("onChangeIp","");
    }//reading data

    public void updateStates(){
        if(level!=null) level.sendBlockUpdated(getBlockPos(),getBlockState(),getBlockState(), Block.UPDATE_CLIENTS);
    }//notifies the world of updates to the block state

    public boolean hasConnection(int index) {
        //Main.LOGGER.info(index+"");
        return localNodes[index] != null;
    }//checks if the connection is null


    @Override
    public String getIpAddress() {
        return deviceIp;
    }

    private boolean isSender(){
        return isSender;
    }

    private boolean isReceiver(){
        return !isSender;
    }

    private IpDataPacket generateNotSupportedPacket(String component,IpDataPacket original){
        CompoundTag data = new CompoundTag();
        data.putString("type","error");
        data.putString("message","Unsupported operation: "+component);
        return original.createResponsePacket(data);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        if(level==null)return;

        BlockState state = getBlockState();
        level.sendBlockUpdated(worldPosition,state,state,Block.UPDATE_ALL);
    }
}
