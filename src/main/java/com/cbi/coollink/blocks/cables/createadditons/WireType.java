package com.cbi.coollink.blocks.cables.createadditons;


import com.cbi.coollink.items.Cat6Cable;
import com.cbi.coollink.items.CoaxialCable;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodec;

//Modified from the create additions wireType class

/**The type of wire connecting 2 things together
 */
public enum WireType {//IRON(4, 256, 87, 87, 87, CAItems.IRON_WIRE.asStack(4), CAItems.IRON_SPOOL.asStack());
	COAX(0, 256, 7, 7, 7),
	CAT6(1,100, 50,50,255),
	FIBER( 2,512,255,255,0),
	PROGRAMING(3,10,28,46,27),
	ANY(4,0,255,255,255)
	;

	private final int ID, TRANSMISSION_DISTANCE, CR, CG, CB;
	//private final ItemStack DROP;
	//private final ItemStack SOURCE_DROP;

	/**Define a wire type
	 * @param id The numerical id of this type
	 * @param transmissionDistance How far a signal can be sent down this type of wire (how many hops/conduits)
	 * @param red The red color value for rendering this type of wire
	 * @param green The green color value for rendering this type of wire
	 * @param blue The blue color value for rendering this type of wire
	 */
	WireType(int id, int transmissionDistance, int red, int green, int blue) {
		ID = id;
		TRANSMISSION_DISTANCE = transmissionDistance;
		CR = red;
		CG = green;
		CB = blue;
		//DROP = drop;
		//SOURCE_DROP = source;
	}

	/**Get the wire type represented by a given index
	 * @param index The index of the wire type
	 * @return The wire type represented by the given value or null if the index is invalid
	 */
	public static WireType fromIndex(int index) {
		return switch (index) {
			case 0 -> COAX;
			case 1 -> CAT6;
			case 2 -> FIBER;
			case 3 -> PROGRAMING;
			case 4 -> ANY;
			default -> null;
		};
	}

	/**Get the numerical index of this type
	 * @return The numerical id of this type
	 */
	public int getIndex() {return ID;}

	//public ItemStack getDrop() {return DROP.copy();}

	//public ItemStack getSourceDrop() {return SOURCE_DROP.copy();}

	/**Get how far a signal can be transmitted down this wire
	 * @return The number of hops/conduits a signal can go through while being transmitted down a wire of this type
	 */
	public int getTransmissionDistance() {
		return TRANSMISSION_DISTANCE;
	}

	/**Get the red color value for this wire
	 * @return The red value used to render this wire type
	 */
	public int getRed() {
		return CR;
	}

	/**Get the green color value for this wire
	 * @return The green value used to render this wire type
	 */
	public int getGreen() {
		return CG;
	}
	/**Get the blue color value for this wire
	 * @return The blue value used to render this wire type
	 */
	public int getBlue() {
		return CB;
	}


	/**Get the wire type of the given item
	 * @param item The item to check the wire type of
	 * @return The wire type represented by the given item or null if no type is represented
	 */
	public static WireType of(Item item) {
		if(item instanceof CoaxialCable) {
			return WireType.COAX;
		}
		if(item instanceof Cat6Cable) {
			return WireType.CAT6;
		}
		//if(item == CoaxCable.ENTRY.asItem())
		//	return WireType.FIBER;
		return null;
	}

	/**
	 * @return The string name/description of this cable type
	 */
	@Override
	public String toString() {
		switch (this){
			case CAT6 -> {return "Cat 6 Ethernet";}
			case FIBER -> {return "Fiber Line";}
			case COAX -> {return "Coax";}
			case PROGRAMING -> {return "Programing cable";}
			case ANY -> {return "Any cable type";}
			default -> {return "WireType: No fucking clew, how the hell did you even get this response? "+super.toString();}
		}
	}

	public static final PacketCodec<ByteBuf, WireType> PACKET_CODEC = new PacketCodec<>() {

        @Override
        public void encode(ByteBuf buf, WireType value) {
            buf.writeInt(value.getIndex());
        }

        @Override
        public WireType decode(ByteBuf buf) {
            return fromIndex(buf.readInt());
        }
    };

	public int rgb(){
		return getRed()<<16|getGreen()<<8|getBlue();
	}
}
