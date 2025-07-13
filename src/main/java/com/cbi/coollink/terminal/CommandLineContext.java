package com.cbi.coollink.terminal;

import com.cbi.coollink.cli.repo.CliCommandPackage;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import net.minecraft.util.Identifier;

public abstract class CommandLineContext {

    abstract public void executeCommand(String command);
    abstract public WWidget getTextOutput();
    abstract public void tick();

    @SuppressWarnings("all")
    abstract public boolean commandExecuting();

    abstract public void installPackage(CliCommandPackage commands, boolean addToStorage);
    abstract public void unInstallPackage(Identifier packageId);

    abstract public void printCommands();

    abstract public void printHelpText(String[] args);
    abstract public void listPackages();
}
