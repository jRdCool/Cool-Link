package com.cbi.coollink.terminal;

import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.cli.CliProgramInit;
import com.cbi.coollink.cli.InternalCommands;
import io.github.cottonmc.cotton.gui.widget.WWidget;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


public class PhoneCommandLineContext extends CommandLineContext {

    private final HashMap<String, CliProgramInit> programRepository = new HashMap<>();
    private final HashMap<String, String> enviormentVariables = new HashMap<>();
    private CliProgram currentExecutingProgram;



    public PhoneCommandLineContext(){
        textOut = new CommandTextOutputArea(375,130,100,"CBi Phone OS 1.0 (C) CBi-games 2025, All rights reserved");
        //set the initial environment variables
        enviormentVariables.put("PWD","/");
        enviormentVariables.put("ECHO","true");

        //load the programs that can be executed from this command line
        programRepository.put("echo", InternalCommands.initOf(InternalCommands.ECHO));
        programRepository.put("export", InternalCommands.initOf(InternalCommands.EXPORT));
        programRepository.put("env", InternalCommands.initOf(InternalCommands.ENV));
    }

    private final CommandTextOutputArea textOut;
    int tmcCnt = 0;
    @Override
    public void executeCommand(String command) {
        //check if the command should be echoed back to the user
        String echoValue = enviormentVariables.get("ECHO");//get the value of the environment variable
        boolean echo = true;
        if(echoValue!=null){
            echo = echoValue.equalsIgnoreCase("true");//if it is ture or not set then set echoing to true
        }
        if(echo){//if command echoing is enabled then echo the command back to the user
            textOut.addLine("> "+command);
        }
        //parse the command into an application and a list of args
        String[] argsAndP = command.split("\"?( |$)(?=(([^\"]*\"){2})*[^\"]*$)\"?");//split the command string into separate args or 1 arg if a quoted sting
        String program = argsAndP[0];//the program will always be the first arg
        String[] args = new String[argsAndP.length-1];//remove the first arg as it is not necessary in a program
        System.arraycopy(argsAndP,1, args ,0, argsAndP.length-1);
        //get the program to run
        CliProgramInit initProgram = programRepository.get(program);
        if(initProgram == null){//if there is no program with that name
            textOut.addLine("'"+program+"' is not recognized as an internal or external command, or operable program");//error
            return;
        }
        //resolve environment variables
        args = Arrays.stream(args).map(in ->
            String.join(" ",Arrays.stream(in.split(" ")).map( inner -> {
                if(inner.startsWith("$")){
                    String envVal = enviormentVariables.get(inner.substring(1));
                    return Objects.requireNonNullElse(envVal, "");
                }
                return inner;
            }).toArray(String[]::new))
        ).toArray(String[]::new);
        //run the program

        currentExecutingProgram = initProgram.main(args,enviormentVariables, textOut);

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
