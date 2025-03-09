package com.cbi.coollink.blocks.cables.createadditons;


import com.cbi.coollink.Main;
import com.cbi.coollink.blocks.cables.CoaxCable;
import com.cbi.coollink.items.Cat6Cable;
import com.cbi.coollink.items.CoaxialCable;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodec;

//Modified from the create additions wireType class
public enum WireType {//IRON(4, 256, 87, 87, 87, CAItems.IRON_WIRE.asStack(4), CAItems.IRON_SPOOL.asStack());
	COAX(0, 256, 7, 7, 7),
	CAT6(1,256, 0,0,255),
	FIBER( 2,256,255,255,0)
	;

	private final int ID, TRANSFER, CR, CG, CB;
	//private final ItemStack DROP;
	//private final ItemStack SOURCE_DROP;

	WireType(int id, int transfer, int red, int green, int blue) {
		ID = id;
		TRANSFER = transfer;
		CR = red;
		CG = green;
		CB = blue;
		//DROP = drop;
		//SOURCE_DROP = source;
	}

	public static WireType fromIndex(int index) {
		return switch (index) {
			case 0 -> COAX;
			case 1 -> CAT6;
			case 2 -> FIBER;
			default -> null;
		};
	}

	public int getIndex() {return ID;}

	//public ItemStack getDrop() {return DROP.copy();}

	//public ItemStack getSourceDrop() {return SOURCE_DROP.copy();}

	public int transfer() {
		return TRANSFER;
	}

	public int getRed() {
		return CR;
	}

	public int getGreen() {
		return CG;
	}

	public int getBlue() {
		return CB;
	}


	public static WireType of(Item item) {
		if(item == Main.coaxialCableEntry.asItem())
			return WireType.COAX;
		if(item == Main.cat6CableEntry.asItem())
			return WireType.CAT6;
		//if(item == CoaxCable.ENTRY.asItem())
		//	return WireType.FIBER;
		return WireType.COAX;
	}

	@Override
	public String toString() {
		switch (this){
			case CAT6 -> {return "Cat 6 Ethernet";}
			case FIBER -> {return "Fiber Line";}
			default -> {return "Coax";}
		}
	}

	public static final PacketCodec<ByteBuf, WireType> PACKET_CODEC = new PacketCodec<ByteBuf, WireType>(){

		@Override
		public void encode(ByteBuf buf, WireType value) {
			buf.writeInt(value.getIndex());
		}

		@Override
		public WireType decode(ByteBuf buf) {
			return fromIndex(buf.readInt());
		}
	};
}
