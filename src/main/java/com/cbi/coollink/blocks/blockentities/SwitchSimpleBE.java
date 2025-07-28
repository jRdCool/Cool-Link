package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.blocks.networkdevices.Router;
import com.cbi.coollink.blocks.networkdevices.Switch;
import com.cbi.coollink.net.protocol.IpDataPacket;
import com.cbi.coollink.net.protocol.Mac;
import com.cbi.coollink.net.protocol.WireDataPacket;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SwitchSimpleBE extends BlockEntity implements IWireNode, Switch {

    public SwitchSimpleBE(BlockPos pos, BlockState state) {
        super(Main.SWITCH_SIMPLE_BLOCK_ENTITY, pos, state);
        //String currentThread = Thread.currentThread().getName();
        this.localNodes = new LocalNode[getNodeCount()];
    }//Constructor

    //variables
    private static final int nodeCount = 5;
    private final boolean[] isNodeUsed = new boolean[nodeCount];
    private final LocalNode[] localNodes;

    private int routerPort = -1;

    private int routerCheckCounter = 0;

    private final Queue<IpDataPacket> switchingQueue = new ArrayDeque<>();

    private final ArrayList<IncomingPacket> incomingPackets = new ArrayList<>();


    private final ArrayList<Mac>[] switchingTables = new ArrayList[]{new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>()};

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        WriteView.ListView connections = view.getList("connections");
        //WriteView.ListAppender<LocalNodeConnection> listAppender = view.getListAppender("connections", LocalNodeConnection.CODEC);
        for(int i=0;i<nodeCount;i++){
            if(localNodes[i]==null){
                connections.add();
            }else{
                WriteView connection = connections.add();
                localNodes[i].write(connection);
            }
        }

        //view.put("connections",,nodeIDS);
    }//Writing data

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        ReadView.ListReadView lrv = view.getListReadView("connections");
        List<ReadView> connectionNodes = lrv.stream().toList();

        for (int i=0;i<nodeCount;i++){
            if(i < connectionNodes.size()){
                ReadView cn = connectionNodes.get(i);

                if(cn.getOptionalInt(LocalNode.ID).isEmpty()){
                    isNodeUsed[i] = false;
                }else{
                    localNodes[i]=new LocalNode(this , cn);
                    isNodeUsed[i] = true;
                }
            }else{
                isNodeUsed[i] = false;
            }
        }
    }//reading data

    @Override
    public Vec3d getNodeOffset(int node) {
        double node0XN = 0.3;
        double node1XN = 0.4;
        double node2XN = 0.5;
        double node3XN = 0.6;
        double node4XN = 0.7;
        double nodeZN = 0.625;
        double nodeY = .04  ;
        Vec3d[][] nodes = {
                {new Vec3d(node0XN,nodeY,nodeZN), new Vec3d(node1XN,nodeY,nodeZN), new Vec3d(node2XN,nodeY,nodeZN), new Vec3d(node3XN,nodeY,nodeZN), new Vec3d(node4XN,nodeY,nodeZN) },// - - - - - - - - - - - - - - NORTH
                {new Vec3d(1-nodeZN,nodeY,node0XN), new Vec3d(1-nodeZN,nodeY,node1XN), new Vec3d(1-nodeZN,nodeY,node2XN), new Vec3d(1-nodeZN,nodeY,node3XN), new Vec3d(1-nodeZN,nodeY,node4XN) },// - - - - - - - EAST
                {new Vec3d(1-node0XN,nodeY,1-nodeZN), new Vec3d(1-node1XN,nodeY,1-nodeZN), new Vec3d(1-node2XN,nodeY,1-nodeZN), new Vec3d(1-node3XN,nodeY,1-nodeZN), new Vec3d(1-node4XN,nodeY,1-nodeZN) },// SOUTH
                {new Vec3d(nodeZN,nodeY,1-node0XN), new Vec3d(nodeZN,nodeY,1-node1XN), new Vec3d(nodeZN,nodeY,1-node2XN), new Vec3d(nodeZN,nodeY,1-node3XN), new Vec3d(nodeZN,nodeY,1-node4XN) } // - - - - - - - WEST
        };
        int dir = 0;
        switch (getCachedState().get(Properties.HORIZONTAL_FACING)){
            case EAST -> dir=1;
            case SOUTH -> dir=2;
            case WEST -> dir=3;
            default -> {}
        }

        return nodes[dir][node];
    }//offsets for the nodes

    @Override
    public IWireNode getWireNode(int index) {
        return this;
    }//returns this class

    @Override
    public int getOtherNodeIndex(int index) {
        return localNodes[index].getOtherIndex();
    }//getting the index of the other node

    @Override
    public LocalNode getLocalNode(int index) {
        return localNodes[index];
    }//getting the local nodes

    @Override
    public void setNode(int index, int otherNode, BlockPos pos, WireType type) {
        this.localNodes[index] = new LocalNode(this, index, otherNode, type, pos);
        isNodeUsed[index]=true;
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
    }//setting the node status

    @Override
    public void removeNode(int index, boolean dropWire) {
        this.localNodes[index] = null;
        markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
        this.setIsNodeUsed(index,false);
    }//Removing the wire

    @Override
    public WireType getPortType(int index) {
        return WireType.CAT6;
    }//The types of nodes declaration

    @Override
    public boolean isNodeInUse(int index) {
        return isNodeUsed[index];
    }//checks if the node is in use

    @Override
    public void setIsNodeUsed(int index, boolean set) {
        isNodeUsed[index]=set;
    }//setting node usage

    /**
     * Get the node of the destination device at the other end of the wire (not the next place the wire connects).
     * NOTE FOR IMPLEMENTATION: first: validate that the specified node has a connection/exsists then call IWireNode.traverseWire()
     *
     * @param connectionIndex The index of the start of the connection on this device
     * @return The {@link LocalNode} representing the other end of the wire, where index is the index of the node connection this wire terminates in on the receiving device
     */
    @Override
    public LocalNode getDestinationNode(int connectionIndex) {
        if(connectionIndex < 0 || connectionIndex > 5){
            return null;
        }
        if(localNodes[connectionIndex] == null){
            return null;
        }
        LocalNode outputNode = IWireNode.traverseWire(localNodes[connectionIndex]);
        if(outputNode == null || outputNode.getType() != localNodes[connectionIndex].getType()){
            Main.LOGGER.error("Null destination or incorrect output wire type (from Simple switch port: "+connectionIndex+") "+getPos());
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
        if(data instanceof IpDataPacket ipPacket) {
            //add the mac from this port to the switching table
            if(connectionIndex != routerPort){
                switchingTables[connectionIndex].add(ipPacket.getSourceMacAddress());
            }
            World w = getWorld();
            if(w == null){
                return;
            }
            incomingPackets.add(new IncomingPacket(ipPacket, w.getTime()));
        }
    }

    @Override
    public int getNodeCount() {
        return nodeCount;
    }//returns the number of nodes

    @Override
    public String toString() {
        return "SwitchSimpleBE{" +
                "isNodeUsed=" + Arrays.toString(isNodeUsed) +
                ", localNodes=" + Arrays.toString(localNodes) +
                '}';
    }

    @SuppressWarnings("unused")
    public void updateStates(){
        if(world!=null) world.updateListeners(getPos(),getCachedState(),getCachedState(), Block.NOTIFY_LISTENERS);
    }//notifies the world of updates to the block state

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }//Rendering crucial

    @Override
    public void switchProcessPacketQueue() {
        for(int i = 0; i<15 && !switchingQueue.isEmpty(); i++){
            IpDataPacket packet = switchingQueue.poll();
            if(packet.hasDestinationMac()){
                int port = getOutputPort(packet);
                if(port != -1){
                    //send
                    sendPacket(packet,port);
                }
            }else{
                if(routerPort != -1){
                    //send
                    sendPacket(packet,routerPort);
                }
            }
        }
    }

    private int getOutputPort(IpDataPacket packet){
        for(int i=0;i<5;i++){
            if(i == routerPort){
                continue;
            }
            for(Mac addr: switchingTables[i]){
                if(addr.equals(packet.getDestinationMacAddress())){
                    return i;
                }
            }
        }

        return routerPort;
    }

    void sendPacket(IpDataPacket packet,int port){
        LocalNode connectedDevice = getDestinationNode(port);
        if(connectedDevice != null){
            BlockEntity otherBlock = connectedDevice.getBlockEntity();
            if(otherBlock instanceof IWireNode node){
                node.transmitData(connectedDevice.getIndex(),packet);
            }
        }else{
            //this port is now a null destination so dump the routing table for this port
            switchingTables[port].clear();
        }
    }

    @Override
    public boolean knowsWhereRouterIs() {
        return routerPort >= 0;
    }

    private void findRouter(){
        for(int i=0;i<5;i++){

            if(localNodes[i] != null){
                LocalNode connectedDevice = getDestinationNode(i);
                if(connectedDevice == null){
                    continue;
                }
                //Main.LOGGER.info("Searching for router: "+i+" "+getPos());
                BlockEntity connectedBlockEntity = connectedDevice.getBlockEntity();
                if(connectedBlockEntity instanceof Router){
                    //Main.LOGGER.info("Found router on port: "+i);
                    routerPort = i;
                    return;
                }
                if(connectedBlockEntity instanceof Switch otherSwitch){
                    if(otherSwitch.knowsWhereRouterIs()){
                        //Main.LOGGER.info("Found a block that knows where the router is: "+i);
                        routerPort = i;
                    }
                }
            }
        }
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, SwitchSimpleBE switchBE) {
        if(world.isClient){
            return;
        }
        switchBE.routerCheckCounter ++;
        if((switchBE.knowsWhereRouterIs() && switchBE.routerCheckCounter >= 200) || (!switchBE.knowsWhereRouterIs() && switchBE.routerCheckCounter >= 20)){
            switchBE.findRouter();
            switchBE.routerCheckCounter = 0;
        }

        //prevent packets that just came in from being instantly transmitted

        long currentTime = world.getTime();
        //go through all the incoming packets
        for(int i=0;i<switchBE.incomingPackets.size();i++){
            //if the packet did not come in the current tick
            if(switchBE.incomingPackets.get(i).tickTime != currentTime){
                //add it to the switching queue
                switchBE.switchingQueue.add(switchBE.incomingPackets.remove(i).packet);
                i--;
            }
        }
        //process packets in the switching queue
        switchBE.switchProcessPacketQueue();
    }

    private record IncomingPacket(IpDataPacket packet, long tickTime){}
}
