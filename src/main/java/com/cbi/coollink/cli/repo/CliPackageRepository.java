package com.cbi.coollink.cli.repo;

import com.cbi.coollink.Main;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;

public class CliPackageRepository {
    public static final String PHONE_ENVIRONMENT = "phone";
    public static final String ANY_ENVIRONMENT = "any";
    public static final String COMPUTER_ENVIRONMENT = "computer";
    public static final String SERVER_ENVIRONMENT = "server";


    private static final HashMap<Identifier, CliCommandPackage> anyPackages = new HashMap<>();
    private static final HashMap<Identifier, CliCommandPackage> phonePackages = new HashMap<>();
    private static final HashMap<Identifier, CliCommandPackage> computerPackages = new HashMap<>();
    private static final HashMap<Identifier, CliCommandPackage> serverPackages = new HashMap<>();


    public static CliCommandPackage getPackage(Identifier id, String deviceEnvironment){
        //get the program from the correct environment repo
        CliCommandPackage commandPackage = switch (deviceEnvironment){
            case PHONE_ENVIRONMENT -> phonePackages.get(id);
            case COMPUTER_ENVIRONMENT -> computerPackages.get(id);
            case SERVER_ENVIRONMENT -> serverPackages.get(id);
            case null, default -> null;
        };

        if(commandPackage == null){//if the correct repo didn't have it then check the any repo
            commandPackage = anyPackages.get(id);
        }

        return commandPackage;
    }

    public static void registerPackage(CliCommandPackage commandPackage, String programEnvironment){
        switch (programEnvironment){
            case PHONE_ENVIRONMENT -> phonePackages.put(commandPackage.getId(),commandPackage);
            case COMPUTER_ENVIRONMENT -> computerPackages.put(commandPackage.getId(),commandPackage);
            case SERVER_ENVIRONMENT -> serverPackages.put(commandPackage.getId(),commandPackage);
            case ANY_ENVIRONMENT -> anyPackages.put(commandPackage.getId(),commandPackage);
            case null, default -> {
                Main.LOGGER.error("Unknown program environment "+programEnvironment+", package not registered",new RuntimeException());
            }
        }
    }

    /**Find any packages in the appropriate repos that contain the search string in their id
     * @param searchString The content to search the repo for
     * @param programEnvironment The environments to search in
     * @return An array containing all the packages that match the search
     */
    public static CliCommandPackage[] searchPackages(String searchString,String programEnvironment){
        ArrayList<CliCommandPackage> found = new ArrayList<>();
        HashMap<Identifier, CliCommandPackage> repo = switch (programEnvironment){
            case PHONE_ENVIRONMENT -> phonePackages;
            case COMPUTER_ENVIRONMENT -> computerPackages;
            case SERVER_ENVIRONMENT -> serverPackages;
            case null, default -> null;
        };
        if(repo != null){
            Identifier[] keys = repo.keySet().toArray(new Identifier[0]);
            for(Identifier key:keys){
                if(key.toString().contains(searchString)){
                    found.add(repo.get(key));
                }
            }
        }
        Identifier[] keys = anyPackages.keySet().toArray(new Identifier[0]);
        for(Identifier key:keys){
            if(key.toString().contains(searchString)){
                found.add(anyPackages.get(key));
            }
        }
        return found.toArray(CliCommandPackage[]::new);
    }
}
