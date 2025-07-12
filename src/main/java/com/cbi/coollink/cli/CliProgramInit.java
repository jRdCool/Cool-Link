package com.cbi.coollink.cli;

import com.cbi.coollink.terminal.CommandTextOutputArea;

import java.util.HashMap;

public interface CliProgramInit {
    CliProgram main(String[] args, HashMap<String,String> env, CommandTextOutputArea stdOut);
}
