package com.cbi.coollink.terminal;

import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.cli.CliProgramInit;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.util.HashMap;


public class PhoneCommandLineContext extends CommandLineContext {

    private final HashMap<String, CliProgramInit> programRepository = new HashMap<>();
    private final HashMap<String, String> enviormentVariables = new HashMap<>();
    private CliProgram currentExecutingProgram;



    public PhoneCommandLineContext(){
        textOut = new CommandTextOutputArea(375,130,100,"CBi Phone OS 1.0 (C) CBi-games 2025, All rights reserved");
        enviormentVariables.put("PWD","/");
        enviormentVariables.put("ECHO","true");
    }

    private final CommandTextOutputArea textOut;
    int tmcCnt = 0;
    @Override
    public void executeCommand(String command) {
        //parse the command into an application and a list of args
        String echoValue = enviormentVariables.get("ECHO");
        boolean echo = true;
        if(echoValue!=null){
            echo = echoValue.equalsIgnoreCase("true");
        }
        if(echo){
            textOut.addLine("> "+command);
        }
        String[] argsAndP = command.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");
        String program = argsAndP[0];
        String[] args = new String[argsAndP.length-1];
        System.arraycopy(argsAndP,1, args ,0, argsAndP.length-1);
        CliProgramInit initProgram = programRepository.get(program);
        if(initProgram == null){
            textOut.addLine("'"+program+"' is not recognized as an internal or external command, or operable program");
        }

    }

    @Override
    public WWidget getTextOutput() {
        return textOut.getWidget();
    }

    @Override
    public void tick() {
        if(currentExecutingProgram != null && currentExecutingProgram.isProgramRunning()){
            currentExecutingProgram.tick();
        }else if(currentExecutingProgram != null && !currentExecutingProgram.isProgramRunning()){
            currentExecutingProgram = null;
        }
    }

    @Override
    public boolean commandExecuting() {
        return currentExecutingProgram != null && currentExecutingProgram.isProgramRunning();
    }

}
