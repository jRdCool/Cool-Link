package com.cbi.coollink.cli.repo;

import com.cbi.coollink.cli.CliProgramInit;
import net.minecraft.util.Identifier;

public class CliCommandPackage {



    public CliCommandPackage(Identifier packageID,String packageDescription,CommandInfo... commands){
        this.id = packageID;
        this.commands = commands;
        this.description = packageDescription;
    }

    private final Identifier id;
    private final CommandInfo[] commands;
    private final String description;

    public Identifier getId(){
        return id;
    }

    public int size(){
        return commands.length;
    }

    public String getCommandName(int index){
        return commands[index].commandName();
    }

    public CliProgramInit getProgram(int index){
        return commands[index].programInit();
    }

    public String getDescription() {
        return description;
    }

    public record CommandInfo(String commandName, CliProgramInit programInit){}
}
