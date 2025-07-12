package com.cbi.coollink.terminal;

import com.cbi.coollink.cli.CliProgram;
import com.cbi.coollink.cli.CliProgramInit;
import com.cbi.coollink.cli.InternalCommands;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;


public class PhoneCommandLineContext extends CommandLineContext {

    private final HashMap<String, CliProgramInit> programRepository = new HashMap<>();
    private final HashMap<String, String> environmentVariables = new HashMap<>();
    private CliProgram currentExecutingProgram;



    public PhoneCommandLineContext(){
        textOut = new CommandTextOutputArea(375,130,100,"CBi Phone OS 1.0 (C) CBi-games 2025, All rights reserved");
        //set the initial environment variables
        environmentVariables.put("PWD","/");
        environmentVariables.put("ECHO","true");
        environmentVariables.put("PLATFORM","phone");
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        String playerUUID;
        String username;
        if(player == null){
            playerUUID = "";
            username = "";
        }else{
            playerUUID = player.getUuidAsString();
            username = player.getName().getString();
        }
        environmentVariables.put("USER", playerUUID);
        environmentVariables.put("USERNAME",username);

        //load the programs that can be executed from this command line
        programRepository.put("echo", InternalCommands.initOf(InternalCommands.ECHO,"Print the passed in arguments to the output"));
        programRepository.put("export", InternalCommands.initOf(InternalCommands.EXPORT,"Usage: export <var>=<value>\nSet an environment variable"));
        programRepository.put("env", InternalCommands.initOf(InternalCommands.ENV,"List all environment variables"));
        programRepository.put("cmds", InternalCommands.initOf((args, env, stdOut) -> printCommands(),"List all installed commands"));
        programRepository.put("help", InternalCommands.initOf((args, env, stdOut) -> printHelpText(args),"Usage: help <command>\nGet the help text for a given command"));

    }

    private final CommandTextOutputArea textOut;
    int tmcCnt = 0;
    @Override
    public void executeCommand(String command) {
        //check if the command should be echoed back to the user
        String echoValue = environmentVariables.get("ECHO");//get the value of the environment variable
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
                    String envVal = environmentVariables.get(inner.substring(1));
                    return Objects.requireNonNullElse(envVal, "");
                }
                return inner;
            }).toArray(String[]::new))
        ).toArray(String[]::new);
        //run the program

        currentExecutingProgram = initProgram.main(args, environmentVariables, textOut);

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

    public void printCommands(){
        String[] programs = programRepository.keySet().toArray(String[]::new);
        textOut.addLine("Installed commands:");
        for(String program:programs){
            textOut.addLine(program);
        }
    }

    public void printHelpText(String[] args){
        if(args.length < 1){
            textOut.addLine("Missing parameter command");
            return;
        }
        CliProgramInit commandInit = programRepository.get(args[0]);
        if(commandInit == null){
            textOut.addLine("Unknown command: "+args[0]);
            return;
        }
        String[] helpText = commandInit.helpText().split("\n");
        for(String help:helpText){
            textOut.addLine(help);
        }
    }

}
