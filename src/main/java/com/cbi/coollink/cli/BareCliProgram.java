package com.cbi.coollink.cli;

import com.cbi.coollink.terminal.CommandTextOutputArea;

import java.util.HashMap;

/**A simple frame for a CLI program intended to be implemented as a lambda that does all its processing on initialization
 */
public interface BareCliProgram extends CliProgram{

    void init(String[] args, HashMap<String,String> env, CommandTextOutputArea stdOut);

    @Override
    default boolean isProgramRunning(){
      return false;
    };

    @Override
    default void tick(){

    }
}
