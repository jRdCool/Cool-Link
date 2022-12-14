package com.cbi.coollink;

import com.cbi.coollink.app.AIOSettingApp;
import com.cbi.coollink.app.AppRegistry;
import com.cbi.coollink.app.ExampleApp;
import net.fabricmc.api.ClientModInitializer;

public class ClientEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        //register client things here
        AppRegistry.registerApp(new ExampleApp());
    }
}
