package com.cbi.coollink.cli.lowlevelnet;

import com.cbi.coollink.Util;
import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import com.cbi.coollink.terminal.CommandTextOutputArea;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.util.HashMap;

public class SendPackAndWaitCommand implements CliProgram {

    boolean response = false;

    public SendPackAndWaitCommand(String[] args, HashMap<String,String> ignoredEnv, CommandTextOutputArea stdout, ProgramNetworkInterface networkInterface){
        if(args.length<2){
            stdout.addLine("Invalid args, expected sendpack <ip> <NBTdata>");
            response = true;
            return;
        }
        String destIp = args[0];
        if(!Util.validIp(destIp)){
            stdout.addLine("Invalid Ip");
            response = true;
            return;
        }

        if(!networkInterface.isConnectedToNetwork()){
            stdout.addLine("Not connected to network");
        }

        String[] nbtArgs = new String[args.length-1];
        System.arraycopy(args,1,nbtArgs,0,nbtArgs.length);
        String rawStringNbt = String.join(" ",nbtArgs);
        CompoundTag packetNbt;
        try {
            packetNbt = NbtIo.read(Util.stringDataInput(rawStringNbt));
        } catch (IOException e) {
            throw new RuntimeException(e);
            stdout.addLine("§cError parsing NBT:§r "+e.getMessage());
            response = true;
            return;
        }
        networkInterface.sendRawData(destIp,packetNbt, context -> {
            stdout.addLine("Received response from: " + context.getData().getSourceIpAddress() + " " + context.getData().getData());
            response = true;
        });
    }

    /**
     * Process ongoing program operations
     */
    @Override
    public void tick() {

    }

    /**
     * Get if this program is still running
     *
     * @return true if this program is still running on the tick method
     */
    @Override
    public boolean isProgramRunning() {
        return !response;
    }
}
