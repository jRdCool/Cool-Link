package com.cbi.coollink.cli.lowlevelnet;

import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import com.cbi.coollink.terminal.CommandTextOutputArea;

import java.util.HashMap;

public class ReceivePacketCommand implements CliProgram {

    boolean responseFound = false;
    boolean noLimit = false;

    public ReceivePacketCommand(String[] args, HashMap<String,String> ignoredEnv, CommandTextOutputArea stdout, ProgramNetworkInterface networkInterface) {
        if (networkInterface.isConnectedToNetwork()) {
            //arg parsing
            if(args.length > 0){
                if(args[0].equals("nolimit")){
                    noLimit = true;
                }else{
                    stdout.addLine("Unknown argument: "+args[0]);
                    responseFound = true;
                    return;
                }
            }
            stdout.addLine("Waiting for any packet on all ports");
            networkInterface.setOnPacketReceived(receivedData -> {
                stdout.addLine("Received data from: " + receivedData.getSourceIpAddress() + " " + receivedData.getData());
                if(!noLimit) {
                    responseFound = true;
                }
            });
        } else {
            responseFound = true;
            stdout.addLine("Not connected to network");
        }
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
        return !responseFound;
    }
}
