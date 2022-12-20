package com.cbi.coollink.blocks;

import com.cbi.coollink.Main;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

// Made with Blockbench 4.5.2
// Exported for Minecraft version 1.17+ for Yarn
// Paste this class into your mod and generate all required imports
public class AIOBlockEntity extends BlockEntity {
	public AIOBlockEntity(BlockPos pos, BlockState state) {
		super(Main.AIO_BLOCK_ENTITY, pos, state);
	}

	private int number = 0;
	private String key;
	public String password;
	public String ssid;
	public String netPass;

	// Serialize the BlockEntity
	@Override
	public void writeNbt(NbtCompound nbt) {
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
		super.writeNbt(nbt);
	}

	// Deserialize the BlockEntity
	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		password=nbt.getString("password");
		number = nbt.getInt("number");
		ssid = nbt.getString("ssid");
		netPass = nbt.getString("Wireless_password");
	}

	@Nullable
	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}

	public static void tick(World world, BlockPos pos, BlockState state, AIOBlockEntity be) {
		//Main.LOGGER.info(be.password);
	}
	public void updateStates(){
		world.updateListeners(getPos(),getCachedState(),getCachedState(), Block.NOTIFY_LISTENERS);
	}
}