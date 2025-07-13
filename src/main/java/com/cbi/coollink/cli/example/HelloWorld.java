package com.cbi.coollink.cli.example;

import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.terminal.CommandTextOutputArea;

import java.util.HashMap;

public class HelloWorld implements CliProgram {
    //programs are registered to packages in your mods mod initializer method (see com.cbi.coollink.Main)

    public HelloWorld(String[] ignoredArgs, HashMap<String,String> ignoredEnv, CommandTextOutputArea stdout){
        stdout.addLine("Hello world");
    }

    @Override
    public void tick() {

    }

    @Override
    public boolean isProgramRunning() {
        return false;
    }
}
