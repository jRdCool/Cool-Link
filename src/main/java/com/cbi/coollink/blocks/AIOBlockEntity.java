package com.cbi.coollink.blocks;

import com.cbi.coollink.Main;
import com.cbi.coollink.net.protocol.Mac;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class AIOBlockEntity extends BlockEntity {
	public AIOBlockEntity(BlockPos pos, BlockState state) {
		super(Main.AIO_BLOCK_ENTITY, pos, state);
		String currentThread = Thread.currentThread().getName();
		//if(currentThread.equals("Server thread")) {
			mac1 = new Mac(deviceID);
			mac2 = new Mac(deviceID);
		//}
	}

	private int number = 0;
	private String key;
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
		nbt.putString("MAC1",mac1.toString());
		nbt.putString("MAC2",mac2.toString());
		super.writeNbt(nbt,registryLookup);
	}

	// Deserialize the BlockEntity
	@Override
	public void readNbt(NbtCompound nbt,RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt,registryLookup);
		password=nbt.getString("password");
		number = nbt.getInt("number");
		ssid = nbt.getString("ssid");
		netPass = nbt.getString("Wireless_Password");
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
				Main.LOGGER.info("!!!!! !!!ERROR!!! MORE IPs THAN DEVICES !!!!!");
			}
		}
		else {
			for (int i=0;i<deviceName.size();i++)
			{
				String space = "  ";
				for(int j=16-deviceName.get(i).length();j>0;j--)
				{
					space += " ";
				}
				connectedDevices.set(i,deviceName.get(i)+space+deviceIP.get(i));
			}
		}
	}

}