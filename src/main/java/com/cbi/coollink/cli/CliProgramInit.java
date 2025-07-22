package com.cbi.coollink.cli;

import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import com.cbi.coollink.terminal.CommandTextOutputArea;

import java.util.HashMap;

public interface CliProgramInit {
    CliProgram main(String[] args, HashMap<String,String> env, CommandTextOutputArea stdOut, ProgramNetworkInterface networkInterface);

    /**Get the help text for this program
     * @return the help text of the given program
     */
    default String helpText(){
      return "No help text provided";
    }

    /**Create an init program for a given program with the given help text
     * @param program The init for the given program, (use MyProgram::new for this)
     * @param helpText The help text to print when the help command is run for this program
     * @return The initialization program for the given program with help text included
     */
    static CliProgramInit of(CliProgramInit program, String helpText){
        return new CliProgramInit(){
            @Override
            public CliProgram main(String[] args, HashMap<String, String> env, CommandTextOutputArea stdOut,ProgramNetworkInterface networkInterface) {
                return program.main(args,env,stdOut,networkInterface);
            }
            @Override
            public String helpText() {
                return helpText;
            }
        };
    }
}
