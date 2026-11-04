package com.cbi.coollink.app;


import io.github.cottonmc.cotton.gui.widget.*;
import net.minecraft.network.chat.Component;

public class AIODeviceList extends WPlainPanel {
    WButton device;


    public AIODeviceList() {
        device = new WButton(Component.literal("name"));
        this.add(device,5,5,150,20);

        this.setSize(130,180);



    }
}
