package com.cbi.coollink.blocks;

import com.cbi.coollink.Main;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
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

	public void writeDataTo(NbtCompound nbt,String key, int value){//how to write NBT data from anywhere in the mod
		number = value;
		this.key=key;
		writeNbt(nbt);
		return;
	}

	public void readDataFrom(NbtCompound nbt,String key){//how to retrieve nbt data from else where in the mod
		this.key=key;
		readNbt(nbt);
		return;
	}

	// Serialize the BlockEntity
	@Override
	public void writeNbt(NbtCompound nbt) {
		// Save the current value of the number to the nbt
		nbt.putInt(key, number);

		super.writeNbt(nbt);
	}

	// Deserialize the BlockEntity
	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);

		number = nbt.getInt("number");
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
}