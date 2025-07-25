package com.cbi.coollink.cli.netutils;

import com.cbi.coollink.Main;
import com.cbi.coollink.Util;
import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import com.cbi.coollink.terminal.CommandTextOutputArea;
import net.minecraft.nbt.NbtCompound;

import java.util.HashMap;

public class PingProgram implements CliProgram {

    boolean pingged = false;
    int timer = 0;
    CommandTextOutputArea stdout;

    public PingProgram(String[] args, HashMap<String,String> ignoredEnv, CommandTextOutputArea stdout, ProgramNetworkInterface networkInterface) {
        this.stdout = stdout;

        if(args.length < 1){
            stdout.addLine("missing ip address");
            pingged =true;
            return;
        }

        if(!Util.validIp(args[0])){
            stdout.addLine("Invalid Ip Address");
            pingged =true;
            return;
        }
        String ip = args[0];
        NbtCompound pingCompound = new NbtCompound();
        pingCompound.putString("type","ping");
        Main.LOGGER.info(pingCompound+"");
        networkInterface.sendRawData(ip,pingCompound,context -> {
            if(!context.getData().getData().getString("type","notPong").equals("pong")){
                stdout.addLine("Received invalid response");
                pingged = true;
                return;
            }
            stdout.addLine("Reply from "+ip+": "+timer+" ticks");
            pingged = true;
        });

    }

    /**
     * Process ongoing program operations
     */
    @Override
    public void tick() {
        timer++;
        if(timer == 400){
            stdout.addLine("Response timed out");
        }
    }

    /**
     * Get if this program is still running
     *
     * @return true if this program is still running on the tick method
     */
    @Override
    public boolean isProgramRunning() {
        return !pingged && timer < 400;
    }
}
