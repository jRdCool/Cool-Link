package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.Util;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.blocks.networkdevices.*;
import com.cbi.coollink.net.AccessPointLocationPacket;
import com.cbi.coollink.net.ClientWifiConnectionResultPacket;
import com.cbi.coollink.net.WIFIClientIpPacket;
import com.cbi.coollink.net.WIFIServerIpPacket;
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
public class AIOBlockEntity extends BlockEntity implements IWireNode, AccessPoint, Router, Switch, Modem, NetworkDevice {
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
	public String password = "password123456";
	public String ssid = "Unconfigured Network";
	public String netPass = "";
	private final LocalNode[] localNodes;
	private static final int deviceID = 0x11;
	public Mac mac1,mac2;

	public static String IPADDRESS_BASE = "192.168.1.";

	public static final int MAX_CONNECTED_DEVICES = 15;
	public ArrayList<ConnectedDevice> connectedDevices=new ArrayList<>();

	private int onlineCheckCounter = 0;
	private boolean online = false;

	private final Queue<IpDataPacket> switchingPacketQueue = new ArrayDeque<>();

	private final ArrayList<Mac> eth0SwitchingTable = new ArrayList<>();

	private final ArrayList<Mac> eth1SwitchingTable = new ArrayList<>();

	private final HashMap<Mac, ServerPlayerEntity> mobileClientRouting = new HashMap<>();

	private final ArrayList<ConnectedDevice> rememberedDevices = new ArrayList<>();


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

