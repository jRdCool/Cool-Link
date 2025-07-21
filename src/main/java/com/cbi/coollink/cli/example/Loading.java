package com.cbi.coollink.cli.example;

import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import com.cbi.coollink.terminal.CommandTextOutputArea;

import java.util.HashMap;

public class Loading implements CliProgram {

    private final CommandTextOutputArea stdout;
    private int counter = 40;

    public Loading(String[] ignoredArgs, HashMap<String,String> ignoredEnv, CommandTextOutputArea stdout, ProgramNetworkInterface networkInterface){
        this.stdout = stdout;
    }

    @Override
    public void tick() {
        counter --;
        switch (counter){
            case 30 -> stdout.addLine("25%");
            case 20 -> stdout.addLine("50%");
            case 10 -> stdout.addLine("75%");
            case 0 -> stdout.addLine("100%");
        }
    }

    @Override
    public boolean isProgramRunning() {
        return counter > 0;
    }
}
