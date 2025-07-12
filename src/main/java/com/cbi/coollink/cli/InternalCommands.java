package com.cbi.coollink.cli;

import com.cbi.coollink.terminal.CommandTextOutputArea;

import java.util.HashMap;

public class InternalCommands {

    /**Repeat the users args back to them on the output
     */
    public static final BareCliProgram ECHO = (args, env, stdOut) -> {
        stdOut.addLine(String.join(" ",args));
    };

    /**Set an environment variable
     */
    public static final BareCliProgram EXPORT = (args, env, stdOut) -> {
        if(args.length==0){
            stdOut.addLine("Error: No arguments provided");
            return;
        }
        String[] var = args[0].split("=",2);
        if(var.length < 2){
            stdOut.addLine("Error: No assignment found");
            return;
        }
        env.put(var[0],var[1]);
    };

    /**List all current environment variables
     */
    public static final BareCliProgram ENV = (args, env, stdOut) -> {
        String[] keys = env.keySet().toArray(new String[0]);
        for(String key: keys){
            stdOut.addLine(key+"="+env.get(key));
        }
    };


    public static CliProgramInit initOf(BareCliProgram program, String helpText){
        return new CliProgramInit(){
            @Override
            public CliProgram main(String[] args, HashMap<String, String> env, CommandTextOutputArea stdOut) {
                program.init(args,env,stdOut);
                return program;
            }
            @Override
            public String helpText() {
                return helpText;
            }
        };
    }
}
