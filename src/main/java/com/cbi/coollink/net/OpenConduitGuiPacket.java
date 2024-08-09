package com.cbi.coollink.net;

//import net.fabricmc.fabric.api.networking.v1.FabricPacket;
//import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record OpenConduitGuiPacket() /*implements FabricPacket */{
   // public static final PacketType<OpenConduitGuiPacket> TYPE = PacketType.create(Identifier.of("cool-link", "open-conduit-gui"), OpenConduitGuiPacket::new);

    public OpenConduitGuiPacket(PacketByteBuf buf){
        this();
    }

    //@Override
    public void write(PacketByteBuf buf) {

    }
/*
   // @Override
    public PacketType<?> getType() {
        return TYPE;
    }*/
}
