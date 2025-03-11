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
import net.minecraft.nbt.NbtList;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

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
		this.nodeCache = new IWireNode[getNodeCount()];
		//setNode(0,1,pos,WireType.CAT6);
		//setNode(1,2,pos,WireType.COAX);
	}

	private static final int nodeCount = 3;
	private final boolean[] isNodeUsed = new boolean[nodeCount];

	public String password;
	public String ssid;
	public String netPass;

	private final LocalNode[] localNodes;
	private final IWireNode[] nodeCache;

	private static final int deviceID = 0x11;
	public Mac mac1,mac2;

	public ArrayList<String> connectedDevices=new ArrayList<>();
	public ArrayList<String> deviceName=new ArrayList<>();
	public ArrayList<String> deviceIP=new ArrayList<>();

	// Serialize the BlockEntity
	@Override
	public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		// Save the current value of the number to the nbt
		nbt.putInt("number", 89);
		nbt.putString("password", Objects.requireNonNullElse(password, "password123546"));
		nbt.putString("ssid", Objects.requireNonNullElse(ssid, "Unconfigured Network"));
		nbt.putString("Wireless_Password", Objects.requireNonNullElse(netPass, ""));
		nbt.putByteArray("MAC1",mac1.getBytes());
		nbt.putByteArray("MAC2",mac2.getBytes());
		NbtList nodeIDS = new NbtList();
		for(int i=0;i<nodeCount;i++){
			NbtCompound compound = new NbtCompound();
			if(localNodes[i]==null){
				nodeIDS.add(compound);
				continue;
			}

			localNodes[i].write(compound);
			nodeIDS.add(compound);
		}
		nbt.put("connections",nodeIDS);

		super.writeNbt(nbt,registryLookup);
	}

	// Deserialize the BlockEntity
	@Override
	public void readNbt(NbtCompound nbt,RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt,registryLookup);
		password=nbt.getString("password");
		ssid = nbt.getString("ssid");
		netPass = nbt.getString("Wireless_Password");
		byte[] mac1Bytes = nbt.getByteArray("MAC1");
		byte[] mac2Bytes = nbt.getByteArray("MAC2");
		setMacAddresses(mac1Bytes,mac2Bytes);
		NbtList nodeIDS = nbt.getList("connections",NbtCompound.COMPOUND_TYPE);
		for (int i=0;i<nodeCount;i++){
			NbtCompound compound = nodeIDS.getCompound(i);
			if(compound==null || compound.isEmpty()){
				isNodeUsed[i] = false;
				continue;
			}
			localNodes[i]=new LocalNode(this , compound);
			isNodeUsed[i] = true;
		}
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return createNbt(registryLookup);
	}

	@SuppressWarnings("unused")
	public static void tick(World world, BlockPos pos, BlockState state, AIOBlockEntity be) {
		//Main.LOGGER.info(be.password);
		be.createConnectedDevices();
	}
	public void updateStates(){
		if(world!=null) world.updateListeners(getPos(),getCachedState(),getCachedState(), Block.NOTIFY_LISTENERS);
	}

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
				//TODO: Call a function that assigns ip addresses to devices that don't have them
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
	}

	//TODO implement this
	public void setMacAddresses(byte[] mac1,byte[] mac2){
		this.mac1 = new Mac(mac1);
		this.mac2 = new Mac(mac2);
	}


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
	}

	@Override
	public IWireNode getWireNode(int index) {
		return this;
	}

	@Override
	public int getOtherNodeIndex(int index) {
		return localNodes[index].getOtherIndex();
	}

	@Override
	public @Nullable LocalNode getLocalNode(int index) {
		return localNodes[index];
	}

	@Override
	public void setNode(int index, int otherNode, BlockPos pos, WireType type) {
		this.localNodes[index] = new LocalNode(this, index, otherNode, type, pos);
		isNodeUsed[index]=true;
		markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
	}

	@Override
	public void removeNode(int index, boolean dropWire) {
		//LocalNode old = this.localNodes[index];
		this.localNodes[index] = null;
		this.nodeCache[index] = null;
		markDirty();
        assert world != null;
        world.updateListeners(getPos(), getCachedState(), getCachedState(), 0);
	}

	@Override
	public WireType getPortType(int index) {
		if(index <= 1) return WireType.CAT6;
		else return WireType.COAX;
	}

	@Override
	public boolean isNodeInUse(int index) {
		return isNodeUsed[index];
	}

	public void setIsNodeUsed(int index,boolean set){
		isNodeUsed[index]=set;
	}


	@Override
	public boolean hasConnection(int index) {
		//Main.LOGGER.info(index+"");
		return localNodes[index] != null;
	}

	@Override
	public int getNodeCount() {
		return nodeCount;
	}

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
				", nodeCache=" + Arrays.toString(nodeCache) +
				'}';
	}
}