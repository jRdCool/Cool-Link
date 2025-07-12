package com.cbi.coollink.cli;

import com.cbi.coollink.Main;

public class InternalCommands {
    public static final BareCliProgram echo = (args, env, stdOut) -> {
        stdOut.addLine(String.join(" ",args));
    };


    public static CliProgramInit initOf(BareCliProgram program){
        return (args, env, stdOut) -> {
            program.init(args,env,stdOut);
            return program;
        };
    }
}
