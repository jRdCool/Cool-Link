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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class AIOBlockEntity extends BlockEntity implements IWireNode {
	public AIOBlockEntity(BlockPos pos, BlockState state) {
		super(Main.AIO_BLOCK_ENTITY, pos, state);
		String currentThread = Thread.currentThread().getName();
		//if(currentThread.equals("Server thread")) {
			mac1 = new Mac(deviceID);
			mac2 = new Mac(deviceID);
		//}
		this.localNodes = new LocalNode[getNodeCount()];
		this.nodeCache = new IWireNode[getNodeCount()];
	}

	public String password;
	public String ssid;
	public String netPass;

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
		//Main.LOGGER.info("password is: "+password);
		if(password==null){
			nbt.putString("password","password123546");
			//Main.LOGGER.info("saving password as: password123546" );
		}else {
			nbt.putString("password", password);
			//Main.LOGGER.info("saving password as: " + password);
		}
		if(ssid==null){
			nbt.putString("ssid","Unconfigured Network");
		}
		else{
			nbt.putString("ssid", ssid);
		}
		if(netPass==null){
			nbt.putString("Wireless_Password","");
		}
		else{
			nbt.putString("Wireless_Password", netPass);
		}
		nbt.putByteArray("MAC1",mac1.getBytes());
		nbt.putByteArray("MAC2",mac2.getBytes());
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

	public static void tick(World world, BlockPos pos, BlockState state, AIOBlockEntity be) {
		//Main.LOGGER.info(be.password);
		be.createConnectedDevices();
	}
	public void updateStates(){
		world.updateListeners(getPos(),getCachedState(),getCachedState(), Block.NOTIFY_LISTENERS);
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
				//call a function that assigns ip addresses to devices that don't have them
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

	private final LocalNode[] localNodes;
	private final IWireNode[] nodeCache;
	@Override
	public Vec3d getNodeOffset(int node) {
		if(node == 0 ){
			return new Vec3d(1,1,1);
		}
		if(node == 1 ){
			return new Vec3d(0,1,0);
		}

		return null;
	}

	@Override
	public IWireNode getWireNode(int index) {
		return null;
	}

	@Override
	public int getOtherNodeIndex(int index) {
		return 0;
	}

	@Override
	public @Nullable LocalNode getLocalNode(int index) {
		return null;
	}

	@Override
	public void setNode(int index, int other, BlockPos pos, WireType type) {
		this.localNodes[index] = new LocalNode(this, index, other, type, pos);


	}

	@Override
	public void removeNode(int index, boolean dropWire) {
		LocalNode old = this.localNodes[index];
		this.localNodes[index] = null;
		this.nodeCache[index] = null;

	}
}