		WriteView.ListView rememberedDevicesList = view.getList("rememberedDevices");
		for(ConnectedDevice device: rememberedDevices){
			device.writeToView(rememberedDevicesList.add());
		}
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
		ReadView.ListReadView rememberedDevicesList = view.getListReadView("rememberedDevices");
		rememberedDevices.clear();
		for(ReadView device: rememberedDevicesList){
			rememberedDevices.add(new ConnectedDevice(device));
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

		be.switchProcessPacketQueue();//process packets that need to be switched

		//verify the devices connected to the network by sending them a ping packet every 10 seconds
		ArrayList<ConnectedDevice> disconnectList = new ArrayList<>();

		for(ConnectedDevice device: be.connectedDevices){
			device.connectionTimer[0]++;

			if(device.connectionTimer[0] == 200){
				be.sendPingPacket(device);
			}

			if(device.connectionTimer[0] == 400){//disconnect them if it has been 20 seconds
				disconnectList.add(device);
				device.connectionTimer[0] = 0;
			}
		}
		for(ConnectedDevice device: disconnectList){
			be.processDisconnect(device.deviceMac);
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
			Main.LOGGER.error("Null destination or incorrect output wire type (from AIO_BLOCK_ENTITY port: "+connectionIndex+") "+getPos());
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
		if(data instanceof CoaxDataPacket coax){
			handleCoaxPacket(coax);
		}else if(data instanceof IpDataPacket ip){
			handleEthernetPacket(ip,connectionIndex);
		}
	}

	private void handleCoaxPacket(CoaxDataPacket cdp){
		online = cdp.isUplinkOnline();
		onlineCheckCounter = 0;
	}

	private void handleEthernetPacket(IpDataPacket data, int nodeIndex){
		switch (nodeIndex){
			case 0 -> {
				if(!eth0SwitchingTable.contains(data.getSourceMacAddress())) {
					eth0SwitchingTable.add(data.getSourceMacAddress());
				}
			}
			case 1 -> {
				if(!eth1SwitchingTable.contains(data.getSourceMacAddress())) {
					eth1SwitchingTable.add(data.getSourceMacAddress());
				}
			}
		}

		//check if this packet is intended for this router
		//if so process this packet
		if(!data.hasDestinationMac()){
			//resolve IP
			if(resolveMacAddress(data)){
				return;//if this is a device joining the network do not add the packet to the routing queue
			}
		}
		if(Util.parseIpGetIp(data.getDestinationIpAddress()).equals("169.0.0.1")){
			//handle packet
			handleRouterPacket(data);
		}else {
			//otherwise send this indo the switching queue
			switchingPacketQueue.add(data);
		}

	}


	@Override
	public boolean hasConnection(int index) {
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
		//Main.LOGGER.info("AIO received WIFI packet: "+packet);
		//apply delay based on distance
		mobileClientRouting.put(packet.payload().getSourceMacAddress(), player);
		//check if this packet is going to this AOI and process it if so
		//place the packet in the routing queue
		handleEthernetPacket(packet.payload(),-1);
	}

	/**
	 * Send the requesting player an array if block positions repressing all the wireless access points connected to this network
	 *
	 * @param player The player sending the request
	 */
	@Override
	public void getNetworkAccessPointLocations(ServerPlayerEntity player) {
		ServerPlayNetworking.send(player,new AccessPointLocationPacket(new BlockPos[]{getPos()},getSsid()));
	}

	@Override
	public void handleClientWifiConnectionRequest(String password, Mac deviceMacAddress, ServerPlayerEntity player, String deviceName) {
		//in this situation we are already at the rougher so we can just process this immediately, but if wer were not at the rougher then we would have to send a packet to the rougher with the info

		//check if this is device is already connected
		for(ConnectedDevice device: connectedDevices){
			if(device.deviceMac().equals(deviceMacAddress)){
				//this device is already connected
				mobileClientRouting.put(deviceMacAddress, player);
				ClientWifiConnectionResultPacket cpn = new ClientWifiConnectionResultPacket(false,false,device.ipAddress(),getSsid(),online);
				ServerPlayNetworking.send(player,cpn);
				return;
			}
		}

		if(connectedDevices.size() >= MAX_CONNECTED_DEVICES){
			//fail, too many connected devices
			ClientWifiConnectionResultPacket cpn = new ClientWifiConnectionResultPacket(false,true,"",getSsid(),false);
			ServerPlayNetworking.send(player,cpn);
			return;
		}

		if(netPass.isEmpty() || password.equals(netPass)){//correct password
			ConnectedDevice remembered = getRememberedDevice(deviceMacAddress);
			if(remembered!=null) {
				connectedDevices.add(remembered);
				ClientWifiConnectionResultPacket cpn = new ClientWifiConnectionResultPacket(false, false, remembered.ipAddress(), getSsid(), online);
				mobileClientRouting.put(deviceMacAddress, player);
				ServerPlayNetworking.send(player, cpn);
			}else {
				String deviceIp = generateNewIp();
				ConnectedDevice newDevice = new ConnectedDevice(deviceIp, deviceMacAddress, deviceName,new int[]{0});
				connectedDevices.add(newDevice);
				rememberDevice(newDevice);
				ClientWifiConnectionResultPacket cpn = new ClientWifiConnectionResultPacket(false, false, deviceIp, getSsid(), online);
				mobileClientRouting.put(deviceMacAddress, player);
				ServerPlayNetworking.send(player, cpn);
			}
		}else{
			//incorrect password
			ClientWifiConnectionResultPacket cpn = new ClientWifiConnectionResultPacket(true,false,"",getSsid(),false);
			ServerPlayNetworking.send(player,cpn);
		}
	}

	/**
	 * Get the ssid of the network this ap is attached to
	 *
	 * @return The ssid of this network
	 */
	@Override
	public String getSsid() {
		if(ssid == null){
			Main.LOGGER.error("AIO SSID NULL!");
		}
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

	@Override
	public void switchProcessPacketQueue() {
		final int maxPackersPerTick = 5;
		for(int i=0;i<maxPackersPerTick;i++){
			IpDataPacket packet = switchingPacketQueue.poll();
			if(packet == null){
				return;
			}
			sendPacket(packet);
		}
	}

	@Override
	public boolean knowsWhereRouterIs() {
		return true;
	}

	private void sendPacket(IpDataPacket packet) {
		//Main.LOGGER.info("AIO processing send packet: "+packet);
		//figure out what port to send out the paket on/if the packet needs to be sent over wifi
		int port = -1;
		if(eth0SwitchingTable.contains(packet.getDestinationMacAddress())){
			port = 0;
		}
		if(eth1SwitchingTable.contains(packet.getDestinationMacAddress())){
			port = 1;
		}

		if(port == -1){
			//check for wifi routing
			//checkPhoneRouting
			ServerPlayerEntity player = mobileClientRouting.get(packet.getDestinationMacAddress());
			if(player != null){
				//check if the player is in the same dimension
				if(player.getWorld().equals(getWorld())) {
					sendWifiClientPacket(packet, player);
				}
			}
			return;
		}

		LocalNode portConnected = getDestinationNode(port);
		if(portConnected == null){
			//this port is not connected, remove every mac from this
			//TODO remove these devices from the device list before clearing the list
			switch (port){
				case 0 -> eth0SwitchingTable.clear();
				case 1 -> eth1SwitchingTable.clear();
			}
			//also i guess the packet is lost then
			return;
		}
		if(portConnected.getBlockEntity() instanceof IWireNode otherDevice){
			//Main.LOGGER.info("AIO sending packet on port: "+port+" "+packet);
			otherDevice.transmitData(portConnected.getIndex(), packet);
		}else{
			//uhhhhh i guess the packet is lost then
		}
	}

	void sendWifiClientPacket(IpDataPacket data, ServerPlayerEntity player){
		//Main.LOGGER.error("AIO Sending packet to client: "+data+" "+player.getName());
		ServerPlayNetworking.send(player,new WIFIServerIpPacket(data, getPos()));
	}


	@Override
	public boolean resolveMacAddress(IpDataPacket data) {
		String ip = Util.parseIpGetIp(data.getDestinationIpAddress());
		if(ip.equals("169.0.0.1")){
			setupNewEthDevice(data);
			return true;
		}else{
			//just resolve the Mac
			ConnectedDevice device = null;
			for(ConnectedDevice cd: connectedDevices){
				if(cd.ipAddress().equals(ip)){
					device = cd;
					break;
				}
			}
			if(device == null){//if the device is not found on the network then eat the packet and send a 404 type response
				//TODO send some sort of no device found result
				return true;
			}
			data.setDestinationMacAddress(device.deviceMac());
			return false;
		}

	}

	private void setupNewEthDevice(IpDataPacket data) {
		data.setDestinationMacAddress(mac1);
		//having to add the device to the network
		//see if this device is already on the net
		ConnectedDevice device = null;
		for(ConnectedDevice cd: connectedDevices){
			if(cd.deviceMac().equals(data.getSourceMacAddress())){
				device = cd;
				break;
			}
		}
		if(device != null) {

			String packetType = data.getData().getString("type","connect");
			//if the device is requesting a service other than connection
			if(!packetType.equals("connect")){
				handleRouterPacket(data);
				return;
			}
			//if so then send them the valid packet back
			NbtCompound response = new NbtCompound();
			response.putString("type","connected");
			response.putBoolean("online",online);
			device.connectionTimer()[0]=0;
			sendPacket(new IpDataPacket(device.ipAddress(),"169.0.0.1",mac1,device.deviceMac(),response));
			return;
		}

		//check to see if the network is full
		if(connectedDevices.size() >= MAX_CONNECTED_DEVICES) {
			//if it is then send a rejection packet
			//perhaps do this later
			return;
		}
		//see if this device has been on the network before
		ConnectedDevice remembered = getRememberedDevice(data.getSourceMacAddress());
		if(remembered!=null){
			connectedDevices.add(remembered);
			NbtCompound response = new NbtCompound();
			response.putString("type","connected");
			response.putBoolean("online",online);
			remembered.connectionTimer()[0]=0;
			sendPacket(new IpDataPacket(remembered.ipAddress(),"169.0.0.1",mac1, data.getSourceMacAddress(),response));
		}else {
			//register this device on the network and send it its IP back
			String deviceIp = generateNewIp();
			String deviceName = data.getData().getString("deviceName", "Unnamed device");
			ConnectedDevice newDevice = new ConnectedDevice(deviceIp, data.getSourceMacAddress(), deviceName,new int[]{0});
			connectedDevices.add(newDevice);
			rememberDevice(newDevice);
			NbtCompound response = new NbtCompound();
			response.putString("type", "connected");
			response.putBoolean("online", online);
			sendPacket(new IpDataPacket(deviceIp, "169.0.0.1", mac1, data.getSourceMacAddress(), response));
		}
	}

	public void handleRouterPacket(IpDataPacket data){
		String type = data.getData().getString("type","unknown");
		switch (type) {
			case "connect" -> setupNewEthDevice(data);
			case "disconnect" -> processDisconnect(data.getSourceMacAddress());
			case "ping" -> {
				NbtCompound pongCompound = new NbtCompound();
				pongCompound.putString("type","pong");
				sendPacket(data.createResponsePacket(pongCompound));
			}
			case "pong" -> {
				for(ConnectedDevice cd: connectedDevices){
					if(cd.deviceMac().equals(data.getSourceMacAddress())){
						cd.connectionTimer()[0]=0;
					}
				}
			}
		}
	}

	@Override
	public String getIpAddress() {
		return "169.0.0.1";
	}

	void processDisconnect(Mac disconnectedMac){
		//in the case that this disconnect is from a wifi client
		mobileClientRouting.remove(disconnectedMac);
		//remove from connected devices
		for(int i=0;i<connectedDevices.size();i++){
			if(connectedDevices.get(i).deviceMac.equals(disconnectedMac)){
				connectedDevices.get(i).connectionTimer()[0]=0;
				connectedDevices.remove(i);
				break;
			}
		}
	}

	void rememberDevice(ConnectedDevice device){
		//check if the list needs to be purged a bit
		int MAX_REMEMBERED_DEVICES = 60;
		if(rememberedDevices.size()>=MAX_REMEMBERED_DEVICES){
			//find the first one that is not online and remove that one
			for(int i=0;i<rememberedDevices.size();i++){
				if(!connectedDevices.contains(rememberedDevices.get(i))){
					rememberedDevices.remove(i);
					break;
				}

			}
		}

		//add this new device
		rememberedDevices.add(device);

		markDirty();//save changes to the server
	}

	private void sendPingPacket(ConnectedDevice device){
		NbtCompound pingCompound =  new NbtCompound();
		pingCompound.putString("type","ping");
		sendPacket(new IpDataPacket(device.ipAddress(), "169.0.0.1", mac1,device.deviceMac() ,pingCompound));
	}

	ConnectedDevice getRememberedDevice(Mac deviceMac){
		for(ConnectedDevice device:rememberedDevices){
			if(device.deviceMac().equals(deviceMac)){
				return device;
			}
		}
		return null;
	}

	public record ConnectedDevice(String ipAddress, Mac deviceMac, String deviceName, int[] connectionTimer){

		@Override
		public String toString() {
			return "ConnectedDevice{" +
					"ipAddress='" + ipAddress + '\'' +
					", deviceMac=" + deviceMac +
					", deviceName='" + deviceName + '\'' +
					'}';
		}

		public ConnectedDevice(ReadView view){
			this(view.getString("ip","0.0.0.0"),new Mac(view.getOptionalIntArray("mac").orElse(new int[]{0,0,0})),view.getString("name","Forgotten device"),new int[]{0});
		}

		public void writeToView(WriteView output){
			output.putString("ip",ipAddress);
			output.putIntArray("mac",deviceMac.getMac());
			output.putString("name",deviceName);
		}
	}
}