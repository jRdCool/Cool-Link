package com.cbi.coollink.cli;

import com.cbi.coollink.cli.repo.CliCommandPackage;
import com.cbi.coollink.cli.repo.CliPackageRepository;
import com.cbi.coollink.terminal.CommandLineContext;
import com.cbi.coollink.terminal.CommandTextOutputArea;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class PackageManager implements CliProgram{

    private boolean running = true;
    private final String programEnv;
    private final CommandLineContext cli;

    public PackageManager(String[] args, HashMap<String,String> env, CommandTextOutputArea stdOut, CommandLineContext cli){
        programEnv = env.get("PLATFORM");
        this.cli=cli;
        if(args.length<1){
            stdOut.addLine("Missing arguments");
            running = false;
            return;
        }
        String subCommand = args[0];
        switch(subCommand){
            case "install" -> installPath(args,stdOut);
            case "uninstall" -> uninstallPath(args,stdOut);
            case "search" -> searchPath(args,stdOut);
            case "list" -> listPath();
            case null, default -> {
                stdOut.addLine("Unknown sub command: "+subCommand);
                running = false;
            }
        }
    }

    /**
     * Process ongoing program operations
     */
    @Override
    public void tick() {

    }

    /**
     * Get if this program is still running
     *
     * @return true if this program is still running on the tick method
     */
    @Override
    public boolean isProgramRunning() {
        return running;//currently not very useful but if we wanted to make this interesting/animated in the future then
    }

    void installPath(String[] args, CommandTextOutputArea stdOut){
        if(isOffline()){
            return;
        }
        if(args.length < 2) {
            running = false;
            stdOut.addLine("Missing parameter: package id");
            return;
        }
        Identifier packageId;
        try{
            packageId = Identifier.of(args[1]);
        }catch (RuntimeException e){
            stdOut.addLine("Invalid Id: "+args[1]);
            running = false;
            return;
        }
        CliCommandPackage commandPackage = CliPackageRepository.getPackage(packageId,programEnv);
        if(commandPackage == null){
            stdOut.addLine("No package found with the id: "+packageId);
            running = false;
            return;
        }
        //install the package on the cli
        cli.installPackage(commandPackage,true);
        running = false;
    }
    void searchPath(String[] args, CommandTextOutputArea stdOut){
        if(isOffline()){
            return;
        }
        if(args.length < 2) {
            running = false;
            stdOut.addLine("Missing parameter: search string");
            return;
        }
        CliCommandPackage[] packages = CliPackageRepository.searchPackages(args[1],programEnv);
        if(packages.length == 0){
            stdOut.addLine("No packages found");
        }else{
            for(CliCommandPackage commands: packages){
                stdOut.addLine(commands.getId().toString());
                String[] description = commands.getDescription().split("\n");
                for(String line: description){
                    stdOut.addLine(line);
                }
            }
        }
        running = false;
    }

    void uninstallPath(String[] args, CommandTextOutputArea stdOut){
        if(args.length < 2) {
            running = false;
            stdOut.addLine("Missing parameter: package id");
            return;
        }
        Identifier packageId;
        try{
            packageId = Identifier.of(args[1]);
        }catch (RuntimeException e){
            stdOut.addLine("Invalid Id: "+args[1]);
            running = false;
            return;
        }
        cli.unInstallPackage(packageId);
        running = false;
    }

    void listPath(){
        cli.listPackages();
        running = false;
    }

    boolean isOffline(){
        return false;
    }
}
