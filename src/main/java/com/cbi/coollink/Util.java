package com.cbi.coollink;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.function.IntFunction;

public class Util {

    public static <T> PacketCodec<ByteBuf, T[]> arrayPacketCodec(PacketCodec<ByteBuf,T> codec, IntFunction<T[]> arrayFactory){
        return new PacketCodec<ByteBuf, T[]>() {
            @Override
            public T[] decode(ByteBuf buf) {
                int arrSize = buf.readInt();
                ArrayList<T> list = new ArrayList<>(arrSize);
//                @SuppressWarnings("unchecked")
//                T[] arr = (T[])new Object[arrSize];
                for(int i=0;i<arrSize;i++){
                    list.add(codec.decode(buf));
                }
                return list.toArray(arrayFactory);
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

    private static int countShell(int r) {
        if (r == 0) return 1;
        int outer = (2 * r + 1) * (2 * r + 1) * (2 * r + 1);
        int inner = (2 * (r - 1) + 1);
        inner = inner * inner * inner;
        return outer - inner;
    }

    public static BlockPos getCubePos(int index,BlockPos center){
        //vibe coding, i am so ashamed
        if (index < 0) throw new IllegalArgumentException("Index must be non-negative.");

        int shell = 0;
        int total = 0;

        // Step 1: Find which shell the index belongs to
        while (true) {
            int shellCount = countShell(shell);
            if (index < total + shellCount) break;
            total += shellCount;
            shell++;
        }

        int offset = index - total;

        // Step 2: Iterate over positions in the shell and return the one at offset
        int count = 0;
        for (int x = -shell; x <= shell; x++) {
            for (int y = -shell; y <= shell; y++) {
                for (int z = -shell; z <= shell; z++) {
                    if (Math.max(Math.abs(x), Math.max(Math.abs(y), Math.abs(z))) != shell) continue;

                    if (count == offset) {
                        return new BlockPos(x +center.getX(), y+center.getY(), z+center.getZ());
                    }
                    count++;
                }
            }
        }

        throw new IllegalStateException("Index out of bounds after shell computation");
    }

    public static String parseIpGetIp(String rawIp){
        String[] ipart = rawIp.split(":");
        return ipart[0];
    }

    public static int parseIpGetPort(String rawIp){
        String[] ipart = rawIp.split(":");
        if(ipart.length == 1){
            return -1;
        }
        if(ipart.length>=2){
            return Integer.parseInt(ipart[1]);
        }
        return -1;
    }

    public static boolean validIp(String rawIp){
        rawIp = parseIpGetIp(rawIp);
        String[] pts = rawIp.split("\\.");
        if(pts.length!=4){
            return false;
        }
        for (String pt : pts) {
            int pv = Integer.parseInt(pt);
            if (pv < 0 || pv > 255) {
                return false;
            }
        }
        return true;
    }
}
