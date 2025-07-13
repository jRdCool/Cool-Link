package com.cbi.coollink.terminal;

import io.github.cottonmc.cotton.gui.widget.WWidget;

public abstract class CommandLineContext {

    abstract public void executeCommand(String command);
    abstract public WWidget getTextOutput();
    abstract public void tick();

    @SuppressWarnings("all")
    abstract public boolean commandExecuting();
}
