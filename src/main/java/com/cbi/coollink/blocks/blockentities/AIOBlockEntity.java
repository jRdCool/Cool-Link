package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.blocks.networkdevices.AccessPoint;
import com.cbi.coollink.blocks.networkdevices.Modem;
import com.cbi.coollink.blocks.networkdevices.Router;
import com.cbi.coollink.blocks.networkdevices.Switch;
import com.cbi.coollink.net.AccessPointLocationPacket;
import com.cbi.coollink.net.ClientWifiConnectionResultPacket;
import com.cbi.coollink.net.WIFIClientIpPacket;
import com.cbi.coollink.net.protocol.CoaxDataPacket;
import com.cbi.coollink.net.protocol.IpDataPacket;
import com.cbi.coollink.net.protocol.Mac;
import com.cbi.coollink.net.protocol.WireDataPacket;
import com.cbi.coollink.rendering.IWireNode;
import com.cbi.coollink.rendering.LocalNode;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class AIOBlockEntity extends BlockEntity implements IWireNode, AccessPoint, Router, Switch, Modem {
	public AIOBlockEntity(BlockPos pos, BlockState state) {
		super(Main.AIO_BLOCK_ENTITY, pos, state);
		//String currentThread = Thread.currentThread().getName();
		//if(currentThread.equals("Server thread")) {
			mac1 = new Mac(deviceID);
			mac2 = new Mac(deviceID);
		//}
		this.localNodes = new LocalNode[getNodeCount()];
		//setNode(0,1,pos,WireType.CAT6);
		//setNode(1,2,pos,WireType.COAX);
	}//Constructor

	//Variable definitions
	private static final int nodeCount = 3;
	private final boolean[] isNodeUsed = new boolean[nodeCount];
	public String password;
	public String ssid;
	public String netPass = "";
	private final LocalNode[] localNodes;
	private static final int deviceID = 0x11;
	public Mac mac1,mac2;

	public static String IPADDRESS_BASE = "192.168.1.";

	public static final int MAX_CONNECTED_DEVICES = 15;
	public ArrayList<ConnectedDevice> connectedDevices=new ArrayList<>();

	private int onlineCheckCounter = 0;
	private boolean online = false;


	// Serialize the BlockEntity
	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);
		view.putInt("number", 89);
		view.putString("password", Objects.requireNonNullElse(password, "password123456"));
		view.putString("ssid", Objects.requireNonNullElse(ssid, "Unconfigured Network"));
		view.putString("Wireless_Password", Objects.requireNonNullElse(netPass, ""));
		view.putIntArray("MAC1",mac1.getMac());
		view.putIntArray("MAC2",mac2.getMac());
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
		password=view.getString("password","password123546");
		ssid = view.getString("ssid","Unconfigured Network");
		netPass = view.getString("Wireless_Password","");
		int[] mac1Bytes = view.getOptionalIntArray("MAC1").get();
		int[] mac2Bytes = view.getOptionalIntArray("MAC2").get();
		setMacAddresses(mac1Bytes,mac2Bytes);
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

	// Deserialize the BlockEntity
	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return createNbt(registryLookup);
	}//Rendering Crucial


	public static void tick(World world, BlockPos pos, BlockState state, AIOBlockEntity be) {
		//called from the AIONetwork block class
		if(world.isClient){
			return;
		}
		be.onlineCheckCounter ++;
		if(be.onlineCheckCounter == 5*20){//if the check online counter is at the check level
			LocalNode output = be.getDestinationNode(2);//get the other end of the coax cable
			if(output == null){//if there was nothing at the other end
				be.online = false;//then you are offline
			}else{
				if(output.getBlockEntity() instanceof IWireNode outputNode){//convert the output to a wire node
					//send a request to the other end asking if it is online
					outputNode.transmitData(output.getIndex(), CoaxDataPacket.ofRequest());
				}
			}
		}else if(be.onlineCheckCounter >= 10*20){
			be.online =false;
			be.onlineCheckCounter = 0;
		}

	}//run on tick
	public void updateStates(){
		if(world!=null) world.updateListeners(getPos(),getCachedState(),getCachedState(), Block.NOTIFY_LISTENERS);
	}//notifies the world of updates to the block state

	public void setMacAddresses(int[] mac1,int[] mac2){
		this.mac1 = new Mac(mac1);
		this.mac2 = new Mac(mac2);
	}//mac addresses new

	@Deprecated
	public void setMacAddresses(byte[] mac1,byte[] mac2){
		this.mac1 = new Mac(mac1);
		this.mac2 = new Mac(mac2);
	}//mac addresses old


	@Override
	public Vec3d getNodeOffset(int node) {
		double node0XN = 0.275;
		double node1XN = 0.475;
		double node2XN = 0.75;
		double nodeZN = 0.9;
		double nodeY = .05;
		Vec3d[][] nodes = {
				{new Vec3d(node0XN,nodeY,nodeZN), new Vec3d(node1XN,nodeY,nodeZN), new Vec3d(node2XN,nodeY,nodeZN) },// - - - - - - - - - - - - - - NORTH
				{new Vec3d(1-nodeZN,nodeY,node0XN), new Vec3d(1-nodeZN,nodeY,node1XN), new Vec3d(1-nodeZN,nodeY,node2XN) },// - - - - - - - EAST
				{new Vec3d(1-node0XN,nodeY,1-nodeZN), new Vec3d(1-node1XN,nodeY,1-nodeZN), new Vec3d(1-node2XN,nodeY,1-nodeZN) },// SOUTH
				{new Vec3d(nodeZN,nodeY,1-node0XN), new Vec3d(nodeZN,nodeY,1-node1XN), new Vec3d(nodeZN,nodeY,1-node2XN) } // - - - - - - - WEST
		};
		int dir = 0;
		switch (getCachedState().get(Properties.HORIZONTAL_FACING)){
			case EAST -> dir=1;
			case SOUTH -> dir=2;
			case WEST -> dir=3;
			default -> {}
		}

		return nodes[dir][node];
	}//Getting the offset from the block origin for the nodes

	@Override
	public IWireNode getWireNode(int index) {
		return this;
	}//returns the wire node

	@Override
	public int getOtherNodeIndex(int index) {
		return localNodes[index].getOtherIndex();
	}//returns the index of the remote connection

	@Override
	public @Nullable LocalNode getLocalNode(int index) {
		return localNodes[index];
	}//returns the local node

	@Override
	public void setNode(int index, int otherNode, BlockPos pos, WireType type) {
		this.localNodes[index] = new LocalNode(this, index, otherNode, type, pos);
		isNodeUsed[index]=true;
		markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
	}//Sets the node connection

	@Override
	public void removeNode(int index, boolean dropWire) {
		//LocalNode old = this.localNodes[index];
		this.localNodes[index] = null;
		markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
		this.setIsNodeUsed(index,false);
	}//Breaks the wire

	@Override
	public WireType getPortType(int index) {
		if(index <= 1) return WireType.CAT6;
		else return WireType.COAX;
	}//Gathers the type of each port

	@Override
	public boolean isNodeInUse(int index) {
		return isNodeUsed[index];
	}//Checks if the requested node is in use

	public void setIsNodeUsed(int index,boolean set){
		isNodeUsed[index]=set;
	}//set the node usage

	/**
	 * Get the node of the destination device at the other end of the wire (not the next place the wire connects)
	 *
	 * @param connectionIndex The index of the start of the connection on this device
	 * @return The {@link LocalNode} representing the other end of the wire, where index is the index of the node connection this wire terminates in on the receiving device
	 */
	@Override
	public LocalNode getDestinationNode(int connectionIndex) {
		if(connectionIndex < 0 || connectionIndex > 2){
			return null;
		}
		if(localNodes[connectionIndex] == null){
			return null;
		}
		LocalNode outputNode = IWireNode.traverseWire(localNodes[connectionIndex]);
		if(outputNode == null || outputNode.getType() != localNodes[connectionIndex].getType()){
			Main.LOGGER.error("Null destination or incorrect output wire type (from AIO_BLOCK_ENTITY port: "+connectionIndex+")");
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
		//Main.LOGGER.info("Received data: "+data+" on port: "+connectionIndex+" at "+getPos());
		if(data instanceof CoaxDataPacket coax){
			handleCoaxPacket(coax);
		}else if(data instanceof IpDataPacket ip){
			handleEthernetPacker(ip,connectionIndex);
		}
	}

	private void handleCoaxPacket(CoaxDataPacket cdp){
		online = cdp.isUplinkOnline();
		onlineCheckCounter = 0;
	}

	private void handleEthernetPacker(IpDataPacket data,int nodeIndex){

	}


	@Override
	public boolean hasConnection(int index) {
		//Main.LOGGER.info(index+"");
		return localNodes[index] != null;
	}//checks if the connection is null

	@Override
	public int getNodeCount() {
		return nodeCount;
	}//returns the number of nodes the device has

	@Override
	public String toString() {
		return "AIOBlockEntity{" +
				"isNodeUsed=" + Arrays.toString(isNodeUsed) +
				", password='" + password + '\'' +
				", ssid='" + ssid + '\'' +
				", netPass='" + netPass + '\'' +
				", mac1=" + mac1 +
				", mac2=" + mac2 +
				", connectedDevices=" + connectedDevices +
				", localNodes=" + Arrays.toString(localNodes) +
				'}';
	}

	/**
	 * Take an ip packet from the wifi and transmits it over the network
	 *
	 * @param packet The ip packet transmitted over the wifi
	 * @param player The player that transmitted it
	 */
	@Override
	public void processIncomingWifiPacket(WIFIClientIpPacket packet, ServerPlayerEntity player) {

	}

	/**
	 * Send the requesting player an array if block positions repressing all the wireless access points connected to this network
	 *
	 * @param player The player sending the request
	 */
	@Override
	public void getNetworkAccessPointLocations(ServerPlayerEntity player) {
		ServerPlayNetworking.send(player,new AccessPointLocationPacket(new BlockPos[]{getPos()},ssid));
	}

	@Override
	public void handleClientWifiConnectionRequest(String password, Mac deviceMacAddress, ServerPlayerEntity player, String deviceName) {
		//in this situation we are already at the rougher swo we can just process this immediately, but if wer were not at the rougher then we would have to send a packet to the rougher with the info

		//check if this is device is already connected
		for(ConnectedDevice device: connectedDevices){
			if(device.deviceMac().equals(deviceMacAddress)){
				//this device is already connected
				ServerPlayNetworking.send(player,new ClientWifiConnectionResultPacket(false,false,device.ipAddress(),ssid,online));
				return;
			}
		}

		if(connectedDevices.size() >= MAX_CONNECTED_DEVICES){
			//fail, too many connected devices
			ServerPlayNetworking.send(player,new ClientWifiConnectionResultPacket(false,true,"",ssid,false));
			return;
		}

		if(netPass.isEmpty() || password.equals(netPass)){//correct password
			String deviceIp = generateNewIp();
			connectedDevices.add(new ConnectedDevice(deviceIp,deviceMacAddress,deviceName));
			ServerPlayNetworking.send(player,new ClientWifiConnectionResultPacket(false,false,deviceIp,ssid,online));
		}else{
			//incorrect password
			ServerPlayNetworking.send(player,new ClientWifiConnectionResultPacket(true,false,"",ssid,false));
		}
	}

	/**
	 * Get the ssid of the network this ap is attached to
	 *
	 * @return The ssid of this network
	 */
	@Override
	public String getSsid() {
		return ssid;
	}

	private String generateNewIp(){
		String ip = "";
		boolean ipExsists = false;

		do{
			ipExsists = false;
			int num = (int)(Math.random()*252)+2;
			ip = IPADDRESS_BASE+num;
			//check if the address exsists
			for(ConnectedDevice device: connectedDevices){
				if(device.ipAddress().equals(ip)){
					ipExsists = true;
					break;
				}
			}
		}while (ipExsists);

		return ip;
	}

	public record ConnectedDevice(String ipAddress, Mac deviceMac, String deviceName){
		@Override
		public String toString() {
			return "ConnectedDevice{" +
					"ipAddress='" + ipAddress + '\'' +
					", deviceMac=" + deviceMac +
					", deviceName='" + deviceName + '\'' +
					'}';
		}
	}
}