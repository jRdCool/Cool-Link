package com.cbi.coollink.cli.lowlevelnet;

import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import com.cbi.coollink.terminal.CommandTextOutputArea;

import java.util.HashMap;

public class ReceivePacketCommand implements CliProgram {

    boolean responseFound = false;

    public ReceivePacketCommand(String[] args, HashMap<String,String> ignoredEnv, CommandTextOutputArea stdout, ProgramNetworkInterface networkInterface) {
        if (networkInterface.isConnectedToNetwork()) {
            stdout.addLine("Waiting for any packet on all ports");
            networkInterface.setOnPacketReceived(receivedData -> {
                stdout.addLine("Received data from: " + receivedData.getSourceIpAddress() + " " + receivedData.getData());
                responseFound = true;
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
