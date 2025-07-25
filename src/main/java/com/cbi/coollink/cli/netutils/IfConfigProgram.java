package com.cbi.coollink.cli.netutils;

import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import com.cbi.coollink.terminal.CommandTextOutputArea;

import java.util.HashMap;

public class IfConfigProgram implements CliProgram {


    public IfConfigProgram(String[] args, HashMap<String,String> ignoredEnv, CommandTextOutputArea stdout, ProgramNetworkInterface networkInterface) {
        stdout.addLine("Your ip address is: "+networkInterface.getDeviceIp());
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
