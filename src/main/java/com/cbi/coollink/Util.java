package com.cbi.coollink;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;

public class Util {

    public static <T> PacketCodec<ByteBuf, T[]> arrayPacketCodec(PacketCodec<ByteBuf,T> codec){
        return new PacketCodec<ByteBuf, T[]>() {
            @Override
            public T[] decode(ByteBuf buf) {
                int arrSize = buf.readInt();
                @SuppressWarnings("unchecked")
                T[] arr = (T[])new Object[arrSize];
                for(int i=0;i<arr.length;i++){
                    arr[i] = codec.decode(buf);
                }
                return arr;
            }

            @Override
            public void encode(ByteBuf buf, T[] value) {
                buf.writeInt(value.length);
                for(T thing:value){
                    codec.encode(buf,thing);
                }
            }
        };
    }
}
