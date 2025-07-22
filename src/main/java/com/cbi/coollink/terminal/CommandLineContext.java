package com.cbi.coollink.terminal;

import com.cbi.coollink.cli.repo.CliCommandPackage;
import com.cbi.coollink.net.protocol.ProgramNetworkInterface;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.util.Identifier;

public abstract class CommandLineContext {

    abstract public void executeCommand(String command, ProgramNetworkInterface networkInterface);
    abstract public WWidget getTextOutput();
    abstract public void tick();

    abstract public boolean commandExecuting();

    abstract public void installPackage(CliCommandPackage commands, boolean addToStorage);
    abstract public void unInstallPackage(Identifier packageId);

    abstract public void printCommands();

    abstract public void printHelpText(String[] args);
    abstract public void listPackages();
    abstract public void terminateRunningProgram();
}
