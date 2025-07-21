package com.cbi.coollink.cli.lowlevelnet;

import com.cbi.coollink.Util;
import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import com.cbi.coollink.terminal.CommandTextOutputArea;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;

import java.util.HashMap;

public class SendPackCommand implements CliProgram {

    public SendPackCommand(String[] args, HashMap<String,String> ignoredEnv, CommandTextOutputArea stdout, ProgramNetworkInterface networkInterface){
        if(args.length<2){
            stdout.addLine("Invalid args, expected sendpack <ip> <NBTdata>");
            return;
        }
        String destIp = args[0];
        if(!Util.validIp(destIp)){
            stdout.addLine("Invalid Ip");
            return;
        }

        if(!networkInterface.isConnectedToNetwork()){
            stdout.addLine("Not connected to network");
        }

        String[] nbtArgs = new String[args.length-1];
        System.arraycopy(args,1,nbtArgs,0,nbtArgs.length);
        String rawStringNbt = String.join(" ",nbtArgs);
        NbtCompound packetNbt;
        try {
            packetNbt = StringNbtReader.readCompound(rawStringNbt);
        } catch (CommandSyntaxException e) {
            stdout.addLine("§cError parsing NBT:§r "+e.getMessage());
            return;
        }
        networkInterface.sendRawData(destIp,packetNbt);
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
        return false;
    }
}
