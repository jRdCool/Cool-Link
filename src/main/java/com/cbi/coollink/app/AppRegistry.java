package com.cbi.coollink.app;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class AppRegistry {
    static ArrayList<AbstractPhoneApp> registerApps =new ArrayList<>();
    public static AbstractPhoneApp registerApp(AbstractPhoneApp app){
        registerApps.add(app);
        return app;
    }

    public static AbstractPhoneApp[] getApps(){
        return registerApps.toArray(new AbstractPhoneApp[0]);
    }

    @Nullable
    public  static AbstractPhoneApp get(Identifier id){
        for (AbstractPhoneApp registerApp : registerApps) {
            if(registerApp.appId.equals(id))
                return registerApp;
        }
        return null;
    }

}
