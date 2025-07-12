package com.cbi.coollink.app;

import com.cbi.coollink.terminal.CommandContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public final class AppRegistry {
    private static final ArrayList<Container> registeredApps =new ArrayList<>();
    private static final HashMap<Identifier,Container> registeredApps2 = new HashMap<>();
    public static void registerApp(Identifier appId,AppLauncher launcher, Identifier icon, Text description){
        //if(app instanceof AbstractRootApp)
        //    throw new RuntimeException("attempted to register app with root permissions to app registry");

        if(icon == null){//use the default icon if none was set
            icon = Identifier.of("minecraft","textures/misc/unknown_pack.png");
        }
        //check if the app was already registered
        Container app = new Container(appId,launcher,icon,description, (be)->false);
        registeredApps2.put(appId,app);
        registeredApps.add(app);
    }

    public static void registerApp(Identifier appId,AppLauncher launcher, Identifier icon, Text description, OpenOnBlockEntityCheck blockEntityCheck){
        //if(app instanceof AbstractRootApp)
        //    throw new RuntimeException("attempted to register app with root permissions to app registry");

        if(icon == null){//use the default icon if none was set
            icon = Identifier.of("minecraft","textures/misc/unknown_pack.png");
        }
        //check if the app was already registered
        Container app = new Container(appId,launcher,icon,description, blockEntityCheck);
        registeredApps2.put(appId,app);
        registeredApps.add(app);
    }

    public static Identifier[] getAppIcons(){
        return registeredApps.stream().map(Container::icon).toArray(Identifier[]::new);
    }

    public static Identifier[] getAppIds(){
        return registeredApps.stream().map(Container::appId).toArray(Identifier[]::new);
    }

    public static Text[] getAppDescriptions(){
        return registeredApps.stream().map(Container::description).toArray(Text[]::new);
    }

    public static Identifier getIcon(int index){
        return registeredApps.get(index).icon();
    }

    public static Identifier getId(int index){
        return registeredApps.get(index).appId();
    }

    public static Text getDescription(int index){
        return registeredApps.get(index).description();
    }

    public static AppLauncher getLauncher(int index){
        return registeredApps.get(index).launcher();
    }

    public static Identifier getIcon(Identifier id){
        Container container = registeredApps2.get(id);
        if(container == null){
            return null;
        }
        return container.icon();
    }

    public static Text getDescription(Identifier id){
        Container container = registeredApps2.get(id);
        if(container == null){
            return null;
        }
        return container.description();
    }

    public static AppLauncher getLauncher(Identifier id){
        Container container = registeredApps2.get(id);
        if(container == null){
            return null;
        }
        return container.launcher();
    }

    public static OpenOnBlockEntityCheck getOpensOnBlockEntity(Identifier id){
        Container container = registeredApps2.get(id);
        if(container == null){
            return null;
        }
        return container.blockEntityCheck;
    }

    public static int size(){
        return registeredApps.size();
    }

    public interface AppLauncher{
        AbstractPhoneApp launch(World world, @Nullable BlockEntity clickedOmBlockEntity, NbtCompound appData, CommandContext commandRunner);
    }

    /**Function to check if an app should open when the phone click on a block entity
     */
    public interface OpenOnBlockEntityCheck{
        /**Check if this app should open when the device is opened while clicking on a block entity
         * @param be The block entity that was clicked on
         * @return true If this app should open when the device clicks on a block entity of that type
         */
        boolean openOnBlockEntity(BlockEntity be);
    }
    private record Container(Identifier appId, AppLauncher launcher, Identifier icon, Text description, OpenOnBlockEntityCheck blockEntityCheck){}
}


