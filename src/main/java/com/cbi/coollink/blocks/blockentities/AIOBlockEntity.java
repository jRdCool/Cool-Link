package com.cbi.coollink.blocks.blockentities;

import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.createadditons.WireType;
import com.cbi.coollink.net.protocol.Mac;
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

// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class AIOBlockEntity extends BlockEntity implements IWireNode {
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
	public String netPass;
	private final LocalNode[] localNodes;
	private static final int deviceID = 0x11;
	public Mac mac1,mac2;
	public ArrayList<String> connectedDevices=new ArrayList<>();
	public ArrayList<String> deviceName=new ArrayList<>();
	public ArrayList<String> deviceIP=new ArrayList<>();



	// Serialize the BlockEntity
	@Override
	protected void writeData(WriteView view) {
		super.writeData(view);
		view.putInt("number", 89);
		view.putString("password", Objects.requireNonNullElse(password, "password123546"));
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

//	@Override
//	public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
//		// Save the current value of the number to the nbt
//		nbt.putInt("number", 89);
//		nbt.putString("password", Objects.requireNonNullElse(password, "password123546"));
////		nbt.putString("ssid", Objects.requireNonNullElse(ssid, "Un-configured Network"));
//		nbt.putString("Wireless_Password", Objects.requireNonNullElse(netPass, ""));
//		nbt.putByteArray("MAC1",mac1.getBytes());
//		nbt.putByteArray("MAC2",mac2.getBytes());
//		NbtList nodeIDS = new NbtList();
//		for(int i=0;i<nodeCount;i++){
//			NbtCompound compound = new NbtCompound();
//			if(localNodes[i]==null){
//				nodeIDS.add(compound);
//				continue;
//			}
//
//			localNodes[i].write(compound);
//			nodeIDS.add(compound);
//		}
//		nbt.put("connections",nodeIDS);
//
//		super.writeNbt(nbt,registryLookup);
//	}


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
//	@Override
//	public void readNbt(NbtCompound nbt,RegistryWrapper.WrapperLookup registryLookup) {
//		super.readNbt(nbt,registryLookup);
//		password=nbt.getString("password");
//		ssid = nbt.getString("ssid");
//		netPass = nbt.getString("Wireless_Password");
//		byte[] mac1Bytes = nbt.getByteArray("MAC1");
//		byte[] mac2Bytes = nbt.getByteArray("MAC2");
//		setMacAddresses(mac1Bytes,mac2Bytes);
//		NbtList nodeIDS = nbt.getList("connections",NbtCompound.COMPOUND_TYPE);
//		for (int i=0;i<nodeCount;i++){
//			NbtCompound compound = nodeIDS.getCompound(i);
//			if(compound==null || compound.isEmpty()){
//				isNodeUsed[i] = false;
//				continue;
//			}
//			localNodes[i]=new LocalNode(this , compound);
//			isNodeUsed[i] = true;
//		}
//	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return createNbt(registryLookup);
	}//Rendering Crucial

	@SuppressWarnings("unused")
	public static void tick(World world, BlockPos pos, BlockState state, AIOBlockEntity be) {
		//Main.LOGGER.info(be.password);
		be.createConnectedDevices();
	}//run on tick
	public void updateStates(){
		if(world!=null) world.updateListeners(getPos(),getCachedState(),getCachedState(), Block.NOTIFY_LISTENERS);
	}//notifies the world of updates to the block state

	void createConnectedDevices() {
		if(deviceName.isEmpty() && deviceIP.isEmpty()) {
			if(!connectedDevices.isEmpty())
			{connectedDevices.clear();}
			connectedDevices.add("No connected devices");

		}
		else if(deviceName.size() != deviceIP.size())
		{
			if(deviceName.size() < deviceIP.size())
			{
				//TODO: Call a function that assigns ip addresses to devices that don't have them (DHCP Server)
				//return;
			}
			else {
				Main.LOGGER.info("!!!!! !!!ERROR!!! MORE DEVICES THAN IPs !!!!!");
			}
		}
		else {
			for (int i=0;i<deviceName.size();i++)
			{
                connectedDevices.set(i, deviceName.get(i) + "  " + " ".repeat(Math.max(0, 16 - deviceName.get(i).length())) + deviceIP.get(i));
				String sb = "  " + " ".repeat(Math.max(0, 16 - deviceName.get(i).length()));
				connectedDevices.set(i,deviceName.get(i)+ sb +deviceIP.get(i));
			}
		}
	}//WIFI

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
				", deviceName=" + deviceName +
				", deviceIP=" + deviceIP +
				", localNodes=" + Arrays.toString(localNodes) +
				'}';
	}
}